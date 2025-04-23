package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.Avatar;
import com.faforever.moderatorclient.api.domain.AvatarService;
import com.faforever.moderatorclient.mapstruct.AvatarMapper;
import com.faforever.moderatorclient.ui.AvatarInfoController;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.LoadingStateManager;
import com.faforever.moderatorclient.ui.UiService;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.AvatarAssignmentFX;
import com.faforever.moderatorclient.ui.domain.AvatarFX;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarsController implements Controller<SplitPane> {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UiService uiService;
    private final AvatarService avatarService;
    private final AvatarMapper avatarMapper;
    private final ObservableList<AvatarFX> avatars = FXCollections.observableArrayList();
    private final ObservableList<AvatarAssignmentFX> avatarAssignments = FXCollections.observableArrayList();
    private final LoadingStateManager loadingStateManager = new LoadingStateManager();

    public TableView<AvatarFX> avatarTableView;
    public TableView<AvatarAssignmentFX> avatarAssignmentTableView;
    public SplitPane root;
    public RadioButton showAllAvatarsRadioButton;
    public RadioButton searchAvatarsByIdRadioButton;
    public RadioButton searchAvatarsByTooltipRadioButton;
    public RadioButton searchAvatarsByAssignedUserRadioButton;
    public TextField searchAvatarsTextField;
    public Button searchAvatarsButton;

    public Button editAvatarButton;
    public Button deleteAvatarButton;

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildAvatarTableView(avatarTableView, avatars);
        ViewHelper.buildAvatarAssignmentTableView(avatarAssignmentTableView, avatarAssignments, this::removeAvatarFromPlayer);

        editAvatarButton.disableProperty().bind(avatarTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteAvatarButton.disableProperty().bind(avatarTableView.getSelectionModel().selectedItemProperty().isNull());

        avatarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            avatarAssignments.clear();
            Optional.ofNullable(newValue).ifPresent(avatar -> avatarAssignments.addAll(avatar.getAssignments()));

            if (newValue != null) {
                applicationEventPublisher.publishEvent(newValue);
            }
        });

        loadingStateManager
                .add(avatarTableView)
                .add(searchAvatarsButton);
    }

    public Thread asyncLoad(Supplier<List<Avatar>> loadingFunc) {
        avatars.clear();
        loadingStateManager.applyLoadingState();

        return Thread.startVirtualThread(() -> {
            List<AvatarFX> loadedAvatars = avatarMapper.map(loadingFunc.get());
            Platform.runLater(() -> {
                loadingStateManager.revertLoadingState();
                avatars.addAll(loadedAvatars);
                avatarTableView.getSortOrder().clear();
            });
        });
    }

    private void removeAvatarFromPlayer(AvatarAssignmentFX avatarAssignment) {
        Assert.notNull(avatarAssignment, "You need to select a user's avatar.");
        avatarAssignment.getAvatar().getAssignments().remove(avatarAssignment);
        avatarAssignments.remove(avatarAssignment);

        Thread.startVirtualThread(() -> {
            avatarService.removeAvatarAssignment(avatarAssignment);
        });
    }

    public void refresh() {
        refresh(null);
    }

    public void refresh(@Nullable Runnable postLoad) {
        Thread loadingThread = asyncLoad(avatarService::getAll);

        if (postLoad != null) {
            Thread.startVirtualThread(() -> {
                try {
                    loadingThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                postLoad.run();
            });

        }
    }

    private void openAvatarDialog(AvatarFX avatarFX, boolean isNew) {
        AvatarInfoController avatarInfoController = uiService.loadFxml("ui/avatarInfo.fxml");
        avatarInfoController.setAvatar(avatarFX);

        Stage avatarInfoDialog = new Stage();
        avatarInfoDialog.setTitle(isNew ? "Add new avatar" : "Edit avatar");
        avatarInfoDialog.setScene(new Scene(avatarInfoController.getRoot()));
        avatarInfoDialog.showAndWait();
        refresh();
    }

    public void onSearchAvatars() {
        avatars.clear();
        avatarTableView.getSortOrder().clear();

        Supplier<List<Avatar>> avatarSearch;
        String pattern = searchAvatarsTextField.getText();

        if (searchAvatarsByIdRadioButton.isSelected()) {
            avatarSearch = () -> avatarService.findAvatarsById(pattern);
        } else if (searchAvatarsByTooltipRadioButton.isSelected()) {
            avatarSearch = () -> avatarService.findAvatarsByTooltip(pattern);
        } else if (searchAvatarsByAssignedUserRadioButton.isSelected()) {
            avatarSearch = () -> avatarService.findAvatarsByAssignedUser(pattern);
        } else {
            avatarSearch = avatarService::getAll;
        }
        asyncLoad(avatarSearch);
    }

    public void onAddAvatar() {
        openAvatarDialog(new AvatarFX(), true);
    }

    public void onEditAvatar() {
        AvatarFX avatarFX = avatarTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(avatarFX, "You need to select an avatar first.");

        openAvatarDialog(avatarFX, false);
    }

    public void onDeleteAvatar() {
        AvatarFX avatarFX = avatarTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(avatarFX, "You need to select an avatar first.");

        if (avatarFX.getAssignments().isEmpty()) {
            boolean confirmed = ViewHelper.confirmDialog("Delete avatar " + avatarFX.getTooltip(), "Are you sure that you want to delete this avatar?");

            if (confirmed) {
                avatarService.deleteAvatar(avatarFX.getId());
                avatars.remove(avatarFX);
            }
        } else {
            ViewHelper.errorDialog("Deleting avatar failed", "You can't remove an avatar as long as it has assignments.");
        }
    }
}
