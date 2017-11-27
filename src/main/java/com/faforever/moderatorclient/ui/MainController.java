package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.dto.*;
import com.faforever.moderatorclient.api.rest.domain.AvatarService;
import com.faforever.moderatorclient.api.rest.domain.MapService;
import com.faforever.moderatorclient.api.rest.domain.UserService;
import com.faforever.moderatorclient.mapstruct.MapMapper;
import com.faforever.moderatorclient.mapstruct.MapVersionMapper;
import com.faforever.moderatorclient.mapstruct.PlayerMapper;
import com.faforever.moderatorclient.ui.domain.FeaturedModFX;
import com.faforever.moderatorclient.ui.domain.GamePlayerStatsFX;
import com.faforever.moderatorclient.ui.domain.MapFX;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class MainController implements Controller<TabPane> {
    private final MapMapper mapMapper;
    private final MapVersionMapper mapVersionMapper;
    private final PlayerMapper playerMapper;

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
    public RadioButton searchUserByIpRadioButton;
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

    // Tab "Map vault"
    public RadioButton searchMapByMapNameRadioButton;
    public RadioButton searchMapByAuthorIdRadioButton;
    public RadioButton searchMapByAuthorNameRadioButton;
    public TextField mapSearchTextField;
    public CheckBox excludeHiddenMapVersionsCheckbox;
    public ImageView mapVersionPreviewImageView;
    public TableView<MapFX> mapSearchTableView;
    public TableView<MapVersionFX> mapVersionTableView;
    public Button toggleMapVersionHidingButton;
    public Button toggleMapVersionRatingButton;

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
    public TableView<GamePlayerStatsFX> userLastGamesTable;
    public ChoiceBox<FeaturedModFX> featuredModFilterChoiceBox;
    public Button loadMoreGamesButton;
    private Runnable loadMoreGamesRunnable;
    private int page = 1;
    private final String replayDownLoadFormat;
    private final PlatformService platformService;

    public MainController(MapMapper mapMapper, MapVersionMapper mapVersionMapper, PlayerMapper playerMapper, UiService uiService, UserService userService, MapService mapSearchService, AvatarService avatarService, @Value("${faforever.vault.replayDownloadUrlFormat}") String replayDownLoadFormat, PlatformService platformService) {
        this.mapMapper = mapMapper;
        this.mapVersionMapper = mapVersionMapper;
        this.playerMapper = playerMapper;
        this.uiService = uiService;
        this.userService = userService;
        this.mapService = mapSearchService;
        this.avatarService = avatarService;
        this.replayDownLoadFormat = replayDownLoadFormat;
        this.platformService = platformService;
    }

    @Override
    public TabPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        initUserManagementTab();
        initLadderMapPoolTab();
        initMapVaultTab();
        initAvatarTab();
        initRecentActivityTab();
    }

    private void initUserManagementTab() {
        ViewHelper.buildUserTableView(userSearchTableView);
        ViewHelper.buildNameHistoryTableView(userNameHistoryTableView);
        ViewHelper.buildBanTableView(userBansTableView);
        ViewHelper.buildPlayersGamesTable(userLastGamesTable, replayDownLoadFormat, platformService);

        loadMoreGamesButton.visibleProperty()
                .bind(Bindings.createBooleanBinding(() -> userLastGamesTable.getItems().size() != 0 && userLastGamesTable.getItems().size() % 100 == 0, userLastGamesTable.getItems()));

        featuredModFilterChoiceBox.setConverter(new StringConverter<FeaturedModFX>() {
            @Override
            public String toString(FeaturedModFX object) {
                return object == null ? "All" : object.getDisplayName();
            }

            @Override
            public FeaturedModFX fromString(String string) {
                throw (new UnsupportedOperationException("Not implemented"));
            }
        });
        featuredModFilterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            userLastGamesTable.getItems().clear();
            page = 1;
            if (loadMoreGamesRunnable != null) loadMoreGamesRunnable.run();
        });

        featuredModFilterChoiceBox.getItems().add(null);
        featuredModFilterChoiceBox.getSelectionModel().select(0);
        CompletableFuture.supplyAsync(userService::getFeaturedMods)
                .thenAccept(featuredMods -> Platform.runLater(() -> featuredModFilterChoiceBox.getItems().addAll(featuredMods)));

        ViewHelper.buildTeamkillTableView(userTeamkillsTableView, false);
        ViewHelper.buildUserAvatarsTableView(userAvatarsTableView);

        userSearchTableView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedUser);
        userBansTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editBanButton.setDisable(newValue == null);
        });
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

    private void initMapVaultTab() {
        ViewHelper.buildMapTableView(mapSearchTableView);
        ViewHelper.buildMapVersionTableView(mapVersionTableView);

        mapSearchTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mapVersionTableView.getItems().clear();
            Optional.ofNullable(newValue).ifPresent(map -> mapVersionTableView.getItems().addAll(
                    map.getVersions()));
        });

        mapVersionTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                mapVersionPreviewImageView.setImage(null);
                toggleMapVersionHidingButton.setDisable(true);
                toggleMapVersionRatingButton.setDisable(true);
            } else {
                mapVersionPreviewImageView.setImage(new Image(newValue.getThumbnailUrlLarge().toString()));
                toggleMapVersionHidingButton.setDisable(false);
                toggleMapVersionRatingButton.setDisable(false);
            }
        });
    }

    private void initAvatarTab() {
        ViewHelper.buildAvatarTableView(avatarTableView);
        ViewHelper.buildAvatarAssignmentTableView(avatarAssignmentTableView);

        avatarTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            avatarAssignmentTableView.getItems().clear();
            Optional.ofNullable(newValue).ifPresent(avatar -> avatarAssignmentTableView.getItems().addAll(avatar.getAssignments()));
        });
    }

    private void initRecentActivityTab() {
        ViewHelper.buildUserTableView(userRegistrationFeedTableView);
        ViewHelper.buildTeamkillTableView(teamkillFeedTableView, true);
    }

    public void display() {
        LoginController loginController = uiService.loadFxml("ui/login.fxml");

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

    private void refreshLadderPool() {
        ladderPoolView.getRoot().getChildren().clear();
        ladderPoolView.getSortOrder().clear();
        mapService.findMapsInLadder1v1Pool()
                .forEach(map -> {
                    TreeItem<MapTableItemAdapter> mapItem = new TreeItem<>(new MapTableItemAdapter(map));
                    ladderPoolView.getRoot().getChildren().add(mapItem);

                    map.getVersions().forEach(mapVersion -> mapItem.getChildren().add(new TreeItem<>(new MapTableItemAdapter(mapVersion))));
                });
    }

    private void refreshAvatars() {
        avatarTableView.getItems().clear();
        avatarTableView.getSortOrder().clear();
        avatarTableView.getItems().addAll(
                avatarService.getAll()
        );
    }

    public void refreshRecentActivity() {
        userRegistrationFeedTableView.getItems().clear();
        userRegistrationFeedTableView.getSortOrder().clear();
        userRegistrationFeedTableView.getItems().addAll(userService.findLatestRegistrations());

        teamkillFeedTableView.getItems().clear();
        userRegistrationFeedTableView.getSortOrder().clear();
        teamkillFeedTableView.getItems().addAll(userService.findLatestTeamkills());
    }

    private void onSelectedUser(ObservableValue<? extends Player> observable, Player oldValue, Player newValue) {
        userNameHistoryTableView.getItems().clear();
        userNameHistoryTableView.getSortOrder().clear();
        userBansTableView.getItems().clear();
        userBansTableView.getSortOrder().clear();
        userTeamkillsTableView.getItems().clear();
        userTeamkillsTableView.getSortOrder().clear();
        userAvatarsTableView.getItems().clear();
        userAvatarsTableView.getSortOrder().clear();
        userLastGamesTable.getItems().clear();
        userLastGamesTable.getSortOrder().clear();

        if (newValue != null) {
            userNameHistoryTableView.getItems().addAll(newValue.getNames());
            userBansTableView.getItems().addAll(newValue.getBans());
            userTeamkillsTableView.getItems().addAll(userService.findTeamkillsByUserId(newValue.getId()));
            userAvatarsTableView.getItems().addAll(newValue.getAvatarAssignments());

            page = 1;
            loadMoreGamesRunnable = () -> CompletableFuture.supplyAsync(() -> userService.getLastHunderedPlayedGamesByFeaturedMod(newValue.getId(), page, featuredModFilterChoiceBox.getSelectionModel().getSelectedItem()))
                    .thenAccept(gamePlayerStats -> Platform.runLater(() -> userLastGamesTable.getItems().addAll(gamePlayerStats)));
            loadMoreGamesRunnable.run();
        }

        newBanButton.setDisable(newValue == null);
    }

    public void onUserSearch() {
        userSearchTableView.getItems().clear();
        userSearchTableView.getSortOrder().clear();

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
        } else if (searchUserByIpRadioButton.isSelected()) {
            usersFound = userService.findUserByIP(searchPattern);
        }

        userSearchTableView.getItems().addAll(usersFound);
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
        mapVaultView.getSortOrder().clear();
        String mapNamePattern = null;

        if (filterByMapNameCheckBox.isSelected()) {
            mapNamePattern = mapNamePatternTextField.getText();
        }

        ViewHelper.fillMapTreeView(mapVaultView,
                mapService.findMaps(mapNamePattern).stream());
    }

    public void onSearchMaps() {
        mapSearchTableView.getItems().clear();
        mapSearchTableView.getSortOrder().clear();

        List<Map> mapsFound = Collections.emptyList();
        String searchPattern = mapSearchTextField.getText();
        if (searchMapByMapNameRadioButton.isSelected()) {
            mapsFound = mapService.findMapsByName(searchPattern, excludeHiddenMapVersionsCheckbox.isSelected());
        } else if (searchMapByAuthorIdRadioButton.isSelected()) {
            mapsFound = mapService.findMapsByAuthorId(searchPattern, excludeHiddenMapVersionsCheckbox.isSelected());
        } else if (searchMapByAuthorNameRadioButton.isSelected()) {
            mapsFound = mapService.findMapsByAuthorName(searchPattern, excludeHiddenMapVersionsCheckbox.isSelected());
        }

        mapSearchTableView.getItems().addAll(mapMapper.map(mapsFound));
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

    public void onNewBan() {
        Player selectedPlayer = userSearchTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedPlayer, "Tou need to select a player to create a ban.");

        BanInfoController banInfoController = uiService.loadFxml("ui/banInfo.fxml");
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

        BanInfoController banInfoController = uiService.loadFxml("ui/banInfo.fxml");
        banInfoController.setBanInfo(selectedBan);

        Stage banInfoDialog = new Stage();
        banInfoDialog.setTitle("Edit Ban");
        banInfoDialog.setScene(new Scene(banInfoController.getRoot()));
        banInfoDialog.showAndWait();
    }


    public void onToggleMapVersionHiding() {
        MapVersionFX mapVersion = mapVersionTableView.getSelectionModel().getSelectedItem();

        Assert.notNull(mapVersion, "You can only edit a selected MapVersion");

        mapVersion.setHidden(!mapVersion.isHidden());
        mapService.patchMapVersion(mapVersionMapper.map(mapVersion));
    }

    public void onToggleMapVersionRanking() {
        MapVersionFX mapVersion = mapVersionTableView.getSelectionModel().getSelectedItem();

        Assert.notNull(mapVersion, "You can only edit a selected MapVersion");

        mapVersion.setRanked(!mapVersion.isRanked());
        mapService.patchMapVersion(mapVersionMapper.map(mapVersion));
    }

    public void loadMoreGames() {
        page++;
        loadMoreGamesRunnable.run();
    }
}