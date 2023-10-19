package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.Avatar;
import com.faforever.moderatorclient.api.domain.AvatarService;
import com.faforever.moderatorclient.mapstruct.AvatarMapper;
import com.faforever.moderatorclient.ui.AvatarInfoController;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.UiService;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.AvatarAssignmentFX;
import com.faforever.moderatorclient.ui.domain.AvatarFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

    public TableView<AvatarFX> avatarTableView;
    public TableView<AvatarAssignmentFX> avatarAssignmentTableView;
    public SplitPane root;
    public RadioButton showAllAvatarsRadioButton;
    public RadioButton searchAvatarsByIdRadioButton;
    public RadioButton searchAvatarsByTooltipRadioButton;
    public RadioButton searchAvatarsByAssignedUserRadioButton;
    public TextField searchAvatarsTextField;

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
            Optional.ofNullable(newValue).ifPresent(avatar -> {
                avatarAssignments.addAll(avatar.getAssignments());
            });

            if (newValue != null) {
                applicationEventPublisher.publishEvent(newValue);
            }
        });
    }

    private void removeAvatarFromPlayer(AvatarAssignmentFX a) {
        AvatarAssignmentFX avatarAssignmentFX = a;
        Assert.notNull(avatarAssignmentFX, "You need to select a user's avatar.");

        avatarService.removeAvatarAssignment(avatarAssignmentFX);
        avatarAssignmentTableView.getItems().remove(avatarAssignmentFX);
        avatarAssignmentFX.getPlayer().getAvatarAssignments().remove(avatarAssignmentFX);
        avatarAssignmentFX.getAvatar().setAssignments(avatarAssignmentTableView.getItems());
        avatarAssignmentTableView.refresh();
        refresh();
        Optional.ofNullable(a.getAvatar()).ifPresent(avatar -> avatarAssignments.addAll(a.getAvatar().getAssignments()));
    }

    public void refresh() {
        System.out.println("refeshing");
        avatars.clear();
        avatarAssignments.clear();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                avatars.addAll(avatarMapper.map(avatarService.getAll().get()));

                avatarTableView.getSortOrder().clear();
                return null;

            }
        };
        new Thread(task).start();
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

    public void onSearchAvatars() throws ExecutionException, InterruptedException {
        avatars.clear();
        avatarTableView.getSortOrder().clear();

        List<Avatar> avatarSearchResult;
        String pattern = searchAvatarsTextField.getText();

        if (searchAvatarsByIdRadioButton.isSelected()) {
            avatarSearchResult = avatarService.findAvatarsById(pattern);
        } else if (searchAvatarsByTooltipRadioButton.isSelected()) {
            avatarSearchResult = avatarService.findAvatarsByTooltip(pattern);
        } else if (searchAvatarsByAssignedUserRadioButton.isSelected()) {
            avatarSearchResult = avatarService.findAvatarsByAssignedUser(pattern).get();
        } else {
            avatarSearchResult = avatarService.getAll().get();
        }
        avatars.addAll(avatarMapper.map(avatarSearchResult));
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
            boolean confirmed = ViewHelper.confirmDialog("Delete avatar " + avatarFX.getTooltip(),
                    "Are you sure that you want to delete this avatar?");

            if (confirmed) {
                avatarService.deleteAvatar(avatarFX.getId());
                avatars.remove(avatarFX);
            }
        } else {
            ViewHelper.errorDialog("Deleting avatar failed", "You can't remove an avatar as long as it has assignments.");
        }
    }
}
