package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.*;
import com.faforever.commons.api.dto.Map;
import com.faforever.moderatorclient.api.domain.VotingService;
import com.faforever.moderatorclient.mapstruct.VotingChoiceFX;
import com.faforever.moderatorclient.mapstruct.VotingQuestionFX;
import com.faforever.moderatorclient.mapstruct.VotingSubjectFX;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.slf4j.Logger;

import java.net.URL;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ViewHelper {
    private ViewHelper() {
        // static class
    }

    /**
     * Adds a context menu to a table view for predefined columns (with extractors for the columns)
     *
     * @param tableView  that the context menu will be added to
     * @param extractors contains a map of columns to copy along with a value extraction function
     */
    public static <T> void applyCopyContextMenus(TableView<T> tableView, java.util.Map<TableColumn<T, ?>, Function<T, ?>> extractors) {
        ContextMenu contextMenu = new ContextMenu();

        for (TableColumn<?, ?> column : tableView.getColumns()) {
            if (extractors.containsKey(column)) {
                MenuItem menuItem = new MenuItem("Copy " + column.getText());
                menuItem.setOnAction(event -> {
                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                    final ClipboardContent content = new ClipboardContent();

                    T selectedItem = (T) column.getTableView().getSelectionModel().getSelectedItem();
                    if (selectedItem == null) {
                        return;
                    }

                    Object result = extractors.get(column).apply(selectedItem);

                    if (result != null) {
                        content.putString(result.toString());
                        clipboard.setContent(content);
                    }
                });
                contextMenu.getItems().add(menuItem);
            }


        }

        tableView.setContextMenu(contextMenu);
    }

    public static void buildAvatarTableView(TableView<AvatarFX> tableView, ObservableList<AvatarFX> data) {
        tableView.setItems(data);
        HashMap<TableColumn<AvatarFX, ?>, Function<AvatarFX, ?>> extractors = new HashMap<>();

        TableColumn<AvatarFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, AvatarFX::getId);

        TableColumn<AvatarFX, String> previewColumn = new TableColumn<>("Preview");
        previewColumn.setCellValueFactory(o -> o.getValue().urlProperty());
        previewColumn.setCellFactory(param -> new UrlImageViewTableCell<>());
        previewColumn.setMinWidth(50);
        tableView.getColumns().add(previewColumn);

        TableColumn<AvatarFX, String> tooltipColumn = new TableColumn<>("Tooltip");
        tooltipColumn.setCellValueFactory(o -> o.getValue().tooltipProperty());
        tooltipColumn.setMinWidth(50);
        tableView.getColumns().add(tooltipColumn);
        extractors.put(tooltipColumn, AvatarFX::getTooltip);

        TableColumn<AvatarFX, OffsetDateTime> changeTimeColumn = new TableColumn<>("Created");
        changeTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<AvatarFX, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(o -> o.getValue().urlProperty());
        urlColumn.setMinWidth(50);
        tableView.getColumns().add(urlColumn);
        extractors.put(urlColumn, AvatarFX::getUrl);

        applyCopyContextMenus(tableView, extractors);
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

    public static void buildAvatarAssignmentTableView(TableView<AvatarAssignmentFX> tableView, ObservableList<AvatarAssignmentFX> data) {
        tableView.setItems(data);
        HashMap<TableColumn<AvatarAssignmentFX, ?>, Function<AvatarAssignmentFX, ?>> extractors = new HashMap<>();

        TableColumn<AvatarAssignmentFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, AvatarAssignmentFX::getId);

        TableColumn<AvatarAssignmentFX, String> userIdColumn = new TableColumn<>("User ID");
        userIdColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(avatarAssignment -> avatarAssignment.getPlayer().getId())
                        .orElse(""))
        );
        userIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        userIdColumn.setMinWidth(50);
        tableView.getColumns().add(userIdColumn);
        extractors.put(userIdColumn, avatarAssignmentFX -> avatarAssignmentFX.getPlayer().getId());

        TableColumn<AvatarAssignmentFX, String> userNameColumn = new TableColumn<>("User name");
        userNameColumn.setCellValueFactory(o -> o.getValue().playerProperty().get().representationProperty());
        userNameColumn.setMinWidth(150);
        tableView.getColumns().add(userNameColumn);
        extractors.put(userNameColumn, avatarAssignmentFX -> avatarAssignmentFX.getPlayer().getLogin());

        TableColumn<AvatarAssignmentFX, Boolean> selectedColumn = new TableColumn<>("Selected");
        selectedColumn.setCellValueFactory(o -> o.getValue().selectedProperty());
        selectedColumn.setMinWidth(50);
        tableView.getColumns().add(selectedColumn);

        TableColumn<AvatarAssignmentFX, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires at");
        expiresAtColumn.setCellValueFactory(o -> o.getValue().expiresAtProperty());
        expiresAtColumn.setMinWidth(180);
        tableView.getColumns().add(expiresAtColumn);
        extractors.put(expiresAtColumn, AvatarAssignmentFX::getExpiresAt);

        TableColumn<AvatarAssignmentFX, OffsetDateTime> assignedAtColumn = new TableColumn<>("Assigned at");
        assignedAtColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        assignedAtColumn.setMinWidth(180);
        tableView.getColumns().add(assignedAtColumn);

        applyCopyContextMenus(tableView, extractors);
    }

    public static void buildBanTableView(TableView<BanInfoFX> tableView, ObservableList<BanInfoFX> data, boolean showAffectedPlayerInfo) {
        tableView.setItems(data);
        HashMap<TableColumn<BanInfoFX, ?>, Function<BanInfoFX, ?>> extractors = new HashMap<>();

        TableColumn<BanInfoFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, BanInfoFX::getId);

        TableColumn<BanInfoFX, BanLevel> banLevelColumn = new TableColumn<>("Level");
        banLevelColumn.setCellValueFactory(o -> o.getValue().levelProperty());
        banLevelColumn.setMinWidth(80);
        tableView.getColumns().add(banLevelColumn);
        extractors.put(banLevelColumn, BanInfoFX::getLevel);

        TableColumn<BanInfoFX, BanStatus> banStatusColumn = new TableColumn<>("Status");
        banStatusColumn.setCellValueFactory(o -> o.getValue().banStatusProperty());
        banStatusColumn.setMinWidth(100);
        tableView.getColumns().add(banStatusColumn);
        extractors.put(banStatusColumn, BanInfoFX::getBanStatus);

        TableColumn<BanInfoFX, BanDurationType> banDurationColumn = new TableColumn<>("Duration");
        banDurationColumn.setCellValueFactory(o -> o.getValue().durationProperty());
        banDurationColumn.setMinWidth(100);
        tableView.getColumns().add(banDurationColumn);
        extractors.put(banDurationColumn, BanInfoFX::getDuration);

        if (showAffectedPlayerInfo) {
            TableColumn<BanInfoFX, String> affectedPlayerColumn = new TableColumn<>("Affected Player");
            affectedPlayerColumn.setCellValueFactory(o -> o.getValue().getPlayer().representationProperty());
            affectedPlayerColumn.setMinWidth(100);
            tableView.getColumns().add(affectedPlayerColumn);
            extractors.put(affectedPlayerColumn, banInfoFX -> banInfoFX.getPlayer().getLogin());
        }

        TableColumn<BanInfoFX, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires at");
        expiresAtColumn.setCellValueFactory(o -> o.getValue().expiresAtProperty());
        expiresAtColumn.setMinWidth(180);
        tableView.getColumns().add(expiresAtColumn);
        extractors.put(expiresAtColumn, BanInfoFX::getExpiresAt);

        TableColumn<BanInfoFX, String> reasonColumn = new TableColumn<>("Reason");
        reasonColumn.setCellValueFactory(o -> o.getValue().reasonProperty());
        reasonColumn.setMinWidth(250);
        tableView.getColumns().add(reasonColumn);
        extractors.put(reasonColumn, BanInfoFX::getReason);

        TableColumn<BanInfoFX, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(o -> o.getValue().authorProperty().get().representationProperty());
        authorColumn.setMinWidth(150);
        tableView.getColumns().add(authorColumn);
        extractors.put(authorColumn, banInfoFX -> banInfoFX.getAuthor().getLogin());

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
        extractors.put(revokeReasonColumn, banInfoFX -> banInfoFX.getBanRevokeData() == null ? null : banInfoFX.getBanRevokeData().getReason());

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
        extractors.put(revokeAuthorColumn, banInfoFX -> banInfoFX.getBanRevokeData() == null ? null : banInfoFX.getBanRevokeData().getAuthor().getLogin());

        TableColumn<BanInfoFX, OffsetDateTime> changeTimeColumn = new TableColumn<>("Created Time");
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<BanInfoFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Update (Revoke) Time");
        updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
        updateTimeColumn.setMinWidth(180);
        tableView.getColumns().add(updateTimeColumn);

        applyCopyContextMenus(tableView, extractors);
    }

    public static void buildNameHistoryTableView(TableView<NameRecordFX> tableView, ObservableList<NameRecordFX> data) {
        tableView.setItems(data);
        HashMap<TableColumn<NameRecordFX, ?>, Function<NameRecordFX, ?>> extractors = new HashMap<>();

        TableColumn<NameRecordFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, NameRecordFX::getId);

        TableColumn<NameRecordFX, OffsetDateTime> changeTimeColumn = new TableColumn<>("Change Time");
        changeTimeColumn.setCellValueFactory(o -> o.getValue().changeTimeProperty());
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);
        extractors.put(changeTimeColumn, NameRecordFX::getChangeTime);

        TableColumn<NameRecordFX, String> nameColumn = new TableColumn<>("Previous Name");
        nameColumn.setCellValueFactory(o -> o.getValue().nameProperty());
        nameColumn.setMinWidth(200);
        tableView.getColumns().add(nameColumn);
        extractors.put(nameColumn, NameRecordFX::getName);

        applyCopyContextMenus(tableView, extractors);
    }

    /**
     * @param tableView  The tableView to be populated
     * @param data       data to be put in the tableView
     * @param showKiller whether to show the killer
     * @param onAddBan   if not null shows a ban button which triggers this consumer
     */
    public static void buildTeamkillTableView(TableView<TeamkillFX> tableView, ObservableList<TeamkillFX> data, boolean showKiller, Consumer<PlayerFX> onAddBan) {
        tableView.setItems(data);
        HashMap<TableColumn<TeamkillFX, ?>, Function<TeamkillFX, ?>> extractors = new HashMap<>();

        //Add context menu to copy username and ip
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyUsername = new MenuItem("Copy killer name");
        copyUsername.setOnAction(event -> {
            TeamkillFX teamkillFX = tableView.getSelectionModel().getSelectedItem();
            if (teamkillFX == null) return;
            toClipBoard(teamkillFX.getTeamkiller().getLogin());
        });

        contextMenu.getItems().add(copyUsername);
        tableView.setContextMenu(contextMenu);

        TableColumn<TeamkillFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(50);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, TeamkillFX::getId);

        if (showKiller) {
            TableColumn<TeamkillFX, String> killerColumn = new TableColumn<>("Killer");
            killerColumn.setCellValueFactory(o -> o.getValue().teamkillerProperty().get().representationProperty());
            killerColumn.setMinWidth(180);
            tableView.getColumns().add(killerColumn);
            extractors.put(killerColumn, teamkillFX -> teamkillFX.getTeamkiller().getLogin());
        }

        TableColumn<TeamkillFX, String> victimColumn = new TableColumn<>("Victim");
        victimColumn.setCellValueFactory(o -> o.getValue().victimProperty().get().representationProperty());
        victimColumn.setMinWidth(180);
        tableView.getColumns().add(victimColumn);
        extractors.put(victimColumn, teamkillFX -> teamkillFX.getVictim().getLogin());

        TableColumn<TeamkillFX, String> gameIdColumn = new TableColumn<>("Game ID");
        gameIdColumn.setCellValueFactory(o -> o.getValue().gameProperty().get().idProperty());
        gameIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        gameIdColumn.setMinWidth(100);
        tableView.getColumns().add(gameIdColumn);
        extractors.put(gameIdColumn, TeamkillFX::getGame);

        TableColumn<TeamkillFX, Number> gameTimeColumn = new TableColumn<>("Game Time");
        gameTimeColumn.setCellValueFactory(o -> o.getValue().gameTimeProperty());
        gameTimeColumn.setMinWidth(100);
        tableView.getColumns().add(gameTimeColumn);
        extractors.put(gameTimeColumn, TeamkillFX::getGameTime);

        TableColumn<TeamkillFX, OffsetDateTime> reportedAtColumn = new TableColumn<>("Reported At");
        reportedAtColumn.setCellValueFactory(o -> o.getValue().reportedAtProperty());
        reportedAtColumn.setMinWidth(180);
        tableView.getColumns().add(reportedAtColumn);
        extractors.put(reportedAtColumn, TeamkillFX::getReportedAt);

        if (onAddBan != null) {
            TableColumn<TeamkillFX, TeamkillFX> banOptionColumn = new TableColumn<>("Ban");
            banOptionColumn.setMinWidth(150);
            banOptionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
            banOptionColumn.setCellFactory(param -> new TableCell<TeamkillFX, TeamkillFX>() {

                @Override
                protected void updateItem(TeamkillFX item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        PlayerFX teamkiller = item.getTeamkiller();
                        if (!teamkiller.isBannedGlobally()) {
                            Button button = new Button("Add ban to killer");
                            button.setOnMouseClicked(event -> onAddBan.accept(teamkiller));
                            setGraphic(button);
                            return;
                        }
                    }
                    setGraphic(null);
                }
            });
            tableView.getColumns().add(banOptionColumn);
        }

        applyCopyContextMenus(tableView, extractors);
    }

    /**
     * @param tableView The tableview to be populated
     * @param data      data to be put in the tableView
     * @param onAddBan  if not null shows a ban button which triggers this consumer
     */
    public static void buildUserTableView(TableView<PlayerFX> tableView, ObservableList<PlayerFX> data, Consumer<PlayerFX> onAddBan) {
        tableView.setItems(data);
        HashMap<TableColumn<PlayerFX, ?>, Function<PlayerFX, ?>> extractors = new HashMap<>();

        //Add context menu to copy username and ip
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyUsername = new MenuItem("Copy username");
        copyUsername.setOnAction(event -> {
            PlayerFX selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;
            toClipBoard(selectedItem.getLogin());
        });
        MenuItem copyIp = new MenuItem("Copy ip");
        copyIp.setOnAction(event -> {
            PlayerFX selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;
            toClipBoard(selectedItem.getRecentIpAddress());
        });
        contextMenu.getItems().addAll(Arrays.asList(copyUsername, copyIp));
        tableView.setContextMenu(contextMenu);

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
        extractors.put(idColumn, PlayerFX::getId);

        TableColumn<PlayerFX, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(o -> o.getValue().loginProperty());
        nameColumn.setMinWidth(150);
        tableView.getColumns().add(nameColumn);
        extractors.put(nameColumn, PlayerFX::getLogin);

        TableColumn<PlayerFX, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(o -> o.getValue().emailProperty());
        emailColumn.setMinWidth(250);
        tableView.getColumns().add(emailColumn);
        extractors.put(emailColumn, PlayerFX::getEmail);

        TableColumn<PlayerFX, String> steamIdColumn = new TableColumn<>("Steam ID");
        steamIdColumn.setCellValueFactory(o -> o.getValue().steamIdProperty());
        steamIdColumn.setMinWidth(150);
        tableView.getColumns().add(steamIdColumn);
        extractors.put(steamIdColumn, PlayerFX::getSteamId);

        TableColumn<PlayerFX, String> ipColumn = new TableColumn<>("Recent IP Address");
        ipColumn.setCellValueFactory(o -> o.getValue().recentIpAddressProperty());
        ipColumn.setMinWidth(160);
        tableView.getColumns().add(ipColumn);
        extractors.put(ipColumn, PlayerFX::getRecentIpAddress);

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
        extractors.put(userAgentColumn, PlayerFX::getUserAgent);

        if (onAddBan != null) {
            TableColumn<PlayerFX, PlayerFX> banOptionColumn = new TableColumn<>("Ban");
            banOptionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
            banOptionColumn.setCellFactory(param -> new TableCell<PlayerFX, PlayerFX>() {

                @Override
                protected void updateItem(PlayerFX item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        if (!item.isBannedGlobally()) {
                            Button button = new Button("Add ban");
                            button.setOnMouseClicked(event -> onAddBan.accept(item));
                            setGraphic(button);
                            return;
                        }
                    }
                    setGraphic(null);
                }
            });
            tableView.getColumns().add(banOptionColumn);
        }

        applyCopyContextMenus(tableView, extractors);
    }

    private static void toClipBoard(String contentString) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(contentString);
        clipboard.setContent(content);
    }

    public static void buildUserAvatarsTableView(TableView<AvatarAssignmentFX> tableView, ObservableList<AvatarAssignmentFX> data) {
        tableView.setItems(data);
        HashMap<TableColumn<AvatarAssignmentFX, ?>, Function<AvatarAssignmentFX, ?>> extractors = new HashMap<>();

        TableColumn<AvatarAssignmentFX, String> idColumn = new TableColumn<>("Assignment ID");
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setMinWidth(140);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, AvatarAssignmentFX::getId);

        TableColumn<AvatarAssignmentFX, String> avatarIdColumn = new TableColumn<>("Avatar ID");
        avatarIdColumn.setCellValueFactory(o -> o.getValue().avatarProperty().get().idProperty());
        avatarIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        avatarIdColumn.setMinWidth(50);
        tableView.getColumns().add(avatarIdColumn);
        extractors.put(avatarIdColumn, avatarAssignmentFX -> avatarAssignmentFX.getAvatar().getId());

        TableColumn<AvatarAssignmentFX, String> previewColumn = new TableColumn<>("Preview");
        previewColumn.setCellValueFactory(o -> o.getValue().avatarProperty().get().urlProperty());
        previewColumn.setCellFactory(param -> new UrlImageViewTableCell<>());
        previewColumn.setMinWidth(50);
        tableView.getColumns().add(previewColumn);
        extractors.put(previewColumn, avatarAssignmentFX -> avatarAssignmentFX.getAvatar().getUrl());

        TableColumn<AvatarAssignmentFX, String> tooltipColumn = new TableColumn<>("Tooltip");
        tooltipColumn.setCellValueFactory(o -> o.getValue().avatarProperty().get().tooltipProperty());
        tooltipColumn.setMinWidth(100);
        tableView.getColumns().add(tooltipColumn);
        extractors.put(tooltipColumn, avatarAssignmentFX -> avatarAssignmentFX.getAvatar().getTooltip());

        TableColumn<AvatarAssignmentFX, Boolean> selectedColumn = new TableColumn<>("Selected");
        selectedColumn.setCellValueFactory(o -> o.getValue().selectedProperty());
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        selectedColumn.setMinWidth(50);
        tableView.getColumns().add(selectedColumn);

        TableColumn<AvatarAssignmentFX, OffsetDateTime> expiresAtColumn = new TableColumn<>("Expires At");
        expiresAtColumn.setCellValueFactory(o -> o.getValue().expiresAtProperty());
        expiresAtColumn.setMinWidth(180);
        tableView.getColumns().add(expiresAtColumn);
        extractors.put(tooltipColumn, AvatarAssignmentFX::getExpiresAt);

        applyCopyContextMenus(tableView, extractors);
    }

    public static void buildMapTableView(TableView<MapFX> tableView, ObservableList<MapFX> data) {
        tableView.setItems(data);
        HashMap<TableColumn<MapFX, ?>, Function<MapFX, ?>> extractors = new HashMap<>();

        TableColumn<MapFX, String> idColumn = new TableColumn<>("Map ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(100);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, MapFX::getId);

        TableColumn<MapFX, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(o -> o.getValue().displayNameProperty());
        nameColumn.setMinWidth(200);
        tableView.getColumns().add(nameColumn);
        extractors.put(nameColumn, MapFX::getDisplayName);


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
        extractors.put(authorColumn, mapFX -> mapFX.getAuthor().getLogin());

        TableColumn<MapFX, OffsetDateTime> createTimeColumn = new TableColumn<>("First upload");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<MapFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);

        applyCopyContextMenus(tableView, extractors);
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

    public static void buildPlayersGamesTable(TableView<GamePlayerStatsFX> tableView, String replayDownloadFormat, PlatformService platformService) {
        HashMap<TableColumn<GamePlayerStatsFX, ?>, Function<GamePlayerStatsFX, ?>> extractors = new HashMap<>();

        TableColumn<GamePlayerStatsFX, String> gameIdColumn = new TableColumn<>("Game ID");
        gameIdColumn.setCellValueFactory(o -> o.getValue().getGame().idProperty());
        gameIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        gameIdColumn.setMinWidth(100);
        tableView.getColumns().add(gameIdColumn);
        extractors.put(gameIdColumn, gamePlayerStatsFX -> gamePlayerStatsFX.getGame().getId());

        TableColumn<GamePlayerStatsFX, String> gameNameColumn = new TableColumn<>("Game Name");
        gameNameColumn.setCellValueFactory(o -> o.getValue().getGame().nameProperty());
        gameNameColumn.setMinWidth(100);
        tableView.getColumns().add(gameNameColumn);
        extractors.put(gameNameColumn, gamePlayerStatsFX -> gamePlayerStatsFX.getGame().getName());

        TableColumn<GamePlayerStatsFX, String> rankedColumn = new TableColumn<>("Game Validity");
        rankedColumn.setCellValueFactory(o -> new SimpleObjectProperty<>(
                o.getValue().getGame().getValidity().name()
        ));
        rankedColumn.setComparator(Comparator.naturalOrder());
        rankedColumn.setMinWidth(120);
        tableView.getColumns().add(rankedColumn);
        extractors.put(rankedColumn, gamePlayerStatsFX -> gamePlayerStatsFX.getGame().getValidity());

        TableColumn<GamePlayerStatsFX, Number> beforeGameRatingColumn = new TableColumn<>("Rating Before Game");
        beforeGameRatingColumn.setCellValueFactory(o -> o.getValue().beforeRatingProperty());
        beforeGameRatingColumn.setMinWidth(150);
        tableView.getColumns().add(beforeGameRatingColumn);
        extractors.put(beforeGameRatingColumn, GamePlayerStatsFX::getBeforeRating);

        TableColumn<GamePlayerStatsFX, Number> afterGameRatingColumn = new TableColumn<>("Rating Change");
        afterGameRatingColumn.setCellValueFactory(o -> o.getValue().ratingChangeProperty());
        afterGameRatingColumn.setMinWidth(100);
        tableView.getColumns().add(afterGameRatingColumn);
        extractors.put(afterGameRatingColumn, GamePlayerStatsFX::getRatingChange);

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

        applyCopyContextMenus(tableView, extractors);
    }

    public static void buildMapVersionTableView(TableView<MapVersionFX> tableView, ObservableList<MapVersionFX> data) {
        tableView.setItems(data);
        HashMap<TableColumn<MapVersionFX, ?>, Function<MapVersionFX, ?>> extractors = new HashMap<>();

        TableColumn<MapVersionFX, String> idColumn = new TableColumn<>("Version ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(100);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, MapVersionFX::getId);

        TableColumn<MapVersionFX, ComparableVersion> nameColumn = new TableColumn<>("Version No.");
        nameColumn.setCellValueFactory(o -> o.getValue().versionProperty());
        nameColumn.setMinWidth(100);
        tableView.getColumns().add(nameColumn);
        extractors.put(nameColumn, MapVersionFX::getVersion);

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
        extractors.put(descriptionColumn, MapVersionFX::getDescription);


        TableColumn<MapVersionFX, OffsetDateTime> createTimeColumn = new TableColumn<>("Uploaded");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<MapVersionFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);

        applyCopyContextMenus(tableView, extractors);
    }

    public static void buildNotesTableView(TableView<UserNoteFX> tableView, ObservableList<UserNoteFX> data, boolean includeUserId) {
        tableView.setItems(data);
        HashMap<TableColumn<UserNoteFX, ?>, Function<UserNoteFX, ?>> extractors = new HashMap<>();

        TableColumn<UserNoteFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, UserNoteFX::getId);

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
            extractors.put(userColumn, userNoteFX -> userNoteFX.getPlayer().getLogin());
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
        extractors.put(noteColumn, UserNoteFX::getNote);

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
        extractors.put(authorColumn, userNoteFX -> userNoteFX.getAuthor().getLogin());

        TableColumn<UserNoteFX, OffsetDateTime> createTimeColumn = new TableColumn<>("Created");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<UserNoteFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);

        applyCopyContextMenus(tableView, extractors);
    }

    public static void fillMapTreeView(TreeTableView<MapTableItemAdapter> mapTreeView, Stream<Map> mapStream) {
        mapStream.forEach(map -> {
            TreeItem<MapTableItemAdapter> mapItem = new TreeItem<>(new MapTableItemAdapter(map));
            mapTreeView.getRoot().getChildren().add(mapItem);

            map.getVersions().forEach(mapVersion -> mapItem.getChildren().add(new TreeItem<>(new MapTableItemAdapter(mapVersion))));
        });
    }

    public static void buildSubjectTable(TableView<VotingSubjectFX> tableView, VotingService votingService, Logger log, Runnable refresh) {
        tableView.setEditable(true);
        HashMap<TableColumn<VotingSubjectFX, ?>, Function<VotingSubjectFX, ?>> extractors = new HashMap<>();

        TableColumn<VotingSubjectFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, VotingSubjectFX::getId);

        TableColumn<VotingSubjectFX, String> subjectKeyColumn = new TableColumn<>("Subject key ✏");
        subjectKeyColumn.setCellValueFactory(param -> param.getValue().subjectKeyProperty());
        subjectKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        subjectKeyColumn.setEditable(true);
        subjectKeyColumn.getStyleClass().add("editable");
        subjectKeyColumn.setMinWidth(100);
        tableView.getColumns().add(subjectKeyColumn);
        subjectKeyColumn.setOnEditCommit(event -> {
            VotingSubjectFX rowValue = event.getRowValue();
            VotingSubject votingSubject = new VotingSubject();
            votingSubject.setId(rowValue.getId());
            votingSubject.setSubjectKey(event.getNewValue());
            votingService.update(votingSubject);
            refresh.run();
        });
        extractors.put(subjectKeyColumn, VotingSubjectFX::getSubjectKey);

        TableColumn<VotingSubjectFX, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(param -> param.getValue().subjectProperty());
        subjectColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        subjectColumn.setEditable(false);
        subjectColumn.setMinWidth(150);
        tableView.getColumns().add(subjectColumn);
        extractors.put(subjectColumn, VotingSubjectFX::getSubject);

        TableColumn<VotingSubjectFX, String> descriptionKeyColumn = new TableColumn<>("Description Key ✏");
        descriptionKeyColumn.setCellValueFactory(param -> param.getValue().descriptionKeyProperty());
        descriptionKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        descriptionKeyColumn.setEditable(true);
        descriptionKeyColumn.setMinWidth(150);
        descriptionKeyColumn.getStyleClass().add("editable");
        tableView.getColumns().add(descriptionKeyColumn);
        descriptionKeyColumn.setOnEditCommit(event -> {
            VotingSubjectFX rowValue = event.getRowValue();
            VotingSubject votingSubject = new VotingSubject();
            votingSubject.setId(rowValue.getId());
            votingSubject.setDescriptionKey(event.getNewValue());
            votingService.update(votingSubject);
            refresh.run();
        });
        extractors.put(descriptionKeyColumn, VotingSubjectFX::getDescriptionKey);

        TableColumn<VotingSubjectFX, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        descriptionColumn.setEditable(false);
        descriptionColumn.setMinWidth(150);
        tableView.getColumns().add(descriptionColumn);
        extractors.put(descriptionKeyColumn, VotingSubjectFX::getDescription);

        TableColumn<VotingSubjectFX, Number> numberOfVotesColumn = new TableColumn<>("Number of votes");
        numberOfVotesColumn.setCellValueFactory(param -> param.getValue().numberOfVotesProperty());
        numberOfVotesColumn.setEditable(false);
        numberOfVotesColumn.setMinWidth(150);
        tableView.getColumns().add(numberOfVotesColumn);
        extractors.put(numberOfVotesColumn, VotingSubjectFX::getNumberOfVotes);

        TableColumn<VotingSubjectFX, String> topicUrlColumn = new TableColumn<>("Topic URL ✏");
        topicUrlColumn.setCellValueFactory(param -> param.getValue().topicUrlProperty());
        topicUrlColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        topicUrlColumn.setEditable(true);
        topicUrlColumn.getStyleClass().add("editable");
        topicUrlColumn.setMinWidth(150);
        tableView.getColumns().add(topicUrlColumn);
        topicUrlColumn.setOnEditCommit(event -> {
            VotingSubjectFX rowValue = event.getRowValue();
            VotingSubject votingSubject = new VotingSubject();
            votingSubject.setId(rowValue.getId());
            votingSubject.setTopicUrl(event.getNewValue());
            votingService.update(votingSubject);
            refresh.run();
        });
        extractors.put(topicUrlColumn, VotingSubjectFX::getTopicUrl);

        TableColumn<VotingSubjectFX, Boolean> revealColumn = new TableColumn<>("Results Revealed");
        revealColumn.setCellValueFactory(param -> param.getValue().revealWinnerProperty());
        Callback<TableColumn<VotingSubjectFX, Boolean>, TableCell<VotingSubjectFX, Boolean>> value = CheckBoxTableCell.forTableColumn(revealColumn);
        revealColumn.setCellFactory(value);
        revealColumn.setEditable(false);
        revealColumn.setMinWidth(10);
        tableView.getColumns().add(revealColumn);

        TableColumn<VotingSubjectFX, String> endDateColumn = new TableColumn<>("End Date");
        endDateColumn.setCellValueFactory(param -> param.getValue().endOfVoteTimeProperty().asString());
        endDateColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        endDateColumn.setEditable(false);
        endDateColumn.setMinWidth(150);
        tableView.getColumns().add(endDateColumn);
        extractors.put(endDateColumn, VotingSubjectFX::getEndOfVoteTime);

        TableColumn<VotingSubjectFX, String> beginDateColumn = new TableColumn<>("Begin Date");
        beginDateColumn.setCellValueFactory(param -> param.getValue().beginOfVoteTimeProperty().asString());
        beginDateColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        beginDateColumn.setEditable(false);
        beginDateColumn.setMinWidth(150);
        tableView.getColumns().add(beginDateColumn);
        extractors.put(beginDateColumn, VotingSubjectFX::getBeginOfVoteTime);

        TableColumn<VotingSubjectFX, Number> minGamesToVoteColumn = new TableColumn<>("Min games to vote ✏");
        minGamesToVoteColumn.setCellValueFactory(param -> param.getValue().minGamesToVoteProperty());
        setStringCellFactory(log, minGamesToVoteColumn);
        minGamesToVoteColumn.setEditable(true);
        minGamesToVoteColumn.getStyleClass().add("editable");
        minGamesToVoteColumn.setMinWidth(20);
        tableView.getColumns().add(minGamesToVoteColumn);
        minGamesToVoteColumn.setOnEditCommit(event -> {
            VotingSubjectFX rowValue = event.getRowValue();
            VotingSubject votingSubject = new VotingSubject();
            votingSubject.setId(rowValue.getId());
            votingSubject.setMinGamesToVote(event.getNewValue().intValue());
            votingService.update(votingSubject);
            refresh.run();
        });
        extractors.put(minGamesToVoteColumn, VotingSubjectFX::getMinGamesToVote);

        applyCopyContextMenus(tableView, extractors);
    }

    public static void buildQuestionTable(TableView<VotingQuestionFX> tableView, VotingService votingService, Logger log, Runnable refresh) {
        tableView.setEditable(true);
        HashMap<TableColumn<VotingQuestionFX, ?>, Function<VotingQuestionFX, ?>> extractors = new HashMap<>();

        TableColumn<VotingQuestionFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, VotingQuestionFX::getId);

        TableColumn<VotingQuestionFX, String> questionKeyColumn = new TableColumn<>("Question key ✏");
        questionKeyColumn.setCellValueFactory(param -> param.getValue().questionKeyProperty());
        questionKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        questionKeyColumn.setEditable(true);
        questionKeyColumn.getStyleClass().add("editable");
        questionKeyColumn.setMinWidth(100);
        tableView.getColumns().add(questionKeyColumn);
        questionKeyColumn.setOnEditCommit(event -> {
            VotingQuestionFX rowValue = event.getRowValue();
            VotingQuestion votingQuestion = new VotingQuestion();
            votingQuestion.setId(rowValue.getId());
            votingQuestion.setQuestionKey(event.getNewValue());
            votingService.update(votingQuestion);
            refresh.run();
        });
        extractors.put(questionKeyColumn, VotingQuestionFX::getQuestionKey);

        TableColumn<VotingQuestionFX, String> questionColumn = new TableColumn<>("Question");
        questionColumn.setCellValueFactory(param -> param.getValue().questionProperty());
        questionColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        questionColumn.setEditable(false);
        questionColumn.setMinWidth(150);
        tableView.getColumns().add(questionColumn);
        extractors.put(questionColumn, VotingQuestionFX::getQuestion);

        TableColumn<VotingQuestionFX, String> descriptionKeyColumn = new TableColumn<>("Description key ✏");
        descriptionKeyColumn.setCellValueFactory(param -> param.getValue().descriptionKeyProperty());
        descriptionKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        descriptionKeyColumn.setEditable(true);
        descriptionKeyColumn.getStyleClass().add("editable");
        descriptionKeyColumn.setMinWidth(100);
        tableView.getColumns().add(descriptionKeyColumn);
        descriptionKeyColumn.setOnEditCommit(event -> {
            VotingQuestionFX rowValue = event.getRowValue();
            VotingQuestion votingQuestion = new VotingQuestion();
            votingQuestion.setId(rowValue.getId());
            votingQuestion.setDescriptionKey(event.getNewValue());
            votingService.update(votingQuestion);
            refresh.run();
        });
        extractors.put(descriptionKeyColumn, VotingQuestionFX::getDescriptionKey);

        TableColumn<VotingQuestionFX, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        descriptionColumn.setEditable(false);
        descriptionColumn.setMinWidth(150);
        tableView.getColumns().add(descriptionColumn);
        extractors.put(descriptionColumn, VotingQuestionFX::getDescription);

        TableColumn<VotingQuestionFX, Boolean> launchColumn = new TableColumn<>("Alternative Question");
        launchColumn.setCellValueFactory(param -> param.getValue().alternativeQuestionProperty());
        Callback<TableColumn<VotingQuestionFX, Boolean>, TableCell<VotingQuestionFX, Boolean>> value = CheckBoxTableCell.forTableColumn(launchColumn);
        launchColumn.setCellFactory(value);
        launchColumn.setEditable(false);
        launchColumn.setMinWidth(10);
        tableView.getColumns().add(launchColumn);

        TableColumn<VotingQuestionFX, String> winnersColumn = new TableColumn<>("Winners");
        winnersColumn.setCellValueFactory(param -> {
            ObservableList<VotingChoiceFX> winners = param.getValue().getWinners();
            String winnerString = winners.stream()
                    .map(VotingChoiceFX::toString)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(winnerString);
        });
        winnersColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        winnersColumn.setEditable(false);
        winnersColumn.setMinWidth(150);
        tableView.getColumns().add(winnersColumn);
        extractors.put(winnersColumn, votingQuestionFX -> votingQuestionFX.getWinners().stream()
                .map(VotingChoiceFX::toString)
                .collect(Collectors.joining(", ")));

        TableColumn<VotingQuestionFX, Number> maxAnswersColumn = new TableColumn<>("Max Answers");
        maxAnswersColumn.setCellValueFactory(param -> param.getValue().maxAnswersProperty());
        setStringCellFactory(log, maxAnswersColumn);
        maxAnswersColumn.setEditable(false);
        maxAnswersColumn.setMinWidth(60);
        tableView.getColumns().add(maxAnswersColumn);
        extractors.put(maxAnswersColumn, VotingQuestionFX::getMaxAnswers);

        TableColumn<VotingQuestionFX, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getVotingSubject().toString()));
        subjectColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        subjectColumn.setEditable(false);
        subjectColumn.setMinWidth(150);
        tableView.getColumns().add(subjectColumn);
        extractors.put(subjectColumn, VotingQuestionFX::getVotingSubject);

        TableColumn<VotingQuestionFX, Number> numberAnswersColumn = new TableColumn<>("Number of answers");
        numberAnswersColumn.setCellValueFactory(param -> param.getValue().numberOfAnswersProperty());
        setStringCellFactory(log, numberAnswersColumn);
        numberAnswersColumn.setEditable(false);
        numberAnswersColumn.setMinWidth(100);
        tableView.getColumns().add(numberAnswersColumn);
        extractors.put(numberAnswersColumn, VotingQuestionFX::getNumberOfAnswers);

        applyCopyContextMenus(tableView, extractors);
    }

    private static void setStringCellFactory(Logger log, TableColumn<?, Number> minGamesToVoteColumn) {
        minGamesToVoteColumn.setCellFactory(TextAreaTableCell.forTableColumn(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object);
            }

            @Override
            public Number fromString(String string) {
                try {
                    int i = Integer.parseInt(string);
                    return i;
                } catch (Exception e) {
                    log.error("Error parsing integer for tutorial ordinal", e);
                }
                return 0;
            }
        }));
    }

    public static void buildChoiceTable(TableView<VotingChoiceFX> tableView, VotingService votingService, Logger log, Runnable refresh) {
        tableView.setEditable(true);
        HashMap<TableColumn<VotingChoiceFX, ?>, Function<VotingChoiceFX, ?>> extractors = new HashMap<>();

        TableColumn<VotingChoiceFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, VotingChoiceFX::getId);

        TableColumn<VotingChoiceFX, String> choiceKeyColumn = new TableColumn<>("Choice key ✏");
        choiceKeyColumn.setCellValueFactory(param -> param.getValue().choiceTextKeyProperty());
        choiceKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        choiceKeyColumn.setEditable(true);
        choiceKeyColumn.getStyleClass().add("editable");
        choiceKeyColumn.setMinWidth(100);
        tableView.getColumns().add(choiceKeyColumn);
        choiceKeyColumn.setOnEditCommit(event -> {
            VotingChoiceFX rowValue = event.getRowValue();
            VotingChoice votingChoice = new VotingChoice();
            votingChoice.setId(rowValue.getId());
            votingChoice.setChoiceTextKey(event.getNewValue());
            votingService.update(votingChoice);
            refresh.run();
        });
        extractors.put(choiceKeyColumn, VotingChoiceFX::getChoiceTextKey);

        TableColumn<VotingChoiceFX, String> choiceColumn = new TableColumn<>("Choice");
        choiceColumn.setCellValueFactory(param -> param.getValue().choiceTextProperty());
        choiceColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        choiceColumn.setEditable(false);
        choiceColumn.setMinWidth(150);
        tableView.getColumns().add(choiceColumn);
        extractors.put(choiceColumn, VotingChoiceFX::getChoiceText);

        TableColumn<VotingChoiceFX, String> descriptionKeyColumn = new TableColumn<>("Description key ✏");
        descriptionKeyColumn.setCellValueFactory(param -> param.getValue().descriptionKeyProperty());
        descriptionKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        descriptionKeyColumn.setEditable(true);
        descriptionKeyColumn.getStyleClass().add("editable");
        descriptionKeyColumn.setMinWidth(100);
        tableView.getColumns().add(descriptionKeyColumn);
        descriptionKeyColumn.setOnEditCommit(event -> {
            VotingChoiceFX rowValue = event.getRowValue();
            VotingChoice votingChoice = new VotingChoice();
            votingChoice.setId(rowValue.getId());
            votingChoice.setDescriptionKey(event.getNewValue());
            votingService.update(votingChoice);
            refresh.run();
        });
        extractors.put(descriptionKeyColumn, VotingChoiceFX::getDescriptionKey);

        TableColumn<VotingChoiceFX, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        descriptionColumn.setEditable(false);
        descriptionColumn.setMinWidth(150);
        tableView.getColumns().add(descriptionColumn);
        extractors.put(descriptionColumn, VotingChoiceFX::getDescription);

        TableColumn<VotingChoiceFX, Number> numberAnswersColumn = new TableColumn<>("Number of answers");
        numberAnswersColumn.setCellValueFactory(param -> param.getValue().numberOfAnswersProperty());
        setStringCellFactory(log, numberAnswersColumn);
        numberAnswersColumn.setEditable(false);
        numberAnswersColumn.setMinWidth(100);
        tableView.getColumns().add(numberAnswersColumn);
        extractors.put(numberAnswersColumn, VotingChoiceFX::getNumberOfAnswers);

        TableColumn<VotingChoiceFX, Number> ordinalColumn = new TableColumn<>("Ordinal");
        ordinalColumn.setCellValueFactory(param -> param.getValue().ordinalProperty());
        setStringCellFactory(log, ordinalColumn);
        ordinalColumn.setEditable(false);
        ordinalColumn.setMinWidth(100);
        tableView.getColumns().add(ordinalColumn);

        TableColumn<VotingChoiceFX, String> questionColumn = new TableColumn<>("Question");
        questionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getVotingQuestion().toString()));
        questionColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        questionColumn.setEditable(false);
        questionColumn.setMinWidth(150);
        tableView.getColumns().add(questionColumn);
        extractors.put(questionColumn, VotingChoiceFX::getVotingQuestion);

        applyCopyContextMenus(tableView, extractors);
    }

    interface ColumnExtractor<T, R> {
        R extract(TableColumn<T, R> column);
    }

    public static boolean confirmDialog(String title, String detail) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(detail);

        Optional<ButtonType> result = alert.showAndWait();
        return result.map(buttonType -> buttonType == ButtonType.OK).orElse(false);
    }

    public static void errorDialog(String title, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(detail);
        alert.showAndWait();
    }
}
