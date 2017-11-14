package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.dto.*;
import com.faforever.moderatorclient.api.rest.domain.AvatarService;
import com.faforever.moderatorclient.api.rest.domain.MapService;
import com.faforever.moderatorclient.api.rest.domain.UserService;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
public class MainController implements Controller<TabPane> {
    private final UiService uiService;
    private final UserService userService;
    private final MapService mapService;
    private final AvatarService avatarService;
    public TabPane root;

    // Tab "User Management"
    public RadioButton searchUserByIdRadioButton;
    public RadioButton searchUserByCurrentNameRadioButton;
    public RadioButton searchUserByPreviousNamesRadioButton;
    public RadioButton searchUserByEmailRadioButton;
    public RadioButton searchUserBySteamIdRadioButton;
    public TextField userSearchTextField;
    public Button newBanButton;
    public Button editBanButton;
    public TableView<Player> userSearchTableView;
    public TableView<NameRecord> userNameHistoryTableView;
    public TableView<BanInfo> userBansTableView;
    public TableView<Teamkill> userTeamkillsTableView;
    public TableView<AvatarAssignment> userAvatarsTableView;

    // Tab "Ladder Map Pool"
    public TreeTableView<MapTableItemAdapter> ladderPoolView;
    public TreeTableView<MapTableItemAdapter> mapVaultView;
    public CheckBox filterByMapNameCheckBox;
    public TextField mapNamePatternTextField;
    public ImageView ladderPoolImageView;
    public ImageView mapVaultImageView;
    public Button removeFromPoolButton;
    public Button addToPoolButton;

    // Tab "Avatars"
    public TableView<Avatar> avatarTableView;
    public TableView<AvatarAssignment> avatarAssignmentTableView;
    public RadioButton showAllAvatarsRadioButton;
    public RadioButton searchAvatarsByIdRadioButton;
    public RadioButton searchAvatarsByTooltipRadioButton;
    public RadioButton searchAvatarsByAssignedUserRadioButton;
    public TextField searchAvatarsTextField;

    // Tab "Recent activity"
    public TableView<Player> userRegistrationFeedTableView;
    public TableView<Teamkill> teamkillFeedTableView;

    public MainController(UiService uiService, UserService userService, MapService mapSearchService, AvatarService avatarService) {
        this.uiService = uiService;
        this.userService = userService;
        this.mapService = mapSearchService;
        this.avatarService = avatarService;
    }

    @FXML
    public void initialize() {
        initUserManagementTab();
        initLadderMapPoolTab();
        initAvatarTab();
        initRecentActivityTab();
    }

    private void initRecentActivityTab() {
        ViewHelper.buildUserTableView(userRegistrationFeedTableView);
        ViewHelper.buildTeamkillTableView(teamkillFeedTableView, true);
    }

