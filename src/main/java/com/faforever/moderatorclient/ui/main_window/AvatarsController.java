package com.faforever.moderatorclient.ui.main_window;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    private ObservableList<AvatarFX> avatars;
    private ObservableList<AvatarAssignmentFX> avatarAssignments;

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

        avatarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            avatarAssignments.clear();
            Optional.ofNullable(newValue).ifPresent(avatar -> avatarAssignments.addAll(avatar.getAssignments()));
        });
    }

    public void refresh() {
        avatars.clear();
        avatars.addAll(avatarMapper.map(avatarService.getAll()));

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
}
