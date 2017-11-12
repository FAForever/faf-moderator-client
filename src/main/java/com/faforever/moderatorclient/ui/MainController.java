package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.dto.*;
import com.faforever.moderatorclient.search.MapService;
import com.faforever.moderatorclient.search.UserService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
public class MainController implements Controller<TabPane> {
    private final UiService uiService;
    private final UserService userService;
    private final MapService mapService;
    public TabPane root;

    // Tab "User Management"
    public RadioButton currentNameRadioButton;
    public RadioButton previousNamesRadioButton;
    public RadioButton emailRadioButton;
    public RadioButton steamIdRadioButton;
    public TextField userSearchTextField;
    public TableView<Player> userSearchTableView;
    public TableView<NameRecord> nameHistoryTableView;
    public TableView<BanInfo> banTableView;
    public TableView<Teamkill> teamkillTableView;
    public TableView recentGamesTableView;

    // Tab "Ladder Map Pool"
    public TreeTableView<MapTableItemAdapter> ladderPoolView;
    public TreeTableView<MapTableItemAdapter> mapVaultView;
    public CheckBox filterByMapNameCheckBox;
    public TextField mapNamePatternTextField;
    public ImageView ladderPoolImageView;
    public ImageView mapVaultImageView;
    public Button removeFromPoolButton;
    public Button addToPoolButton;

    public MainController(UiService uiService, UserService userService, MapService mapSearchService) {
        this.uiService = uiService;
        this.userService = userService;
        this.mapService = mapSearchService;
    }

    private static void buildMapTreeView(TreeTableView<MapTableItemAdapter> mapTreeView) {
        TreeTableColumn<MapTableItemAdapter, String> idColumn = new TreeTableColumn<>("ID");
        idColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        mapTreeView.getColumns().add(idColumn);

        TreeTableColumn<MapTableItemAdapter, String> nameColumn = new TreeTableColumn<>("Name");
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        mapTreeView.getColumns().add(nameColumn);

        TreeTableColumn<MapTableItemAdapter, ComparableVersion> versionColumn = new TreeTableColumn<>("Version");
        versionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("version"));
        mapTreeView.getColumns().add(versionColumn);

        TreeTableColumn<MapTableItemAdapter, String> descriptionColumn = new TreeTableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        mapTreeView.getColumns().add(descriptionColumn);

