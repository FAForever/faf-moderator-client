package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.BanDurationType;
import com.faforever.commons.api.dto.BanLevel;
import com.faforever.commons.api.dto.BanStatus;
import com.faforever.commons.api.dto.Map;
import com.faforever.moderatorclient.ui.domain.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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

    public static void buildAvatarTableView(TableView<AvatarFX> tableView, ObservableList<AvatarFX> data) {
        tableView.setItems(data);

        TableColumn<AvatarFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<AvatarFX, String> previewColumn = new TableColumn<>("Preview");
        previewColumn.setCellValueFactory(o -> o.getValue().urlProperty());
        previewColumn.setCellFactory(param -> new UrlImageViewTableCell<>());
        previewColumn.setMinWidth(50);
        tableView.getColumns().add(previewColumn);

        TableColumn<AvatarFX, String> tooltipColumn = new TableColumn<>("Tooltip");
        tooltipColumn.setCellValueFactory(o -> o.getValue().tooltipProperty());
        tooltipColumn.setMinWidth(50);
        tableView.getColumns().add(tooltipColumn);

        TableColumn<AvatarFX, OffsetDateTime> changeTimeColumn = new TableColumn<>("Created");
        changeTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<AvatarFX, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(o -> o.getValue().urlProperty());
        urlColumn.setMinWidth(50);
        tableView.getColumns().add(urlColumn);

    }

    public static void buildAvatarAssignmentTableView(TableView<AvatarAssignmentFX> tableView, ObservableList<AvatarAssignmentFX> data) {
        tableView.setItems(data);

        TableColumn<AvatarAssignmentFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<AvatarAssignmentFX, String> userIdColumn = new TableColumn<>("User ID");


        userIdColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(avatarAssignment -> avatarAssignment.getPlayer().getId())
                        .orElse(""))
        );
        userIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        userIdColumn.setMinWidth(50);
        tableView.getColumns().add(userIdColumn);

        TableColumn<AvatarAssignmentFX, String> userNameColumn = new TableColumn<>("User name");
        userNameColumn.setCellValueFactory(o -> o.getValue().playerProperty().get().representationProperty());
        userNameColumn.setMinWidth(150);
        tableView.getColumns().add(userNameColumn);

        TableColumn<AvatarAssignmentFX, Boolean> selectedColumn = new TableColumn<>("Selected");
        selectedColumn.setCellValueFactory(o -> o.getValue().selectedProperty());
        selectedColumn.setMinWidth(50);
        tableView.getColumns().add(selectedColumn);

        TableColumn<AvatarAssignmentFX, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires at");
        expiresAtColumn.setCellValueFactory(o -> o.getValue().expiresAtProperty());
        expiresAtColumn.setMinWidth(180);
        tableView.getColumns().add(expiresAtColumn);


        TableColumn<AvatarAssignmentFX, OffsetDateTime> assignedAtColumn = new TableColumn<>("Assigned at");
        assignedAtColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        assignedAtColumn.setMinWidth(180);
        tableView.getColumns().add(assignedAtColumn);

    }

    public static void buildBanTableView(TableView<BanInfoFX> tableView, ObservableList<BanInfoFX> data) {
        tableView.setItems(data);

        TableColumn<BanInfoFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<BanInfoFX, BanLevel> banLevelColumn = new TableColumn<>("Level");
        banLevelColumn.setCellValueFactory(o -> o.getValue().levelProperty());
        banLevelColumn.setMinWidth(80);
        tableView.getColumns().add(banLevelColumn);

        TableColumn<BanInfoFX, BanStatus> banStatusColumn = new TableColumn<>("Status");
        banStatusColumn.setCellValueFactory(o -> o.getValue().banStatusProperty());
        banStatusColumn.setMinWidth(100);
        tableView.getColumns().add(banStatusColumn);

        TableColumn<BanInfoFX, BanDurationType> banDurationColumn = new TableColumn<>("Duration");
        banDurationColumn.setCellValueFactory(o -> o.getValue().durationProperty());
        banDurationColumn.setMinWidth(100);
        tableView.getColumns().add(banDurationColumn);

        TableColumn<BanInfoFX, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires at");
        expiresAtColumn.setCellValueFactory(o -> o.getValue().expiresAtProperty());
        expiresAtColumn.setMinWidth(180);
        tableView.getColumns().add(expiresAtColumn);

        TableColumn<BanInfoFX, String> reasonColumn = new TableColumn<>("Reason");
        reasonColumn.setCellValueFactory(o -> o.getValue().reasonProperty());
        reasonColumn.setMinWidth(250);
        tableView.getColumns().add(reasonColumn);

        TableColumn<BanInfoFX, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(o -> o.getValue().authorProperty().get().representationProperty());
        authorColumn.setMinWidth(150);
        tableView.getColumns().add(authorColumn);

        TableColumn<BanInfoFX, String> revokeReasonColumn = new TableColumn<>("Revocation Reason");
        revokeReasonColumn.setCellValueFactory(o -> {
            ObjectProperty<BanRevokeDataFX> banRevokeProperty = o.getValue().banRevokeDataProperty();
            return Bindings.createStringBinding(() -> {
                        if (banRevokeProperty.get() != null) {
                            return banRevokeProperty.get().reasonProperty().get();
                        } else {
                            return "";
                        }
                    },
                    banRevokeProperty);
        });
        revokeReasonColumn.setMinWidth(250);
        tableView.getColumns().add(revokeReasonColumn);

        TableColumn<BanInfoFX, String> revokeAuthorColumn = new TableColumn<>("Revocation Author");
        revokeAuthorColumn.setCellValueFactory(o -> {
                    BanRevokeDataFX banRevokeData = o.getValue().getBanRevokeData();
                    if (banRevokeData != null) {
                        return banRevokeData.getAuthor().representationProperty();
                    } else {
                        return null;
                    }
                }
        );
        revokeAuthorColumn.setMinWidth(150);
        tableView.getColumns().add(revokeAuthorColumn);

        TableColumn<BanInfoFX, OffsetDateTime> changeTimeColumn = new TableColumn<>("Created Time");
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<BanInfoFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Update (Revoke) Time");
        updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
        updateTimeColumn.setMinWidth(180);
        tableView.getColumns().add(updateTimeColumn);
    }

    public static void buildNameHistoryTableView(TableView<NameRecordFX> tableView, ObservableList<NameRecordFX> data) {
        tableView.setItems(data);

        TableColumn<NameRecordFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        TableColumn<NameRecordFX, OffsetDateTime> changeTimeColumn = new TableColumn<>("Change Time");
        changeTimeColumn.setCellValueFactory(o -> o.getValue().changeTimeProperty());
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<NameRecordFX, String> nameColumn = new TableColumn<>("Previous Name");
        nameColumn.setCellValueFactory(o -> o.getValue().nameProperty());
        nameColumn.setMinWidth(200);
        tableView.getColumns().add(nameColumn);
    }

    public static void buildTeamkillTableView(TableView<TeamkillFX> tableView, ObservableList<TeamkillFX> data, boolean showKiller) {
        tableView.setItems(data);

        TableColumn<TeamkillFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);

        if (showKiller) {
            TableColumn<TeamkillFX, String> killerColumn = new TableColumn<>("Killer");
            killerColumn.setCellValueFactory(o -> o.getValue().teamkillerProperty().get().representationProperty());
            killerColumn.setMinWidth(180);
            tableView.getColumns().add(killerColumn);
        }

        TableColumn<TeamkillFX, String> victimColumn = new TableColumn<>("Victim");
        victimColumn.setCellValueFactory(o -> o.getValue().victimProperty().get().representationProperty());
        victimColumn.setMinWidth(180);
        tableView.getColumns().add(victimColumn);

        TableColumn<TeamkillFX, String> gameIdColumn = new TableColumn<>("Game ID");
        gameIdColumn.setCellValueFactory(o -> o.getValue().gameProperty().get().idProperty());
        gameIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        gameIdColumn.setMinWidth(100);
        tableView.getColumns().add(gameIdColumn);

        TableColumn<TeamkillFX, Number> gameTimeColumn = new TableColumn<>("Game Time");
        gameTimeColumn.setCellValueFactory(o -> o.getValue().gameTimeProperty());
        gameTimeColumn.setMinWidth(100);
        tableView.getColumns().add(gameTimeColumn);

        TableColumn<TeamkillFX, OffsetDateTime> reportedAtColumn = new TableColumn<>("Reported At");
        reportedAtColumn.setCellValueFactory(o -> o.getValue().reportedAtProperty());
        reportedAtColumn.setMinWidth(180);
        tableView.getColumns().add(reportedAtColumn);
    }

    public static void buildUserTableView(TableView<PlayerFX> tableView, ObservableList<PlayerFX> data) {
        tableView.setItems(data);

        TableColumn<PlayerFX, PlayerFX> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        idColumn.setCellFactory(param -> new TableCell<PlayerFX, PlayerFX>() {
            Tooltip tooltip = new Tooltip();

            {
                setTooltip(tooltip);
                setTextAlignment(TextAlignment.RIGHT);
            }

            @Override
            protected void updateItem(PlayerFX item, boolean empty) {
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

        TableColumn<PlayerFX, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(o -> o.getValue().loginProperty());
        nameColumn.setMinWidth(150);
        tableView.getColumns().add(nameColumn);

        TableColumn<PlayerFX, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(o -> o.getValue().emailProperty());
        emailColumn.setMinWidth(250);
        tableView.getColumns().add(emailColumn);

        TableColumn<PlayerFX, String> steamIdColumn = new TableColumn<>("Steam ID");
        steamIdColumn.setCellValueFactory(o -> o.getValue().steamIdProperty());
        steamIdColumn.setMinWidth(150);
        tableView.getColumns().add(steamIdColumn);

        TableColumn<PlayerFX, String> ipColumn = new TableColumn<>("Recent IP Address");
        ipColumn.setCellValueFactory(o -> o.getValue().recentIpAddressProperty());
        ipColumn.setMinWidth(160);
        tableView.getColumns().add(ipColumn);

        TableColumn<PlayerFX, OffsetDateTime> createTimeColumn = new TableColumn<>("Registration Date");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<PlayerFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last lobby login");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);

        TableColumn<PlayerFX, String> userAgentColumn = new TableColumn<>("User Agent");
        userAgentColumn.setCellValueFactory(o -> o.getValue().userAgentProperty());
        userAgentColumn.setMinWidth(200);
        tableView.getColumns().add(userAgentColumn);
    }

    public static void buildUserAvatarsTableView(TableView<AvatarAssignmentFX> tableView, ObservableList<AvatarAssignmentFX> data) {
        tableView.setItems(data);

        TableColumn<AvatarAssignmentFX, String> idColumn = new TableColumn<>("Assignment ID");
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setMinWidth(140);
        tableView.getColumns().add(idColumn);

        TableColumn<AvatarAssignmentFX, String> avatarIdColumn = new TableColumn<>("Avatar ID");
        avatarIdColumn.setCellValueFactory(o -> o.getValue().avatarProperty().get().idProperty());
        avatarIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        avatarIdColumn.setMinWidth(50);
        tableView.getColumns().add(avatarIdColumn);

        TableColumn<AvatarAssignmentFX, String> previewColumn = new TableColumn<>("Preview");
        previewColumn.setCellValueFactory(o -> o.getValue().avatarProperty().get().urlProperty());
        previewColumn.setCellFactory(param -> new UrlImageViewTableCell<>());
        previewColumn.setMinWidth(50);
        tableView.getColumns().add(previewColumn);

        TableColumn<AvatarAssignmentFX, String> tooltipColumn = new TableColumn<>("Tooltip");
        tooltipColumn.setCellValueFactory(o -> o.getValue().avatarProperty().get().tooltipProperty());
        tooltipColumn.setMinWidth(100);
        tableView.getColumns().add(tooltipColumn);

        TableColumn<AvatarAssignmentFX, Boolean> selectedColumn = new TableColumn<>("Selected");
        selectedColumn.setCellValueFactory(o -> o.getValue().selectedProperty());
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setMinWidth(50);
        tableView.getColumns().add(selectedColumn);

        TableColumn<AvatarAssignmentFX, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires At");
        expiresAtColumn.setCellValueFactory(o -> o.getValue().expiresAtProperty());
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

    public static void buildMapTableView(TableView<MapFX> tableView, ObservableList<MapFX> data) {
        tableView.setItems(data);

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

    public static void buildMapVersionTableView(TableView<MapVersionFX> tableView, ObservableList<MapVersionFX> data) {
        tableView.setItems(data);

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

    public static void buildNotesTableView(TableView<UserNoteFX> tableView, ObservableList<UserNoteFX> data, boolean includeUserId) {
        tableView.setItems(data);

        TableColumn<UserNoteFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        tableView.getColumns().add(idColumn);

        if (includeUserId) {
            TableColumn<UserNoteFX, String> userColumn = new TableColumn<>("Player");
            userColumn.setCellValueFactory(o -> {
                PlayerFX user = o.getValue().getPlayer();

                SimpleStringProperty simpleStringProperty = new SimpleStringProperty();
                simpleStringProperty.bind(Bindings.createStringBinding(() -> user.getLogin() + " [id " + user.getId() + "]",
                        user.loginProperty(), user.idProperty()));
                return simpleStringProperty;
            });
            tableView.getColumns().add(userColumn);
        }

        TableColumn<UserNoteFX, Boolean> watchedCheckBoxColumn = new TableColumn<>("Watched");
        watchedCheckBoxColumn.setCellValueFactory(param -> param.getValue().watchedProperty());
        watchedCheckBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(watchedCheckBoxColumn));
        watchedCheckBoxColumn.setEditable(true);
        tableView.getColumns().add(watchedCheckBoxColumn);

        TableColumn<UserNoteFX, String> noteColumn = new TableColumn<>("Note");
        noteColumn.setCellValueFactory(param -> param.getValue().noteProperty());
        noteColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        noteColumn.setEditable(true);
        noteColumn.setMinWidth(600);
        tableView.getColumns().add(noteColumn);

        TableColumn<UserNoteFX, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(o -> {
            PlayerFX author = o.getValue().getAuthor();

            SimpleStringProperty simpleStringProperty = new SimpleStringProperty();
            simpleStringProperty.bind(Bindings.createStringBinding(() -> author.getLogin() + " [id " + author.getId() + "]",
                    author.loginProperty(), author.idProperty()));
            return simpleStringProperty;
        });
        authorColumn.setMinWidth(150);
        tableView.getColumns().add(authorColumn);

        TableColumn<UserNoteFX, OffsetDateTime> createTimeColumn = new TableColumn<>("Created");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<UserNoteFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);
    }
}
