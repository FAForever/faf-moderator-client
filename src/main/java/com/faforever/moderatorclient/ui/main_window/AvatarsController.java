package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.Avatar;
import com.faforever.commons.api.dto.AvatarAssignment;
import com.faforever.moderatorclient.api.domain.AvatarService;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class AvatarsController implements Controller<SplitPane> {
    private final AvatarService avatarService;

    public SplitPane root;
    public TableView<Avatar> avatarTableView;
    public TableView<AvatarAssignment> avatarAssignmentTableView;
    public RadioButton showAllAvatarsRadioButton;
    public RadioButton searchAvatarsByIdRadioButton;
    public RadioButton searchAvatarsByTooltipRadioButton;
    public RadioButton searchAvatarsByAssignedUserRadioButton;
    public TextField searchAvatarsTextField;

    public AvatarsController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildAvatarTableView(avatarTableView);
        ViewHelper.buildAvatarAssignmentTableView(avatarAssignmentTableView);

        avatarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            avatarAssignmentTableView.getItems().clear();
            Optional.ofNullable(newValue).ifPresent(avatar -> avatarAssignmentTableView.getItems().addAll(avatar.getAssignments()));
        });
    }

    public void refresh() {
        avatarTableView.getItems().clear();
        avatarTableView.getSortOrder().clear();
        avatarTableView.getItems().addAll(
                avatarService.getAll()
        );
    }

    public void onSearchAvatars() {
        avatarTableView.getItems().clear();
        avatarTableView.getSortOrder().clear();
        Collection<Avatar> avatars;
        String pattern = searchAvatarsTextField.getText();

        if (searchAvatarsByIdRadioButton.isSelected()) {
            avatars = avatarService.findAvatarsById(pattern);
        } else if (searchAvatarsByTooltipRadioButton.isSelected()) {
            avatars = avatarService.findAvatarsByTooltip(pattern);
        } else if (searchAvatarsByAssignedUserRadioButton.isSelected()) {
            avatars = avatarService.findAvatarsByAssignedUser(pattern);
        } else {
            avatars = avatarService.getAll();
        }
        avatarTableView.getItems().addAll(avatars);
    }
}
