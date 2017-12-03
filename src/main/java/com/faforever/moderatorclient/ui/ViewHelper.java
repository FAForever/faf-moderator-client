package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.dto.*;
import com.faforever.moderatorclient.ui.domain.GamePlayerStatsFX;
import com.faforever.moderatorclient.ui.domain.MapFX;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.net.URL;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;

public class ViewHelper {
    private ViewHelper() {
        // static class
    }

    public static void bindMapTreeViewToImageView(TreeTableView<MapTableItemAdapter> mapTreeView, ImageView imageView) {
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

    public static void buildAvatarTableView(TableView<Avatar> tableView) {
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

    public static void buildAvatarAssignmentTableView(TableView<AvatarAssignment> tableView) {
        TableColumn<AvatarAssignment, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<AvatarAssignment, String> userIdColumn = new TableColumn<>("User ID");
        userIdColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(avatarAssignment -> avatarAssignment.getPlayer().getId())
                        .orElse(""))
        );
        userIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        userIdColumn.setMinWidth(50);
        tableView.getColumns().add(userIdColumn);

        TableColumn<AvatarAssignment, String> userNameColumn = new TableColumn<>("User name");
        userNameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(avatarAssignment -> avatarAssignment.getPlayer().getLogin())
                        .orElse(""))
        );
        userNameColumn.setMinWidth(150);
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
        tableView.getColumns().add(assignedAtColumn);
    }

    public static void buildBanTableView(TableView<BanInfo> tableView) {
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

    public static void buildNameHistoryTableView(TableView<NameRecord> tableView) {
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

    public static void buildTeamkillTableView(javafx.scene.control.TableView<Teamkill> tableView, boolean showKiller) {
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

    public static void buildUserTableView(TableView<Player> tableView) {
        TableColumn<Player, Player> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        idColumn.setCellFactory(param -> new TableCell<Player, Player>() {
            Tooltip tooltip = new Tooltip();

            {
                setTooltip(tooltip);
                setTextAlignment(TextAlignment.RIGHT);
            }

            @Override
            protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                setTextFill(Color.BLACK);

                if (item == null) {
                    setText("");
                    setStyle("");
                    tooltip.setText("");
                } else {
                    setText(item.getId());

                    if (item.getBans().isEmpty()) {
                        setStyle("");
                        tooltip.setText("No bans");
                    } else {
                        setStyle("-fx-font-weight: bold");

                        if (item.getBans().stream()
                                .anyMatch(banInfo -> banInfo.getBanStatus() == BanStatus.BANNED && banInfo.getDuration() == BanDurationType.PERMANENT)) {
                            tooltip.setText("Permanent ban");
                            setTextFill(Color.valueOf("#ca0000"));
                        } else if (item.getBans().stream()
                                .allMatch(banInfo -> banInfo.getBanStatus() == BanStatus.EXPIRED)) {
                            tooltip.setText("Expired ban");
                            setTextFill(Color.valueOf("#098700"));
                        } else {
                            tooltip.setText("Temporary ban");
                            setTextFill(Color.valueOf("#ff8800"));
                        }
                    }
                }
            }
        });
        idColumn.setComparator(Comparator.comparingInt(o -> Integer.parseInt(o.getId())));
        idColumn.setMinWidth(70);
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
        steamIdColumn.setMinWidth(150);
        tableView.getColumns().add(steamIdColumn);

        TableColumn<Player, String> ipColumn = new TableColumn<>("Recent IP Address");
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("recentIpAddress"));
        ipColumn.setMinWidth(160);
        tableView.getColumns().add(ipColumn);

        TableColumn<Player, OffsetDateTime> createTimeColumn = new TableColumn<>("Registration Date");
        createTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<Player, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last lobby login");
        updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);

        TableColumn<Player, String> userAgentColumn = new TableColumn<>("User Agent");
        userAgentColumn.setCellValueFactory(new PropertyValueFactory<>("userAgent"));
        userAgentColumn.setMinWidth(200);
        tableView.getColumns().add(userAgentColumn);
    }

    public static void buildUserAvatarsTableView(TableView<AvatarAssignment> tableView) {
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

    public static void buildMapTreeView(TreeTableView<MapTableItemAdapter> mapTreeView) {
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

    public static void buildMapTableView(TableView<MapFX> tableView) {
        TableColumn<MapFX, String> idColumn = new TableColumn<>("Map ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(100);
        tableView.getColumns().add(idColumn);

        TableColumn<MapFX, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(o -> o.getValue().displayNameProperty());
        nameColumn.setMinWidth(200);
        tableView.getColumns().add(nameColumn);


        TableColumn<MapFX, PlayerFX> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(o -> o.getValue().authorProperty());
        authorColumn.setCellFactory(o -> new TableCell<MapFX, PlayerFX>() {
            Label label;

            {
                label = new Label();
                setGraphic(label);
            }

            @Override
            protected void updateItem(PlayerFX item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    label.setText("");
                } else if (item == null) {
                    label.setText("unknown");
                } else {
                    label.setText(MessageFormat.format("{0} [id = {1}]", item.getLogin(), item.getId()));
                }
            }
        });
        authorColumn.setMinWidth(200);
        tableView.getColumns().add(authorColumn);

        TableColumn<MapFX, OffsetDateTime> createTimeColumn = new TableColumn<>("First upload");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<MapFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);
    }

    public static void buildPlayersGamesTable(TableView<GamePlayerStatsFX> tableView, String replayDownloadFormat, PlatformService platformService) {
        TableColumn<GamePlayerStatsFX, String> gameIdColumn = new TableColumn<>("Game ID");
        gameIdColumn.setCellValueFactory(o -> o.getValue().getGame().idProperty());
        gameIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        gameIdColumn.setMinWidth(100);
        tableView.getColumns().add(gameIdColumn);

        TableColumn<GamePlayerStatsFX, String> gameNameColumn = new TableColumn<>("Game Name");
        gameNameColumn.setCellValueFactory(o -> o.getValue().getGame().nameProperty());
        gameNameColumn.setMinWidth(100);
        tableView.getColumns().add(gameNameColumn);

        TableColumn<GamePlayerStatsFX, String> rankedColumn = new TableColumn<>("Game Validity");
        rankedColumn.setCellValueFactory(o -> new SimpleObjectProperty<>(
                o.getValue().getGame().getValidity().name()
        ));
        rankedColumn.setComparator(Comparator.naturalOrder());
        rankedColumn.setMinWidth(120);
        tableView.getColumns().add(rankedColumn);

        TableColumn<GamePlayerStatsFX, Number> beforeGameRatingColumn = new TableColumn<>("Rating Before Game");
        beforeGameRatingColumn.setCellValueFactory(o -> o.getValue().beforeRatingProperty());
        beforeGameRatingColumn.setMinWidth(150);
        tableView.getColumns().add(beforeGameRatingColumn);

        TableColumn<GamePlayerStatsFX, Number> afterGameRatingColumn = new TableColumn<>("Rating Change");
        afterGameRatingColumn.setCellValueFactory(o -> o.getValue().ratingChangeProperty());
        afterGameRatingColumn.setMinWidth(100);
        tableView.getColumns().add(afterGameRatingColumn);

        TableColumn<GamePlayerStatsFX, OffsetDateTime> scoreTimeDateColumn = new TableColumn<>("Score Time");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
                .withZone(TimeZone.getDefault().toZoneId());
        scoreTimeDateColumn.setCellValueFactory(o ->
                o.getValue().scoreTimeProperty()
        );
        scoreTimeDateColumn.setCellFactory(param -> new TableCell<GamePlayerStatsFX, OffsetDateTime>() {
            Label label;

            {
                label = new Label();
                setGraphic(label);
            }
            @Override
            protected void updateItem(OffsetDateTime item, boolean empty) {
                super.updateItem(item, empty);
                label.setVisible(!empty);
                label.setText(item == null ? "unknown date" : item.format(dateTimeFormatter));
            }
        });
        scoreTimeDateColumn.setComparator(Comparator.naturalOrder());
        scoreTimeDateColumn.setMinWidth(150);
        tableView.getColumns().add(scoreTimeDateColumn);

        TableColumn<GamePlayerStatsFX, String> replayUrlColumn = new TableColumn<>("Replay");
        replayUrlColumn.setCellValueFactory(o ->
                Bindings.createStringBinding(() -> o.getValue().getGame().getReplayUrl(replayDownloadFormat))
        );
        replayUrlColumn.setCellFactory(param -> new TableCell<GamePlayerStatsFX, String>() {
            Button button;

            {
                button = new Button("Download Replay");
                setGraphic(button);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                button.setVisible(!empty && item != null);
                button.setOnAction(event -> platformService.showDocument(item));
            }
        });
        replayUrlColumn.setSortable(false);
        replayUrlColumn.setMinWidth(150);
        tableView.getColumns().add(replayUrlColumn);
    }

    public static void buildMapVersionTableView(TableView<MapVersionFX> tableView) {
        TableColumn<MapVersionFX, String> idColumn = new TableColumn<>("Version ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(100);
        tableView.getColumns().add(idColumn);

        TableColumn<MapVersionFX, ComparableVersion> nameColumn = new TableColumn<>("Version No.");
        nameColumn.setCellValueFactory(o -> o.getValue().versionProperty());
        nameColumn.setMinWidth(100);
        tableView.getColumns().add(nameColumn);


        TableColumn<MapVersionFX, Boolean> rankedCheckBoxColumn = new TableColumn<>("Ranked");
        rankedCheckBoxColumn.setCellValueFactory(param -> param.getValue().rankedProperty());
        rankedCheckBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(rankedCheckBoxColumn));
        tableView.getColumns().add(rankedCheckBoxColumn);

        TableColumn<MapVersionFX, Boolean> hiddenColumn = new TableColumn<>("Hidden");
        hiddenColumn.setCellValueFactory(o -> o.getValue().hiddenProperty());
        hiddenColumn.setCellFactory(CheckBoxTableCell.forTableColumn(hiddenColumn));
        tableView.getColumns().add(hiddenColumn);

        TableColumn<MapVersionFX, Number> maxPlayersColumn = new TableColumn<>("Max Players");
        maxPlayersColumn.setCellValueFactory(o -> o.getValue().maxPlayersProperty());
        maxPlayersColumn.setMinWidth(130);
        tableView.getColumns().add(maxPlayersColumn);

        TableColumn<MapVersionFX, Number> widthColumn = new TableColumn<>("Width");
        widthColumn.setCellValueFactory(o -> o.getValue().widthProperty());
        tableView.getColumns().add(widthColumn);

        TableColumn<MapVersionFX, Number> heightColumn = new TableColumn<>("Height");
        heightColumn.setCellValueFactory(o -> o.getValue().heightProperty());
        tableView.getColumns().add(heightColumn);

        TableColumn<MapVersionFX, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(o -> o.getValue().descriptionProperty());
        descriptionColumn.setMinWidth(300);
        tableView.getColumns().add(descriptionColumn);


        TableColumn<MapVersionFX, OffsetDateTime> createTimeColumn = new TableColumn<>("Uploaded");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<MapVersionFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);
    }

    public static void fillMapTreeView(TreeTableView<MapTableItemAdapter> mapTreeView, Stream<Map> mapStream) {
        mapStream.forEach(map -> {
            TreeItem<MapTableItemAdapter> mapItem = new TreeItem<>(new MapTableItemAdapter(map));
            mapTreeView.getRoot().getChildren().add(mapItem);

            map.getVersions().forEach(mapVersion -> mapItem.getChildren().add(new TreeItem<>(new MapTableItemAdapter(mapVersion))));
        });
    }
}
