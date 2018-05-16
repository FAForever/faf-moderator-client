package com.faforever.moderatorclient.ui.main_window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.faforever.commons.api.dto.ApiException;
import com.faforever.commons.api.dto.Avatar;
import com.faforever.moderatorclient.api.domain.AvatarService;
import com.faforever.moderatorclient.mapstruct.AvatarMapper;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.AvatarAssignmentFX;
import com.faforever.moderatorclient.ui.domain.AvatarFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AvatarsController implements Controller<SplitPane> {
    private final AvatarService avatarService;
    private final AvatarMapper avatarMapper;

    public TableView<AvatarFX> avatarTableView;
    public TableView<AvatarAssignmentFX> avatarAssignmentTableView;

    public SplitPane root;
    public RadioButton showAllAvatarsRadioButton;
    public RadioButton searchAvatarsByIdRadioButton;
    public RadioButton searchAvatarsByTooltipRadioButton;
    public RadioButton searchAvatarsByAssignedUserRadioButton;
    public TextField searchAvatarsTextField;
    public TextField avatarNameTextField;
    public ImageView avatarImageView;
    public Button saveButton;
    public Button deleteButton;

    private ObservableList<AvatarFX> avatars;
    private ObservableList<AvatarAssignmentFX> avatarAssignments;
    private File avatarImageFile;

    public AvatarsController(AvatarService avatarService, AvatarMapper avatarMapper) {
        this.avatarService = avatarService;
        this.avatarMapper = avatarMapper;

        avatars = FXCollections.observableArrayList();
        avatarAssignments = FXCollections.observableArrayList();
    }

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildAvatarTableView(avatarTableView, avatars);
        ViewHelper.buildAvatarAssignmentTableView(avatarAssignmentTableView, avatarAssignments);
        deleteButton.setVisible(false);

        avatarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            avatarAssignments.clear();
            avatarImageFile = null;
            if (newValue != null) {
                avatarAssignments.addAll(newValue.getAssignments());
                avatarNameTextField.setText(newValue.getTooltip());
                avatarImageView.setImage(new Image(newValue.getUrl()));
                saveButton.setText("Update");
                deleteButton.setVisible(true);
            } else {
                avatarAssignments.clear();
                avatarNameTextField.setText(null);
                avatarImageView.setImage(null);
                saveButton.setText("Save");
                deleteButton.setVisible(false);
            }
        });
    }

    public void refresh() {
        avatars.clear();
        avatars.addAll(avatarMapper.map(executeApiCall(avatarService::getAll).orElse(Collections.emptyList())));

        avatarTableView.getSortOrder().clear();
    }

    public void onSearchAvatars() {
        avatars.clear();
        avatarTableView.getSortOrder().clear();

        List<Avatar> avatarSearchResult;
        String pattern = searchAvatarsTextField.getText();

        if (searchAvatarsByIdRadioButton.isSelected()) {
            avatarSearchResult = avatarService.findAvatarsById(pattern);
        } else if (searchAvatarsByTooltipRadioButton.isSelected()) {
            avatarSearchResult = avatarService.findAvatarsByTooltip(pattern);
        } else if (searchAvatarsByAssignedUserRadioButton.isSelected()) {
            avatarSearchResult = avatarService.findAvatarsByAssignedUser(pattern);
        } else {
            avatarSearchResult = avatarService.getAll();
        }
        avatars.addAll(avatarMapper.map(avatarSearchResult));
    }

    public void chooseAvatarImage() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        File selectedFile = fileChooser.showOpenDialog(getRoot().getScene().getWindow());
        if (selectedFile != null) {
            avatarImageFile = selectedFile;
            avatarImageView.setImage(new Image(new FileInputStream(avatarImageFile)));
        }
    }

    public void saveAvatar() {
        final Avatar avatar = avatarMapper.map(avatarTableView.getSelectionModel().getSelectedItem());
        if (avatar != null) {
            final boolean avatarUpdateConfirmed = ViewHelper.confirmDialog("Update Avatar", "Do you really want to update an avatar?");
            if (!avatarUpdateConfirmed) {
                return;
            }
            executeApiCall(() -> avatarService.updateAvatar(avatar.getId(), avatarNameTextField.getText(), avatarImageFile));
        } else {
            executeApiCall(() -> avatarService.createAvatar(avatarNameTextField.getText(), avatarImageFile));
        }
        refresh();
    }

    public void deleteAvatar() {
        final boolean avatarDeletionConfirmed = ViewHelper.confirmDialog("Delete Avatar", "Do you really want to delete an avatar?");
        if (!avatarDeletionConfirmed) {
            return;
        }
        final Avatar avatar = avatarMapper.map(avatarTableView.getSelectionModel().getSelectedItem());
        if (avatar != null) {
            executeApiCall(() -> avatarService.deleteAvatar(avatar.getId()));
            refresh();
        }
    }

    @SneakyThrows
    private <T> Optional<T> executeApiCall(Callable<T> apiCall) {
        try {
            return Optional.ofNullable(apiCall.call());
        } catch (ApiException e) {
            ViewHelper.errorDialog(e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    @SneakyThrows
    private void executeApiCall(Runnable apiCall) {
        try {
            apiCall.run();
        } catch (ApiException e) {
            ViewHelper.errorDialog(e.getLocalizedMessage());
        }
    }
}
