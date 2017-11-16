package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.dto.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

class ViewHelper {
    private ViewHelper() {
        // static class
    }

    static void bindMapTreeViewToImageView(TreeTableView<MapTableItemAdapter> mapTreeView, ImageView imageView) {
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

    static void buildAvatarTableView(TableView<Avatar> tableView) {
        TableColumn<Avatar, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<Avatar, String> previewColumn = new TableColumn<>("Preview");
        previewColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        previewColumn.setCellFactory(param -> new UrlImageViewTableCell<>());
        previewColumn.setMinWidth(50);
        tableView.getColumns().add(previewColumn);

        TableColumn<Avatar, String> tooltipColumn = new TableColumn<>("Tooltip");
        tooltipColumn.setCellValueFactory(new PropertyValueFactory<>("tooltip"));
        tooltipColumn.setMinWidth(50);
        tableView.getColumns().add(tooltipColumn);

        TableColumn<Avatar, OffsetDateTime> changeTimeColumn = new TableColumn<>("Created");
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<Avatar, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.setMinWidth(50);
        tableView.getColumns().add(urlColumn);

    }

    static void buildAvatarAssignmentTableView(TableView<AvatarAssignment> tableView) {
        TableColumn<AvatarAssignment, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        idColumn.setEditable(false);
        tableView.getColumns().add(idColumn);

        TableColumn<AvatarAssignment, String> userIdColumn = new TableColumn<>("User ID");
        userIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userIdColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(avatarAssignment -> avatarAssignment.getPlayer().getId())
                        .orElse(""))
        );
        userIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        userIdColumn.setMinWidth(50);
        idColumn.setEditable(true);
        tableView.getColumns().add(userIdColumn);

