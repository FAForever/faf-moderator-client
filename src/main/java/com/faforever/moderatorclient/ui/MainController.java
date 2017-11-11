package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.dto.Map;
import com.faforever.moderatorclient.api.dto.Player;
import com.faforever.moderatorclient.search.MapService;
import com.faforever.moderatorclient.search.UserService;
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
import java.util.Collection;
import java.util.Collections;
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
    public TableView nameHistoryTableView;
    public TableView bansTableView;
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