        TreeTableColumn<MapTableItemAdapter, String> sizeColumn = new TreeTableColumn<>("Size");
        sizeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));
        mapTreeView.getColumns().add(sizeColumn);

        TreeTableColumn<MapTableItemAdapter, String> filenameColumn = new TreeTableColumn<>("Filename");
        filenameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("filename"));
        mapTreeView.getColumns().add(filenameColumn);

        TreeTableColumn<MapTableItemAdapter, String> isRankedColumn = new TreeTableColumn<>("Ranked");
        isRankedColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("ranked"));
        mapTreeView.getColumns().add(isRankedColumn);

        TreeTableColumn<MapTableItemAdapter, String> isHiddenColumn = new TreeTableColumn<>("Hidden");
        isHiddenColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("hidden"));
        mapTreeView.getColumns().add(isHiddenColumn);

        TreeItem<MapTableItemAdapter> rootTreeItem = new TreeItem<>(new MapTableItemAdapter(new Map()));
        mapTreeView.setRoot(rootTreeItem);
        mapTreeView.setShowRoot(false);
    }

    private static void bindMapTreeViewToImageView(TreeTableView<MapTableItemAdapter> mapTreeView, ImageView imageView) {
        mapTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                imageView.setImage(null);
                return;
            }

            URL thumbnailUrlLarge = newValue.getValue().getThumbnailUrlLarge();
            if (thumbnailUrlLarge != null) {
                imageView.setImage(new Image(thumbnailUrlLarge.toString(), true));
            } else {
                imageView.setImage(null);
            }
        });
    }

    private static void fillMapTreeView(TreeTableView<MapTableItemAdapter> mapTreeView, Stream<Map> mapStream) {
        mapStream.forEach(map -> {
            TreeItem<MapTableItemAdapter> mapItem = new TreeItem<>(new MapTableItemAdapter(map));
            mapTreeView.getRoot().getChildren().add(mapItem);

            map.getVersions().forEach(mapVersion -> mapItem.getChildren().add(new TreeItem<>(new MapTableItemAdapter(mapVersion))));
        });
    }

    @FXML
    public void initialize() {
        initUserManagementTab();
        initLadderMapPoolTab();
    }

    private void initLadderMapPoolTab() {
        buildMapTreeView(ladderPoolView);
        bindMapTreeViewToImageView(ladderPoolView, ladderPoolImageView);

        ladderPoolView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                removeFromPoolButton.setDisable(true);
            } else {
                removeFromPoolButton.setDisable(!newValue.getValue().isMapVersion());
            }
        });

        buildMapTreeView(mapVaultView);
        bindMapTreeViewToImageView(mapVaultView, mapVaultImageView);
        mapVaultView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                addToPoolButton.setDisable(true);
            } else {
                addToPoolButton.setDisable(!newValue.getValue().isMapVersion());
            }
        });
    }

    private void initUserManagementTab() {
        initUserSearchTableView();
        initNameHistoryTableView();
        initBanTableView();
        initTeamkillTableView();
    }

    private void initUserSearchTableView() {
        TableColumn<Player, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(50);
        userSearchTableView.getColumns().add(idColumn);

        TableColumn<Player, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        nameColumn.setMinWidth(150);
        userSearchTableView.getColumns().add(nameColumn);

        TableColumn<Player, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setMinWidth(250);
        userSearchTableView.getColumns().add(emailColumn);

        TableColumn<Player, String> steamIdColumn = new TableColumn<>("Steam ID");
        steamIdColumn.setCellValueFactory(new PropertyValueFactory<>("steamId"));
        steamIdColumn.setMinWidth(100);
        userSearchTableView.getColumns().add(steamIdColumn);

        userSearchTableView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedUser);
    }

    private void initNameHistoryTableView() {
        TableColumn<NameRecord, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(50);
        nameHistoryTableView.getColumns().add(idColumn);

        TableColumn<NameRecord, OffsetDateTime> changeTimeColumn = new TableColumn<>("Change Time");
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("changeTime"));
        changeTimeColumn.setMinWidth(180);
        nameHistoryTableView.getColumns().add(changeTimeColumn);

        TableColumn<NameRecord, String> nameColumn = new TableColumn<>("Previous Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setMinWidth(200);
        nameHistoryTableView.getColumns().add(nameColumn);
    }

    private void initBanTableView() {
        TableColumn<BanInfo, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(50);
        banTableView.getColumns().add(idColumn);

        TableColumn<BanInfo, BanLevel> banLevelColumn = new TableColumn<>("Level");
        banLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        banLevelColumn.setMinWidth(80);
        banTableView.getColumns().add(banLevelColumn);

        TableColumn<BanInfo, BanStatus> banStatusColumn = new TableColumn<>("Status");
        banStatusColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getBanStatus()));
        banStatusColumn.setMinWidth(100);
        banTableView.getColumns().add(banStatusColumn);

        TableColumn<BanInfo, BanDurationType> banDurationColumn = new TableColumn<>("Duration");
        banDurationColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDuration()));
        banDurationColumn.setMinWidth(100);
        banTableView.getColumns().add(banDurationColumn);

        TableColumn<BanInfo, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires at");
        expiresAtColumn.setCellValueFactory(new PropertyValueFactory<>("expiresAt"));
        expiresAtColumn.setMinWidth(180);
        banTableView.getColumns().add(expiresAtColumn);

        TableColumn<BanInfo, String> reasonColumn = new TableColumn<>("Reason");
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonColumn.setMinWidth(250);
        banTableView.getColumns().add(reasonColumn);

        TableColumn<BanInfo, Player> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setMinWidth(150);
        banTableView.getColumns().add(authorColumn);

        TableColumn<BanInfo, String> revokeReasonColumn = new TableColumn<>("Revocation Reason");
        revokeReasonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue().getBanRevokeData())
                        .map(BanRevokeData::getReason)
                        .orElse(""))
        );
        revokeReasonColumn.setMinWidth(250);
        banTableView.getColumns().add(revokeReasonColumn);

        TableColumn<BanInfo, Player> revokeAuthorColumn = new TableColumn<>("Revocation Author");
        revokeAuthorColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue().getBanRevokeData())
                        .map(BanRevokeData::getAuthor)
                        .orElse(null))
        );
        revokeAuthorColumn.setMinWidth(150);
        banTableView.getColumns().add(revokeAuthorColumn);

        TableColumn<BanInfo, OffsetDateTime> changeTimeColumn = new TableColumn<>("Created Time");
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        changeTimeColumn.setMinWidth(180);
        banTableView.getColumns().add(changeTimeColumn);

        TableColumn<BanInfo, OffsetDateTime> updateTimeColumn = new TableColumn<>("Update (Revoke) Time");
        updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
        updateTimeColumn.setMinWidth(180);
        banTableView.getColumns().add(updateTimeColumn);
    }

    private void initTeamkillTableView() {
        TableColumn<Teamkill, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(50);
        teamkillTableView.getColumns().add(idColumn);

        TableColumn<Teamkill, Player> victimColumn = new TableColumn<>("Victim");
        victimColumn.setCellValueFactory(new PropertyValueFactory<>("victim"));
        victimColumn.setMinWidth(180);
        teamkillTableView.getColumns().add(victimColumn);

        TableColumn<Teamkill, String> banStatusColumn = new TableColumn<>("Game ID");
        banStatusColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getGame().getId()));
        banStatusColumn.setMinWidth(100);
        teamkillTableView.getColumns().add(banStatusColumn);

        TableColumn<Teamkill, Long> gameTimeColumn = new TableColumn<>("Game Time");
        gameTimeColumn.setCellValueFactory(new PropertyValueFactory<>("gameTime"));
        gameTimeColumn.setMinWidth(100);
        teamkillTableView.getColumns().add(gameTimeColumn);

        TableColumn<Teamkill, Long> reportedAtColumn = new TableColumn<>("Reported At");
        reportedAtColumn.setCellValueFactory(new PropertyValueFactory<>("reportedAt"));
        reportedAtColumn.setMinWidth(180);
        teamkillTableView.getColumns().add(reportedAtColumn);
    }

    private void onSelectedUser(ObservableValue<? extends Player> observable, Player oldValue, Player newValue) {
        nameHistoryTableView.getItems().clear();
        nameHistoryTableView.getItems().addAll(newValue.getNames());

        banTableView.getItems().clear();
        banTableView.getItems().addAll(newValue.getBans());

        teamkillTableView.getItems().clear();
        teamkillTableView.getItems().addAll(userService.findTeamkillsByUserId(newValue.getId()));
    }

    public void onUserSearch() {
        userSearchTableView.getItems().clear();

        Collection<Player> usersFound = Collections.emptyList();
        String searchPattern = userSearchTextField.getText();
        if (currentNameRadioButton.isSelected()) {
            usersFound = userService.findUserByName(searchPattern);
        }

        if (previousNamesRadioButton.isSelected()) {
            usersFound = userService.findUsersByPreviousName(searchPattern);
        }

        if (emailRadioButton.isSelected()) {
            usersFound = userService.findUserByEmail(searchPattern);
        }

        if (steamIdRadioButton.isSelected()) {
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

        fillMapTreeView(mapVaultView,
                mapService.findMaps(mapNamePattern).stream());
    }
}