        TableColumn<AvatarAssignment, String> userNameColumn = new TableColumn<>("User name");
        userNameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(avatarAssignment -> avatarAssignment.getPlayer().getLogin())
                        .orElse(""))
        );
        userNameColumn.setMinWidth(150);
        userNameColumn.setEditable(false);
        tableView.getColumns().add(userNameColumn);

        TableColumn<AvatarAssignment, Boolean> selectedColumn = new TableColumn<>("Selected");
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setMinWidth(50);
        tableView.getColumns().add(selectedColumn);

        TableColumn<AvatarAssignment, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires at");
        expiresAtColumn.setCellValueFactory(new PropertyValueFactory<>("expiresAt"));
        expiresAtColumn.setMinWidth(180);
        tableView.getColumns().add(expiresAtColumn);


        TableColumn<AvatarAssignment, OffsetDateTime> assignedAtColumn = new TableColumn<>("Assigned at");
        assignedAtColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        assignedAtColumn.setMinWidth(180);
        idColumn.setEditable(false);
        tableView.getColumns().add(assignedAtColumn);
    }

    static void buildBanTableView(TableView<BanInfo> tableView) {
        TableColumn<BanInfo, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<BanInfo, BanLevel> banLevelColumn = new TableColumn<>("Level");
        banLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        banLevelColumn.setMinWidth(80);
        tableView.getColumns().add(banLevelColumn);

        TableColumn<BanInfo, BanStatus> banStatusColumn = new TableColumn<>("Status");
        banStatusColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getBanStatus()));
        banStatusColumn.setMinWidth(100);
        tableView.getColumns().add(banStatusColumn);

        TableColumn<BanInfo, BanDurationType> banDurationColumn = new TableColumn<>("Duration");
        banDurationColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDuration()));
        banDurationColumn.setMinWidth(100);
        tableView.getColumns().add(banDurationColumn);

        TableColumn<BanInfo, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires at");
        expiresAtColumn.setCellValueFactory(new PropertyValueFactory<>("expiresAt"));
        expiresAtColumn.setMinWidth(180);
        tableView.getColumns().add(expiresAtColumn);

        TableColumn<BanInfo, String> reasonColumn = new TableColumn<>("Reason");
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonColumn.setMinWidth(250);
        tableView.getColumns().add(reasonColumn);

        TableColumn<BanInfo, Player> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setMinWidth(150);
        tableView.getColumns().add(authorColumn);

        TableColumn<BanInfo, String> revokeReasonColumn = new TableColumn<>("Revocation Reason");
        revokeReasonColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue().getBanRevokeData())
                        .map(BanRevokeData::getReason)
                        .orElse(""))
        );
        revokeReasonColumn.setMinWidth(250);
        tableView.getColumns().add(revokeReasonColumn);

        TableColumn<BanInfo, Player> revokeAuthorColumn = new TableColumn<>("Revocation Author");
        revokeAuthorColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue().getBanRevokeData())
                        .map(BanRevokeData::getAuthor)
                        .orElse(null))
        );
        revokeAuthorColumn.setMinWidth(150);
        tableView.getColumns().add(revokeAuthorColumn);

        TableColumn<BanInfo, OffsetDateTime> changeTimeColumn = new TableColumn<>("Created Time");
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<BanInfo, OffsetDateTime> updateTimeColumn = new TableColumn<>("Update (Revoke) Time");
        updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
        updateTimeColumn.setMinWidth(180);
        tableView.getColumns().add(updateTimeColumn);
    }

    static void buildNameHistoryTableView(TableView<NameRecord> tableView) {
        TableColumn<NameRecord, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<NameRecord, OffsetDateTime> changeTimeColumn = new TableColumn<>("Change Time");
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("changeTime"));
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<NameRecord, String> nameColumn = new TableColumn<>("Previous Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setMinWidth(200);
        tableView.getColumns().add(nameColumn);
    }

    static void buildTeamkillTableView(javafx.scene.control.TableView<Teamkill> tableView, boolean showKiller) {
        TableColumn<Teamkill, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        if (showKiller) {
            TableColumn<Teamkill, Player> killerColumn = new TableColumn<>("Killer");
            killerColumn.setCellValueFactory(new PropertyValueFactory<>("teamkiller"));
            killerColumn.setMinWidth(180);
            tableView.getColumns().add(killerColumn);
        }

        TableColumn<Teamkill, Player> victimColumn = new TableColumn<>("Victim");
        victimColumn.setCellValueFactory(new PropertyValueFactory<>("victim"));
        victimColumn.setMinWidth(180);
        tableView.getColumns().add(victimColumn);

        TableColumn<Teamkill, String> gameIdColumn = new TableColumn<>("Game ID");
        gameIdColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getGame().getId()));
        gameIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        gameIdColumn.setMinWidth(100);
        tableView.getColumns().add(gameIdColumn);

        TableColumn<Teamkill, Long> gameTimeColumn = new TableColumn<>("Game Time");
        gameTimeColumn.setCellValueFactory(new PropertyValueFactory<>("gameTime"));
        gameTimeColumn.setMinWidth(100);
        tableView.getColumns().add(gameTimeColumn);

        TableColumn<Teamkill, Long> reportedAtColumn = new TableColumn<>("Reported At");
        reportedAtColumn.setCellValueFactory(new PropertyValueFactory<>("reportedAt"));
        reportedAtColumn.setMinWidth(180);
        tableView.getColumns().add(reportedAtColumn);
    }

    static void buildUserTableView(TableView<Player> tableView) {
        TableColumn<Player, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<Player, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        nameColumn.setMinWidth(150);
        tableView.getColumns().add(nameColumn);

        TableColumn<Player, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setMinWidth(250);
        tableView.getColumns().add(emailColumn);

        TableColumn<Player, String> steamIdColumn = new TableColumn<>("Steam ID");
        steamIdColumn.setCellValueFactory(new PropertyValueFactory<>("steamId"));
        steamIdColumn.setMinWidth(100);
        tableView.getColumns().add(steamIdColumn);
    }

    static void buildUserAvatarsTableView(TableView<AvatarAssignment> tableView) {
        TableColumn<AvatarAssignment, String> idColumn = new TableColumn<>("Assignment ID");
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(140);
        tableView.getColumns().add(idColumn);

        TableColumn<AvatarAssignment, String> avatarIdColumn = new TableColumn<>("Avatar ID");
        avatarIdColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                param.getValue().getAvatar().getId()
        ));
        avatarIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        avatarIdColumn.setMinWidth(50);
        tableView.getColumns().add(avatarIdColumn);

        TableColumn<AvatarAssignment, String> previewColumn = new TableColumn<>("Preview");
        previewColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                param.getValue().getAvatar().getUrl()
        ));

        previewColumn.setCellFactory(param -> new UrlImageViewTableCell<>());
        previewColumn.setMinWidth(50);
        tableView.getColumns().add(previewColumn);

        TableColumn<AvatarAssignment, String> tooltipColumn = new TableColumn<>("Tooltip");
        tooltipColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                param.getValue().getAvatar().getTooltip()
        ));
        tooltipColumn.setMinWidth(100);
        tableView.getColumns().add(tooltipColumn);

        TableColumn<AvatarAssignment, Boolean> selectedColumn = new TableColumn<>("Selected");
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
//        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setMinWidth(50);
        tableView.getColumns().add(selectedColumn);

        TableColumn<AvatarAssignment, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires At");
        expiresAtColumn.setCellValueFactory(new PropertyValueFactory<>("expiresAt"));
        expiresAtColumn.setMinWidth(180);
        tableView.getColumns().add(expiresAtColumn);
    }

    static void buildMapTreeView(TreeTableView<MapTableItemAdapter> mapTreeView) {
        TreeTableColumn<MapTableItemAdapter, String> idColumn = new TreeTableColumn<>("ID");
        idColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(100);
        mapTreeView.getColumns().add(idColumn);

        TreeTableColumn<MapTableItemAdapter, String> nameColumn = new TreeTableColumn<>("Name / Description");
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("nameOrDescription"));
        nameColumn.setMinWidth(300);
        mapTreeView.getColumns().add(nameColumn);

        TreeTableColumn<MapTableItemAdapter, ComparableVersion> versionColumn = new TreeTableColumn<>("Version");
        versionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("version"));
        mapTreeView.getColumns().add(versionColumn);

        TreeTableColumn<MapTableItemAdapter, String> sizeColumn = new TreeTableColumn<>("Size");
        sizeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));
        sizeColumn.setMinWidth(130);
        mapTreeView.getColumns().add(sizeColumn);

        TreeTableColumn<MapTableItemAdapter, String> filenameColumn = new TreeTableColumn<>("Filename");
        filenameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("filename"));
        filenameColumn.setMinWidth(300);
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

    static void fillMapTreeView(TreeTableView<MapTableItemAdapter> mapTreeView, Stream<Map> mapStream) {
        mapStream.forEach(map -> {
            TreeItem<MapTableItemAdapter> mapItem = new TreeItem<>(new MapTableItemAdapter(map));
            mapTreeView.getRoot().getChildren().add(mapItem);

            map.getVersions().forEach(mapVersion -> mapItem.getChildren().add(new TreeItem<>(new MapTableItemAdapter(mapVersion))));
        });
    }
}