    private void initAvatarTab() {
        ViewHelper.buildAvatarTableView(avatarTableView);
        ViewHelper.buildAvatarAssignmentTableView(avatarAssignmentTableView);

        avatarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            avatarAssignmentTableView.getItems().clear();
            Optional.ofNullable(newValue).ifPresent(avatar -> avatarAssignmentTableView.getItems().addAll(avatar.getAssignments()));
        });
    }

    public void refreshAvatars() {
        avatarTableView.getItems().clear();
        avatarTableView.getItems().addAll(
                avatarService.getAll()
        );
    }

    private void initLadderMapPoolTab() {
        ViewHelper.buildMapTreeView(ladderPoolView);
        ViewHelper.bindMapTreeViewToImageView(ladderPoolView, ladderPoolImageView);

        ladderPoolView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                removeFromPoolButton.setDisable(true);
            } else {
                removeFromPoolButton.setDisable(!newValue.getValue().isMapVersion());
            }
        });

        ViewHelper.buildMapTreeView(mapVaultView);
        ViewHelper.bindMapTreeViewToImageView(mapVaultView, mapVaultImageView);
        mapVaultView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                addToPoolButton.setDisable(true);
            } else {
                addToPoolButton.setDisable(!newValue.getValue().isMapVersion());
            }
        });
    }

    private void initUserManagementTab() {
        ViewHelper.buildUserTableView(userSearchTableView);
        ViewHelper.buildNameHistoryTableView(userNameHistoryTableView);
        ViewHelper.buildBanTableView(userBansTableView);
        ViewHelper.buildTeamkillTableView(userTeamkillsTableView, false);
        ViewHelper.buildUserAvatarsTableView(userAvatarsTableView);

        userSearchTableView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedUser);
        userBansTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editBanButton.setDisable(newValue == null);
        });
    }

    private void onSelectedUser(ObservableValue<? extends Player> observable, Player oldValue, Player newValue) {
        userNameHistoryTableView.getItems().clear();
        userBansTableView.getItems().clear();
        userTeamkillsTableView.getItems().clear();
        userAvatarsTableView.getItems().clear();

        if (newValue != null) {
            userNameHistoryTableView.getItems().addAll(newValue.getNames());
            userBansTableView.getItems().addAll(newValue.getBans());
            userTeamkillsTableView.getItems().addAll(userService.findTeamkillsByUserId(newValue.getId()));
            userAvatarsTableView.getItems().addAll(newValue.getAvatarAssignments());
        }

        newBanButton.setDisable(newValue == null);
    }

    public void onUserSearch() {
        userSearchTableView.getItems().clear();

        Collection<Player> usersFound = Collections.emptyList();
        String searchPattern = userSearchTextField.getText();
        if (searchUserByIdRadioButton.isSelected()) {
            usersFound = userService.findUserById(searchPattern);
        } else if (searchUserByCurrentNameRadioButton.isSelected()) {
            usersFound = userService.findUserByName(searchPattern);
        } else if (searchUserByPreviousNamesRadioButton.isSelected()) {
            usersFound = userService.findUsersByPreviousName(searchPattern);
        } else if (searchUserByEmailRadioButton.isSelected()) {
            usersFound = userService.findUserByEmail(searchPattern);
        } else if (searchUserBySteamIdRadioButton.isSelected()) {
            usersFound = userService.findUserBySteamId(searchPattern);
        }

        userSearchTableView.getItems().addAll(usersFound);
    }

    public void display() {
        LoginController loginController = uiService.loadFxml("login.fxml");

        Stage loginDialog = new Stage();
        loginDialog.setOnCloseRequest(event -> System.exit(0));
        loginDialog.setAlwaysOnTop(true);
        loginDialog.setTitle("FAF Moderator Client");
        loginDialog.setScene(new Scene(loginController.getRoot()));
        loginDialog.initStyle(StageStyle.UTILITY);
        loginDialog.showAndWait();

        refreshLadderPool();
        refreshAvatars();
        refreshRecentActivity();
    }

    private void refreshRecentActivity() {
        userRegistrationFeedTableView.getItems().clear();
        userRegistrationFeedTableView.getItems().addAll(userService.findLatestRegistrations());

        teamkillFeedTableView.getItems().clear();
        teamkillFeedTableView.getItems().addAll(userService.findLatestTeamkills());
    }

    private void refreshLadderPool() {
        ladderPoolView.getRoot().getChildren().clear();
        mapService.findMapsInLadder1v1Pool()
                .forEach(map -> {
                    TreeItem<MapTableItemAdapter> mapItem = new TreeItem<>(new MapTableItemAdapter(map));
                    ladderPoolView.getRoot().getChildren().add(mapItem);

                    map.getVersions().forEach(mapVersion -> mapItem.getChildren().add(new TreeItem<>(new MapTableItemAdapter(mapVersion))));
                });
    }

    @Override
    public TabPane getRoot() {
        return root;
    }

    public void onRemoveFromLadderPool() {
        MapTableItemAdapter itemAdapter = ladderPoolView.getSelectionModel().getSelectedItem().getValue();
        Assert.isTrue(itemAdapter.isMapVersion(), "Only map version can be removed");

        mapService.removeMapVersionFromLadderPool(itemAdapter.getMapVersion().getLadder1v1Map().getId());
        refreshLadderPool();
    }

    public void onAddToLadderPool() {
        MapTableItemAdapter itemAdapter = mapVaultView.getSelectionModel().getSelectedItem().getValue();
        Assert.isTrue(itemAdapter.isMapVersion(), "Only map version can be added");

        mapService.addMapVersionToLadderPool(itemAdapter.getId());
        refreshLadderPool();
    }

    public void onSearchMapVault() {
        mapVaultView.getRoot().getChildren().clear();
        String mapNamePattern = null;

        if (filterByMapNameCheckBox.isSelected()) {
            mapNamePattern = mapNamePatternTextField.getText();
        }

        ViewHelper.fillMapTreeView(mapVaultView,
                mapService.findMaps(mapNamePattern).stream());
    }

    public void onSearchAvatars() {
        avatarTableView.getItems().clear();
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

    public void onNewBan() {
        Player selectedPlayer = userSearchTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedPlayer, "Tou need to select a player to create a ban.");

        BanInfoController banInfoController = uiService.loadFxml("banInfo.fxml");
        banInfoController.setBanInfo(new BanInfo()
                .setPlayer(selectedPlayer)
        );

        Stage banInfoDialog = new Stage();
        banInfoDialog.setTitle("Apply new ban");
        banInfoDialog.setScene(new Scene(banInfoController.getRoot()));
        banInfoDialog.showAndWait();
    }

    public void onEditBan() {
        BanInfo selectedBan = userBansTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedBan, "You need to select a ban to edit it.");

        BanInfoController banInfoController = uiService.loadFxml("banInfo.fxml");
        banInfoController.setBanInfo(selectedBan);

        Stage banInfoDialog = new Stage();
        banInfoDialog.setTitle("Edit Ban");
        banInfoDialog.setScene(new Scene(banInfoController.getRoot()));
        banInfoDialog.showAndWait();
    }
}