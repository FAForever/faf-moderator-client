package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.BanDurationType;
import com.faforever.commons.api.dto.BanLevel;
import com.faforever.commons.api.dto.BanStatus;
import com.faforever.commons.api.dto.Map;
import com.faforever.commons.api.dto.ModerationReportStatus;
import com.faforever.commons.api.dto.VotingChoice;
import com.faforever.commons.api.dto.VotingQuestion;
import com.faforever.commons.api.dto.VotingSubject;
import com.faforever.moderatorclient.api.domain.MessagesService;
import com.faforever.moderatorclient.api.domain.TutorialService;
import com.faforever.moderatorclient.api.domain.VotingService;
import com.faforever.moderatorclient.ui.caches.LargeThumbnailCache;
import com.faforever.moderatorclient.ui.data_cells.TextAreaTableCell;
import com.faforever.moderatorclient.ui.data_cells.UrlImageViewTableCell;
import com.faforever.moderatorclient.ui.domain.VotingChoiceFX;
import com.faforever.moderatorclient.ui.domain.VotingQuestionFX;
import com.faforever.moderatorclient.ui.domain.VotingSubjectFX;
import com.faforever.moderatorclient.ui.domain.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class ViewHelper {
    private ViewHelper() {
        // static class
    }

    @Autowired
    private static LargeThumbnailCache largeThumbnailCache;

    /**
     * Adds a context menu to a table view for predefined columns (with extractors for the columns)
     *
     * @param tableView  that the context menu will be added to
     * @param extractors contains a map of columns to copy along with a value extraction function
     */
    public static <T> ContextMenu applyCopyContextMenus(TableView<T> tableView, java.util.Map<TableColumn<T, ?>, Function<T, ?>> extractors) {
        ContextMenu contextMenu = tableView.getContextMenu() == null ? new ContextMenu() : tableView.getContextMenu();

        for (TableColumn<?, ?> column : tableView.getColumns()) {
            if (extractors.containsKey(column)) {
                MenuItem menuItem = buildCopyContextMenuItem(column.getText(), () -> {
                    T selectedItem = (T) column.getTableView().getSelectionModel().getSelectedItem();
                    if (selectedItem == null) {
                        return null;
                    }

                    return extractors.get(column).apply(selectedItem);
                });

                contextMenu.getItems().add(menuItem);
            }


        }

        tableView.setContextMenu(contextMenu);

        return contextMenu;
    }

    private static MenuItem buildCopyContextMenuItem(String baseText, Supplier<?> supplier) {
        MenuItem menuItem = new MenuItem("Copy " + baseText);
        menuItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();

            Object o = supplier.get();

            if (o != null) {
                content.putString(o.toString());
                clipboard.setContent(content);
            }
        });
        return menuItem;
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
                imageView.setImage(largeThumbnailCache.fromIdAndString(newValue.getValue().getId(), thumbnailUrlLarge.toString()));
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
        revokeReasonColumn.setCellValueFactory(o -> o.getValue().revokeReasonProperty());
        revokeReasonColumn.setMinWidth(250);
        tableView.getColumns().add(revokeReasonColumn);
        extractors.put(revokeReasonColumn, BanInfoFX::getRevokeReason);

        TableColumn<BanInfoFX, String> revokeAuthorColumn = new TableColumn<>("Revocation Author");
        revokeAuthorColumn.setCellValueFactory(o -> Bindings.createStringBinding(() -> {
            PlayerFX revokeAuthor = o.getValue().getRevokeAuthor();
            return revokeAuthor == null ? null : revokeAuthor.getLogin();
        }, o.getValue().revokeAuthorProperty()));
        revokeAuthorColumn.setMinWidth(150);
        tableView.getColumns().add(revokeAuthorColumn);
        extractors.put(revokeAuthorColumn, banInfoFX -> banInfoFX.getRevokeAuthor() == null ? null : banInfoFX.getRevokeAuthor().getLogin());

        TableColumn<BanInfoFX, OffsetDateTime> revocationAtColumn = new TableColumn<>("Revocation at");
        revocationAtColumn.setCellValueFactory(o -> o.getValue().revokeTimeProperty());
        revocationAtColumn.setMinWidth(180);
        tableView.getColumns().add(revocationAtColumn);
        extractors.put(revocationAtColumn, BanInfoFX::getRevokeTime);

        TableColumn<BanInfoFX, OffsetDateTime> changeTimeColumn = new TableColumn<>("Created Time");
        changeTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        changeTimeColumn.setMinWidth(180);
        tableView.getColumns().add(changeTimeColumn);

        TableColumn<BanInfoFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Update Time");
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
    public static void buildTeamkillTableView(@NotNull TableView<TeamkillFX> tableView, @NotNull ObservableList<TeamkillFX> data, boolean showKiller, @Nullable Consumer<PlayerFX> onAddBan) {
        tableView.setItems(data);
        HashMap<TableColumn<TeamkillFX, ?>, Function<TeamkillFX, ?>> extractors = new HashMap<>();

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
        extractors.put(gameIdColumn, teamkillFX -> teamkillFX.getGame().getId());

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

    private static <T> TableCell<T, PlayerFX> playerFXCellFactory(TableColumn<T, PlayerFX> ignored, Function<PlayerFX, String> textExtractor) {
        return new TableCell<T, PlayerFX>() {
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
                    setText(textExtractor.apply(item));

                    if (item.getBans().isEmpty()) {
                        setStyle("");
                        tooltip.setText("No bans");
                    } else {
                        setStyle("-fx-font-weight: bold");

                        if (item.getBans().stream()
                                .anyMatch(banInfo -> banInfo.getLevel() == BanLevel.GLOBAL
                                        && banInfo.getBanStatus() == BanStatus.BANNED
                                        && banInfo.getDuration() == BanDurationType.PERMANENT)) {
                            tooltip.setText("Permanent global ban");
                            setTextFill(Color.valueOf("#ca0000"));
                        } else if (item.getBans().stream()
                                .anyMatch(banInfo -> banInfo.getLevel() == BanLevel.CHAT
                                        && banInfo.getBanStatus() == BanStatus.BANNED
                                        && banInfo.getDuration() == BanDurationType.PERMANENT)) {
                            tooltip.setText("Permanent chat ban");
                            setTextFill(Color.valueOf("#ff8800"));
                        } else if (item.getBans().stream()
                                .anyMatch(banInfo -> banInfo.getLevel() == BanLevel.VAULT
                                        && banInfo.getBanStatus() == BanStatus.BANNED
                                        && banInfo.getDuration() == BanDurationType.PERMANENT)) {
                            tooltip.setText("Permanent vault ban");
                            setTextFill(Color.valueOf("#ff8800"));
                        } else if (item.getBans().stream()
                                .allMatch(banInfo -> banInfo.getBanStatus() == BanStatus.EXPIRED || banInfo.getBanStatus() == BanStatus.DISABLED)) {
                            tooltip.setText("Expired ban");
                            setTextFill(Color.valueOf("#098700"));
                        } else {
                            tooltip.setText("Temporary ban");
                            setTextFill(Color.valueOf("#ff8800"));
                        }
                    }
                }
            }
        };
    }

    /**
     * @param tableView The tableview to be populated
     * @param data      data to be put in the tableView
     * @param onAddBan  if not null shows a ban button which triggers this consumer
     */
    public static void buildUserTableView(PlatformService platformService, TableView<PlayerFX> tableView, ObservableList<PlayerFX> data, Consumer<PlayerFX> onAddBan) {
        tableView.setItems(data);
        HashMap<TableColumn<PlayerFX, ?>, Function<PlayerFX, ?>> extractors = new HashMap<>();

        TableColumn<PlayerFX, PlayerFX> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        idColumn.setCellFactory(tableColumn -> ViewHelper.playerFXCellFactory(tableColumn, PlayerFX::getId));
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

        TableColumn<PlayerFX, OffsetDateTime> lastLoginColumn = new TableColumn<>("Last login");
        lastLoginColumn.setCellValueFactory(o -> o.getValue().lastLoginProperty());
        lastLoginColumn.setMinWidth(160);
        tableView.getColumns().add(lastLoginColumn);

        TableColumn<PlayerFX, String> userAgentColumn = new TableColumn<>("User Agent");
        userAgentColumn.setCellValueFactory(o -> o.getValue().userAgentProperty());
        userAgentColumn.setMinWidth(200);
        tableView.getColumns().add(userAgentColumn);
        extractors.put(userAgentColumn, PlayerFX::getUserAgent);

        TableColumn<PlayerFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update of record");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);

        if (onAddBan != null) {
            TableColumn<PlayerFX, PlayerFX> banOptionColumn = new TableColumn<>("Ban");
            banOptionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
            banOptionColumn.setCellFactory(param -> new TableCell<>() {

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

        TableColumn<PlayerFX, String> hashColumn = new TableColumn<>("Hash");
        hashColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getHash)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        hashColumn.setMinWidth(200);
        tableView.getColumns().add(hashColumn);
        extractors.put(hashColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getHash).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> uuidColumn = new TableColumn<>("UUID");
        uuidColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getUuid)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        uuidColumn.setMinWidth(200);
        tableView.getColumns().add(uuidColumn);
        extractors.put(uuidColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getUuid).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> memorySerialColumn = new TableColumn<>("Memory S/N");
        memorySerialColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getMemorySerialNumber)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        memorySerialColumn.setMinWidth(200);
        tableView.getColumns().add(memorySerialColumn);
        extractors.put(memorySerialColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getMemorySerialNumber).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> deviceIdColumn = new TableColumn<>("Device ID");
        deviceIdColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getDeviceId)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        deviceIdColumn.setMinWidth(200);
        tableView.getColumns().add(deviceIdColumn);
        extractors.put(deviceIdColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getDeviceId).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> manufacturerColumn = new TableColumn<>("Manufacturer");
        manufacturerColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getManufacturer)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        manufacturerColumn.setMinWidth(200);
        tableView.getColumns().add(manufacturerColumn);
        extractors.put(manufacturerColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getManufacturer).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> cpuNameColumn = new TableColumn<>("Cpu Name");
        cpuNameColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getName)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        cpuNameColumn.setMinWidth(200);
        tableView.getColumns().add(cpuNameColumn);
        extractors.put(cpuNameColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getName).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> processorIdColumn = new TableColumn<>("Processor Id");
        processorIdColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getProcessorId)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        processorIdColumn.setMinWidth(200);
        tableView.getColumns().add(processorIdColumn);
        extractors.put(processorIdColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getProcessorId).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> biosVersionColumn = new TableColumn<>("BIOS Version");
        biosVersionColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getSMBIOSBIOSVersion)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        biosVersionColumn.setMinWidth(200);
        tableView.getColumns().add(biosVersionColumn);
        extractors.put(biosVersionColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getSMBIOSBIOSVersion).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> serialColumn = new TableColumn<>("S/N");
        serialColumn.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getSerialNumber)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        serialColumn.setMinWidth(200);
        tableView.getColumns().add(serialColumn);
        extractors.put(serialColumn, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getSerialNumber).collect(Collectors.toList()));

        TableColumn<PlayerFX, String> volumeSerialNumber = new TableColumn<>("Volume S/N");
        volumeSerialNumber.setCellValueFactory(o -> Bindings.createStringBinding(() ->
                        o.getValue().getUniqueIds().stream().map(UniqueIdFx::getVolumeSerialNumber)
                                .collect(Collectors.joining("\n")),
                o.getValue().getUniqueIds()));
        volumeSerialNumber.setMinWidth(200);
        tableView.getColumns().add(volumeSerialNumber);
        extractors.put(volumeSerialNumber, playerFX -> playerFX.getUniqueIds().stream().map(UniqueIdFx::getVolumeSerialNumber).collect(Collectors.toList()));

        ContextMenu contextMenu = applyCopyContextMenus(tableView, extractors);
        MenuItem steamLookupMenuItem = new MenuItem("Lookup SteamID");
        steamLookupMenuItem.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            PlayerFX selectedPlayer = tableView.getSelectionModel().getSelectedItem();
            return selectedPlayer == null || selectedPlayer.getSteamId() == null;
        }, tableView.getSelectionModel().selectedItemProperty()));
        steamLookupMenuItem.setOnAction(action -> {
            PlayerFX playerFX = tableView.getSelectionModel().getSelectedItem();
            if (playerFX.getSteamId() != null) {
                platformService.showDocument("https://steamidfinder.com/lookup/" + playerFX.getSteamId());
            }
        });
        contextMenu.getItems().add(steamLookupMenuItem);
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
        authorColumn.setCellFactory(o -> getPlayerFXTableCell());
        authorColumn.setMinWidth(200);
        tableView.getColumns().add(authorColumn);
        extractors.put(authorColumn, mapFX -> mapFX.getAuthor().getLogin());

        TableColumn<MapFX, Boolean> recommendColumn = new TableColumn<>("Recommended");
        recommendColumn.setCellValueFactory(o -> o.getValue().recommendedProperty());
        recommendColumn.setCellFactory(CheckBoxTableCell.forTableColumn(recommendColumn));
        recommendColumn.setMinWidth(100);
        tableView.getColumns().add(recommendColumn);

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


    public static void buildModTableView(TableView<ModFX> tableView, ObservableList<ModFX> data) {
        tableView.setItems(data);
        HashMap<TableColumn<ModFX, ?>, Function<ModFX, ?>> extractors = new HashMap<>();

        TableColumn<ModFX, String> idColumn = new TableColumn<>("Mod ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(100);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, ModFX::getId);

        TableColumn<ModFX, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(o -> o.getValue().displayNameProperty());
        nameColumn.setMinWidth(200);
        tableView.getColumns().add(nameColumn);
        extractors.put(nameColumn, ModFX::getDisplayName);

        TableColumn<ModFX, PlayerFX> uploaderColumn = new TableColumn<>("Uploader");
        uploaderColumn.setCellValueFactory(o -> o.getValue().uploaderProperty());
        uploaderColumn.setCellFactory(o -> getPlayerFXTableCell());
        uploaderColumn.setMinWidth(200);
        tableView.getColumns().add(uploaderColumn);
        extractors.put(uploaderColumn, modFX -> modFX.getUploader().getLogin());

        TableColumn<ModFX, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(o -> o.getValue().authorProperty());
        authorColumn.setMinWidth(200);
        tableView.getColumns().add(authorColumn);
        extractors.put(authorColumn, modFX -> modFX.getUploader().getLogin());

        TableColumn<ModFX, Boolean> recommendColumn = new TableColumn<>("Recommended");
        recommendColumn.setCellValueFactory(o -> o.getValue().recommendedProperty());
        recommendColumn.setCellFactory(CheckBoxTableCell.forTableColumn(recommendColumn));
        recommendColumn.setMinWidth(100);
        tableView.getColumns().add(recommendColumn);

        TableColumn<ModFX, OffsetDateTime> createTimeColumn = new TableColumn<>("First upload");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<ModFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);

        applyCopyContextMenus(tableView, extractors);
    }


    @NotNull
    private static <T> TableCell<T, PlayerFX> getPlayerFXTableCell() {
        return new TableCell<T, PlayerFX>() {
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
        };
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

    public static void buildMapFeedTableView(@NotNull TableView<MapVersionFX> tableView, @NotNull ObservableList<MapVersionFX> data, @Nullable Consumer<MapVersionFX> onToggleHide) {
        tableView.setItems(data);
        HashMap<TableColumn<MapVersionFX, ?>, Function<MapVersionFX, ?>> extractors = new HashMap<>();

        TableColumn<MapVersionFX, String> idColumn = new TableColumn<>("Map Version ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(100);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, MapVersionFX::getId);

        TableColumn<MapVersionFX, String> mapIdColumn = new TableColumn<>("Map ID");
        mapIdColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(mapVersionFX -> mapVersionFX.getMap().getId())
                        .orElse(""))
        );
        mapIdColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        mapIdColumn.setMinWidth(50);
        tableView.getColumns().add(mapIdColumn);
        extractors.put(mapIdColumn, mapVersionFX -> mapVersionFX.getMap().getId());

        TableColumn<MapVersionFX, String> mapNameColumn = new TableColumn<>("Map Name");
        mapNameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(mapVersionFX -> mapVersionFX.getMap().getDisplayName())
                        .orElse(""))
        );
        mapNameColumn.setMinWidth(150);
        tableView.getColumns().add(mapNameColumn);
        extractors.put(mapNameColumn, mapVersionFX -> mapVersionFX.getMap().getDisplayName());

        TableColumn<MapVersionFX, String> uploaderColumn = new TableColumn<>("Uploader");
        uploaderColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
                Optional.ofNullable(param.getValue())
                        .map(mapVersionFX -> mapVersionFX.getMap().getAuthor().getRepresentation())
                        .orElse("")));
        uploaderColumn.setMinWidth(150);
        tableView.getColumns().add(uploaderColumn);
        extractors.put(uploaderColumn, mapVersionFX -> mapVersionFX.getMap().getAuthor().getRepresentation());

        TableColumn<MapVersionFX, ComparableVersion> versionColumn = new TableColumn<>("Version");
        versionColumn.setCellValueFactory(o -> o.getValue().versionProperty());
        versionColumn.setMinWidth(50);
        tableView.getColumns().add(versionColumn);
        extractors.put(versionColumn, MapVersionFX::getVersion);

        TableColumn<MapVersionFX, Boolean> rankedCheckBoxColumn = new TableColumn<>("Ranked");
        rankedCheckBoxColumn.setCellValueFactory(param -> param.getValue().rankedProperty());
        rankedCheckBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(rankedCheckBoxColumn));
        tableView.getColumns().add(rankedCheckBoxColumn);

        TableColumn<MapVersionFX, Boolean> hiddenColumn = new TableColumn<>("Hidden");
        hiddenColumn.setCellValueFactory(o -> o.getValue().hiddenProperty());
        hiddenColumn.setCellFactory(CheckBoxTableCell.forTableColumn(hiddenColumn));
        tableView.getColumns().add(hiddenColumn);

        TableColumn<MapVersionFX, Number> widthColumn = new TableColumn<>("Width");
        widthColumn.setCellValueFactory(o -> o.getValue().widthProperty());
        widthColumn.setMinWidth(50);
        tableView.getColumns().add(widthColumn);
        extractors.put(widthColumn, MapVersionFX::getWidth);

        TableColumn<MapVersionFX, Number> heightColumn = new TableColumn<>("Height");
        heightColumn.setCellValueFactory(o -> o.getValue().widthProperty());
        heightColumn.setMinWidth(50);
        tableView.getColumns().add(heightColumn);
        extractors.put(heightColumn, MapVersionFX::getHeight);

        TableColumn<MapVersionFX, Number> maxPlayersColumn = new TableColumn<>("Max players");
        maxPlayersColumn.setCellValueFactory(o -> o.getValue().maxPlayersProperty());
        maxPlayersColumn.setMinWidth(50);
        tableView.getColumns().add(maxPlayersColumn);
        extractors.put(maxPlayersColumn, MapVersionFX::getMaxPlayers);

        TableColumn<MapVersionFX, String> versionDescriptionColumn = new TableColumn<>("Version description");
        versionDescriptionColumn.setCellValueFactory(o -> o.getValue().descriptionProperty());
        versionDescriptionColumn.setMinWidth(250);
        tableView.getColumns().add(versionDescriptionColumn);
        extractors.put(versionDescriptionColumn, MapVersionFX::getDescription);

        TableColumn<MapVersionFX, URL> downloadUrlColumn = new TableColumn<>("Download URL");
        downloadUrlColumn.setCellValueFactory(o -> o.getValue().downloadUrlProperty());
        downloadUrlColumn.setMinWidth(400);
        tableView.getColumns().add(downloadUrlColumn);
        extractors.put(downloadUrlColumn, MapVersionFX::getDownloadUrl);

        if (onToggleHide != null) {
            TableColumn<MapVersionFX, MapVersionFX> toggleHideColumn = new TableColumn<>("Action");
            toggleHideColumn.setMinWidth(50);
            toggleHideColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
            toggleHideColumn.setCellFactory(param -> new TableCell<MapVersionFX, MapVersionFX>() {
                @Override
                protected void updateItem(MapVersionFX mapVersionFX, boolean empty) {
                    super.updateItem(mapVersionFX, empty);
                    if (!empty) {
                        Button button = new Button();
                        button.textProperty().bind(Bindings.createStringBinding(() -> mapVersionFX.hiddenProperty().get() ? "Unhide" : "Hide", mapVersionFX.hiddenProperty()));
                        button.setOnMouseClicked(event -> onToggleHide.accept(mapVersionFX));
                        setGraphic(button);
                        return;
                    }
                    setGraphic(null);
                }
            });
            tableView.getColumns().add(toggleHideColumn);
        }

        applyCopyContextMenus(tableView, extractors);
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

        ContextMenu contextMenu = applyCopyContextMenus(tableView, extractors);
        contextMenu.getItems().add(buildCopyContextMenuItem("Download URL", () -> {
            MapVersionFX mapVersionFX = tableView.getSelectionModel().getSelectedItem();
            return mapVersionFX == null ? null : mapVersionFX.getDownloadUrl().toString();
        }));
    }


    public static void buildModVersionTableView(TableView<ModVersionFX> tableView, ObservableList<ModVersionFX> data) {
        tableView.setItems(data);
        HashMap<TableColumn<ModVersionFX, ?>, Function<ModVersionFX, ?>> extractors = new HashMap<>();

        TableColumn<ModVersionFX, String> idColumn = new TableColumn<>("Version ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        idColumn.setMinWidth(100);
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, ModVersionFX::getId);

        TableColumn<ModVersionFX, String> nameColumn = new TableColumn<>("Version No.");
        nameColumn.setCellValueFactory(o -> Bindings.createStringBinding(() -> o.getValue().getVersion().toString()));
        nameColumn.setMinWidth(100);
        tableView.getColumns().add(nameColumn);
        extractors.put(nameColumn, ModVersionFX::getVersion);

        TableColumn<ModVersionFX, String> uidColumn = new TableColumn<>("UID");
        uidColumn.setCellValueFactory(o -> o.getValue().uidProperty());
        uidColumn.setMinWidth(280);
        tableView.getColumns().add(uidColumn);
        extractors.put(uidColumn, ModVersionFX::getUid);

        TableColumn<ModVersionFX, Boolean> rankedCheckBoxColumn = new TableColumn<>("Ranked");
        rankedCheckBoxColumn.setCellValueFactory(param -> param.getValue().rankedProperty());
        rankedCheckBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(rankedCheckBoxColumn));
        tableView.getColumns().add(rankedCheckBoxColumn);

        TableColumn<ModVersionFX, Boolean> hiddenColumn = new TableColumn<>("Hidden");
        hiddenColumn.setCellValueFactory(o -> o.getValue().hiddenProperty());
        hiddenColumn.setCellFactory(CheckBoxTableCell.forTableColumn(hiddenColumn));
        tableView.getColumns().add(hiddenColumn);

        TableColumn<ModVersionFX, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(o -> o.getValue().descriptionProperty());
        descriptionColumn.setMinWidth(300);
        tableView.getColumns().add(descriptionColumn);
        extractors.put(descriptionColumn, ModVersionFX::getDescription);


        TableColumn<ModVersionFX, OffsetDateTime> createTimeColumn = new TableColumn<>("Uploaded");
        createTimeColumn.setCellValueFactory(o -> o.getValue().createTimeProperty());
        createTimeColumn.setMinWidth(160);
        tableView.getColumns().add(createTimeColumn);

        TableColumn<ModVersionFX, OffsetDateTime> updateTimeColumn = new TableColumn<>("Last update");
        updateTimeColumn.setCellValueFactory(o -> o.getValue().updateTimeProperty());
        updateTimeColumn.setMinWidth(160);
        tableView.getColumns().add(updateTimeColumn);

        ContextMenu contextMenu = applyCopyContextMenus(tableView, extractors);
        contextMenu.getItems().add(buildCopyContextMenuItem("Download URL", () -> {
            ModVersionFX modVersionFX = tableView.getSelectionModel().getSelectedItem();
            return modVersionFX == null ? null : modVersionFX.getDownloadUrl().toString();
        }));
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

    public static void buildCategoryTable(TableView<TutorialCategoryFX> categoryTableView, TutorialService tutorialService, Runnable refresh) {
        categoryTableView.setEditable(true);
        HashMap<TableColumn<TutorialCategoryFX, ?>, Function<TutorialCategoryFX, ?>> extractors = new HashMap<>();

        TableColumn<TutorialCategoryFX, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        categoryTableView.getColumns().add(idColumn);
        extractors.put(idColumn, TutorialCategoryFX::getId);

        TableColumn<TutorialCategoryFX, String> categoryKeyColumn = new TableColumn<>("Category key  ✏");
        categoryKeyColumn.getStyleClass().add("editable");
        categoryKeyColumn.setCellValueFactory(param -> param.getValue().categoryKeyProperty());
        categoryKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        categoryKeyColumn.setEditable(true);
        categoryKeyColumn.setMinWidth(300);
        categoryTableView.getColumns().add(categoryKeyColumn);
        categoryKeyColumn.setOnEditCommit(event -> {
            TutorialCategoryFX rowValue = event.getRowValue();
            rowValue.setCategoryKey(event.getNewValue());
            tutorialService.updateCategory(rowValue);
            refresh.run();
        });
        extractors.put(categoryKeyColumn, TutorialCategoryFX::getCategoryKey);

        TableColumn<TutorialCategoryFX, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(param -> param.getValue().categoryProperty());
        categoryColumn.setEditable(false);
        categoryColumn.setMinWidth(300);
        categoryTableView.getColumns().add(categoryColumn);
        extractors.put(categoryColumn, TutorialCategoryFX::getCategory);

        applyCopyContextMenus(categoryTableView, extractors);
    }

    public static void buildTutorialTable(TableView<TutorialFx> tutorialTableView, TutorialService tutorialService, Logger log, Runnable refresh) {
        tutorialTableView.setEditable(true);
        HashMap<TableColumn<TutorialFx, ?>, Function<TutorialFx, ?>> extractors = new HashMap<>();

        TableColumn<TutorialFx, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        tutorialTableView.getColumns().add(idColumn);
        extractors.put(idColumn, TutorialFx::getId);

        TableColumn<TutorialFx, String> titleKeyColumn = new TableColumn<>("Title Key ✏");
        titleKeyColumn.getStyleClass().add("editable");
        titleKeyColumn.setCellValueFactory(param -> param.getValue().titleKeyProperty());
        titleKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        titleKeyColumn.setEditable(true);
        titleKeyColumn.setMinWidth(150);
        tutorialTableView.getColumns().add(titleKeyColumn);
        titleKeyColumn.setOnEditCommit(event -> {
            TutorialFx rowValue = event.getRowValue();
            rowValue.setTitleKey(event.getNewValue());
            tutorialService.updateTutorial(rowValue);
            refresh.run();
        });
        extractors.put(titleKeyColumn, TutorialFx::getTitleKey);

        TableColumn<TutorialFx, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(param -> param.getValue().titleProperty());
        titleColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        titleColumn.setEditable(false);
        titleColumn.setMinWidth(150);
        tutorialTableView.getColumns().add(titleColumn);
        extractors.put(titleColumn, TutorialFx::getTitle);

        TableColumn<TutorialFx, String> descriptionKeyColumn = new TableColumn<>("Description Key ✏");
        descriptionKeyColumn.getStyleClass().add("editable");
        descriptionKeyColumn.setCellValueFactory(param -> param.getValue().descriptionKeyProperty());
        descriptionKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        descriptionKeyColumn.setEditable(true);
        descriptionKeyColumn.setMinWidth(200);
        tutorialTableView.getColumns().add(descriptionKeyColumn);
        descriptionKeyColumn.setOnEditCommit(event -> {
            TutorialFx rowValue = event.getRowValue();
            rowValue.setDescriptionKey(event.getNewValue());
            tutorialService.updateTutorial(rowValue);
            refresh.run();
        });
        extractors.put(descriptionKeyColumn, TutorialFx::getDescriptionKey);

        TableColumn<TutorialFx, String> descriptionColumn = new TableColumn<>("Description HTML");
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        descriptionColumn.setEditable(false);
        descriptionColumn.setMinWidth(150);
        tutorialTableView.getColumns().add(descriptionColumn);
        extractors.put(descriptionColumn, TutorialFx::getDescription);

        TableColumn<TutorialFx, String> imageColumn = new TableColumn<>("Image File Name ✏");
        imageColumn.getStyleClass().add("editable");
        imageColumn.setCellValueFactory(param -> param.getValue().imageProperty());
        imageColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        imageColumn.setEditable(true);
        imageColumn.setMinWidth(200);
        tutorialTableView.getColumns().add(imageColumn);
        imageColumn.setOnEditCommit(event -> {
            TutorialFx rowValue = event.getRowValue();
            rowValue.setImage(event.getNewValue());
            tutorialService.updateTutorial(rowValue);
        });
        extractors.put(imageColumn, TutorialFx::getImage);

        TableColumn<TutorialFx, Number> categoryColumn = new TableColumn<>("Category ID ✏");
        categoryColumn.getStyleClass().add("editable");
        categoryColumn.setCellValueFactory(param -> param.getValue().getCategory().idProperty());
        categoryColumn.setCellFactory(TextAreaTableCell.forTableColumn(new StringConverter<Number>() {
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
                    log.error("Error parsing integer for category", e);
                }
                return 0;
            }
        }));
        categoryColumn.setEditable(true);
        categoryColumn.setMinWidth(100);
        tutorialTableView.getColumns().add(categoryColumn);
        categoryColumn.setOnEditCommit(event -> {
            Number newValue = event.getNewValue();
            try {
                TutorialFx rowValue = event.getRowValue();
                TutorialCategoryFX tutorialCategoryFX = new TutorialCategoryFX();
                tutorialCategoryFX.setId(newValue.intValue());
                rowValue.setCategory(tutorialCategoryFX);
                tutorialService.updateTutorial(rowValue);
            } catch (Exception e) {
                log.error("Failed to update category key", e);
                ViewHelper.errorDialog("Category ID update failed", String.format("Update of category id to '%d' failed, did you check that this is a valid value?", newValue.intValue()));
                throw e;
            }
        });
        extractors.put(categoryColumn, tutorialFx -> tutorialFx.getCategory().toString());

        TableColumn<TutorialFx, Number> ordinalColumn = new TableColumn<>("Ordinal ✏");
        ordinalColumn.getStyleClass().add("editable");
        ordinalColumn.setCellValueFactory(param -> param.getValue().ordinalProperty());
        ordinalColumn.setCellFactory(TextAreaTableCell.forTableColumn(new StringConverter<Number>() {
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
        ordinalColumn.setEditable(true);
        ordinalColumn.setMinWidth(20);
        tutorialTableView.getColumns().add(ordinalColumn);
        ordinalColumn.setOnEditCommit(event -> {
            TutorialFx rowValue = event.getRowValue();
            rowValue.setOrdinal(event.getNewValue().intValue());
            tutorialService.updateTutorial(rowValue);
        });
        extractors.put(ordinalColumn, TutorialFx::getOrdinal);

        TableColumn<TutorialFx, Boolean> launchColumn = new TableColumn<>("Launchable ✏");
        launchColumn.getStyleClass().add("editable");
        launchColumn.setCellValueFactory(param -> param.getValue().launchableProperty());
        Callback<TableColumn<TutorialFx, Boolean>, TableCell<TutorialFx, Boolean>> value = CheckBoxTableCell.forTableColumn(launchColumn);
        launchColumn.setCellFactory(value);
        launchColumn.setEditable(true);
        launchColumn.setMinWidth(10);
        tutorialTableView.getColumns().add(launchColumn);

        TableColumn<TutorialFx, String> mapColumn = new TableColumn<>("MapVersion id ✏");
        mapColumn.getStyleClass().add("editable");
        mapColumn.setCellValueFactory(param -> {
            MapVersionFX mapVersion = param.getValue().getMapVersion();
            return mapVersion == null ? new SimpleStringProperty("") : mapVersion.idProperty();
        });
        mapColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        mapColumn.setEditable(true);
        mapColumn.setMinWidth(100);
        tutorialTableView.getColumns().add(mapColumn);
        mapColumn.setOnEditCommit(event -> {
            TutorialFx rowValue = event.getRowValue();
            String newValue = event.getNewValue();
            try {
                if (!newValue.isEmpty()) {
                    MapVersionFX mapVersionFX = new MapVersionFX();
                    mapVersionFX.setId(newValue);
                    rowValue.setMapVersion(mapVersionFX);
                    tutorialService.updateTutorial(rowValue);
                }
            } catch (Exception e) {
                log.error("Failed to update MapVersion ID", e);
                ViewHelper.errorDialog("MapVersion ID update failed", String.format("Update of MapVersion ID to '%s' failed, did you check that this is a valid value?", newValue));
                throw e;
            }
        });
        extractors.put(mapColumn, tutorialFx -> tutorialFx.getMapVersion().toString());

        TableColumn<TutorialFx, String> technicalNameColumn = new TableColumn<>("Technical Name ✏");
        technicalNameColumn.getStyleClass().add("editable");
        technicalNameColumn.setCellValueFactory(param -> param.getValue().technicalNameProperty());
        technicalNameColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        technicalNameColumn.setEditable(true);
        technicalNameColumn.setMinWidth(200);
        tutorialTableView.getColumns().add(technicalNameColumn);
        technicalNameColumn.setOnEditCommit(event -> {
            TutorialFx rowValue = event.getRowValue();
            rowValue.setTechnicalName(event.getNewValue());
            tutorialService.updateTutorial(rowValue);
            refresh.run();
        });
        extractors.put(technicalNameColumn, TutorialFx::getTechnicalName);

        applyCopyContextMenus(tutorialTableView, extractors);
    }


    public static void buildMessagesTable(TableView<MessageFx> messageTableView, MessagesService messagesService, Logger log) {
        messageTableView.setEditable(true);
        HashMap<TableColumn<MessageFx, ?>, Function<MessageFx, ?>> extractors = new HashMap<>();

        TableColumn<MessageFx, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> new SimpleIntegerProperty(o.getValue().getId()));
        messageTableView.getColumns().add(idColumn);
        extractors.put(idColumn, MessageFx::getId);

        TableColumn<MessageFx, String> messageKeyColumn = new TableColumn<>("Key ✏");
        messageKeyColumn.getStyleClass().add("editable");
        messageKeyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        messageKeyColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        messageKeyColumn.setEditable(true);
        messageKeyColumn.setMinWidth(150);
        messageTableView.getColumns().add(messageKeyColumn);
        messageKeyColumn.setOnEditCommit(event -> {
            MessageFx rowValue = event.getRowValue();
            rowValue.setKey(event.getNewValue());
            messagesService.updateMessage(rowValue);
        });
        extractors.put(messageKeyColumn, MessageFx::getKey);

        TableColumn<MessageFx, String> languageColumn = new TableColumn<>("Language ✏");
        languageColumn.getStyleClass().add("editable");
        languageColumn.setCellValueFactory(param -> param.getValue().languageProperty());
        languageColumn.setCellFactory(TextAreaTableCell.forTableColumn(new DefaultStringConverter(), 2));
        languageColumn.setEditable(true);
        languageColumn.setMinWidth(150);
        messageTableView.getColumns().add(languageColumn);
        languageColumn.setOnEditCommit(event -> {
            MessageFx rowValue = event.getRowValue();
            rowValue.setLanguage(event.getNewValue());
            messagesService.updateMessage(rowValue);
        });
        extractors.put(languageColumn, MessageFx::getLanguage);

        TableColumn<MessageFx, String> regionColumn = new TableColumn<>("Region ✏");
        regionColumn.getStyleClass().add("editable");
        regionColumn.setCellValueFactory(param -> param.getValue().regionProperty());
        regionColumn.setCellFactory(TextAreaTableCell.forTableColumn(new DefaultStringConverter(), 2));
        regionColumn.setEditable(true);
        regionColumn.setMinWidth(150);
        messageTableView.getColumns().add(regionColumn);
        regionColumn.setOnEditCommit(event -> {
            MessageFx rowValue = event.getRowValue();
            rowValue.setRegion(event.getNewValue());
            messagesService.updateMessage(rowValue);
        });
        extractors.put(regionColumn, MessageFx::getRegion);

        TableColumn<MessageFx, String> valueColumn = new TableColumn<>("Value ✏");
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueColumn.getStyleClass().add("editable");
        valueColumn.setCellFactory(TextAreaTableCell.forTableColumn());
        valueColumn.setEditable(true);
        valueColumn.setMinWidth(150);
        messageTableView.getColumns().add(valueColumn);
        valueColumn.setOnEditCommit(event -> {
            MessageFx rowValue = event.getRowValue();
            rowValue.setValue(event.getNewValue());
            messagesService.updateMessage(rowValue);
        });
        extractors.put(valueColumn, MessageFx::getValue);

        applyCopyContextMenus(messageTableView, extractors);
    }


    public static void buildSubjectTable(TableView<VotingSubjectFX> tableView, VotingService votingService, Logger log, Runnable refresh) {
        tableView.setEditable(true);
        HashMap<TableColumn<VotingSubjectFX, ?>, Function<VotingSubjectFX, ?>> extractors = new HashMap<>();
        makeTableCopyable(tableView, true);


        TableColumn<VotingSubjectFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
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

    public static void buildModerationReportTableView(TableView<ModerationReportFX> tableView, ObservableList<ModerationReportFX> items) {
        tableView.setItems(items);
        tableView.setEditable(true);
        HashMap<TableColumn<ModerationReportFX, ?>, Function<ModerationReportFX, ?>> extractors = new HashMap<>();

        TableColumn<ModerationReportFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
        tableView.getColumns().add(idColumn);
        extractors.put(idColumn, ModerationReportFX::getId);

        TableColumn<ModerationReportFX, ModerationReportStatus> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(param -> param.getValue().reportStatusProperty());
        tableView.getColumns().add(statusColumn);
        statusColumn.setCellFactory(new Callback<TableColumn<ModerationReportFX, ModerationReportStatus>, TableCell<ModerationReportFX, ModerationReportStatus>>() {
            @Override
            public TableCell<ModerationReportFX, ModerationReportStatus> call(TableColumn<ModerationReportFX, ModerationReportStatus> param) {
                return new TableCell<ModerationReportFX, ModerationReportStatus>() {
                    @Override
                    public void updateItem(ModerationReportStatus item, boolean empty) {
                        super.updateItem(item, empty);

                        if (isEmpty()) {
                            setText("");
                            setStyle("  -fx-background-color: transparent;");
                        } else {
                            switch (item) {
                                case AWAITING:
                                    setStyle("  -fx-background-color: #dcc414;");
                                    break;
                                case DISCARDED:
                                    setStyle("-fx-background-color: #9cabab;");
                                    break;
                                case PROCESSING:
                                    setStyle("-fx-background-color: rgba(56, 56, 255, 0.85);");
                                    break;
                                case COMPLETED:
                                    setStyle("-fx-background-color: #5aad58;");
                                    break;
                            }

                            setText(item.name());
                        }
                    }
                };
            }
        });
        extractors.put(statusColumn, ModerationReportFX::getReportStatus);

        TableColumn<ModerationReportFX, PlayerFX> reporterColumn = new TableColumn<>("Reporter");
        reporterColumn.setCellFactory(tableColumn -> ViewHelper.playerFXCellFactory(tableColumn, PlayerFX::getLogin));
        reporterColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getReporter()));
        reporterColumn.setMinWidth(100);
        tableView.getColumns().add(reporterColumn);
        extractors.put(reporterColumn, reportFx -> reportFx.getReporter().getRepresentation());

        TableColumn<ModerationReportFX, String> reportDescriptionColumn = new TableColumn<>("Report Description");
        reportDescriptionColumn.setMinWidth(150);
        reportDescriptionColumn.setCellValueFactory(param -> param.getValue().reportDescriptionProperty());
        reportDescriptionColumn.setCellFactory(column -> {
            TableCell<ModerationReportFX, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            cell.setWrapText(true);
            text.wrappingWidthProperty().bind(Bindings.createDoubleBinding(() -> cell.getWidth() - 10.0, cell.widthProperty()));
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        tableView.getColumns().add(reportDescriptionColumn);
        extractors.put(reportDescriptionColumn, ModerationReportFX::getReportDescription);

        TableColumn<ModerationReportFX, String> incidentTimeCodeColumn = new TableColumn<>("Incident Timecode");
        incidentTimeCodeColumn.setMinWidth(110);
        incidentTimeCodeColumn.setCellValueFactory(param -> param.getValue().gameIncidentTimecodeProperty());
        tableView.getColumns().add(incidentTimeCodeColumn);
        extractors.put(incidentTimeCodeColumn, ModerationReportFX::getGameIncidentTimecode);

        TableColumn<ModerationReportFX, String> gameColumn = new TableColumn<>("Game ID");
        gameColumn.setCellValueFactory(o -> Bindings.createStringBinding(() -> {
            GameFX game = o.getValue().getGame();
            if (game == null) {
                return "";
            }
            return game.getId();
        }, o.getValue().gameProperty()));
        gameColumn.setMinWidth(80);
        tableView.getColumns().add(gameColumn);
        extractors.put(gameColumn, reportFx -> reportFx.getGame() == null ? null : reportFx.getGame().getId());

        TableColumn<ModerationReportFX, String> privateNoteColumn = new TableColumn<>("Private Notice");
        privateNoteColumn.setMinWidth(150);
        privateNoteColumn.setCellValueFactory(param -> param.getValue().moderatorPrivateNoteProperty());
        tableView.getColumns().add(privateNoteColumn);
        extractors.put(privateNoteColumn, ModerationReportFX::getModeratorPrivateNote);

        TableColumn<ModerationReportFX, String> moderatorPrivateNoticeColumn = new TableColumn<>("Public Note");
        moderatorPrivateNoticeColumn.setMinWidth(180);
        moderatorPrivateNoticeColumn.setCellValueFactory(param -> param.getValue().moderatorNoticeProperty());
        tableView.getColumns().add(moderatorPrivateNoticeColumn);
        extractors.put(moderatorPrivateNoticeColumn, ModerationReportFX::getModeratorNotice);

        TableColumn<ModerationReportFX, String> lastModeratorColumn = new TableColumn<>("Last Moderator");
        lastModeratorColumn.setCellValueFactory(o -> Bindings.createStringBinding(() -> {
            PlayerFX lastModerator = o.getValue().getLastModerator();
            if (lastModerator == null) {
                return "null";
            }
            return lastModerator.getRepresentation();
        }, o.getValue().lastModeratorProperty()));
        lastModeratorColumn.setMinWidth(150);
        tableView.getColumns().add(lastModeratorColumn);
        extractors.put(lastModeratorColumn, reportFx -> reportFx.getLastModerator() == null ? null : reportFx.getLastModerator().getLogin());

        TableColumn<ModerationReportFX, OffsetDateTime> createTimeColumn = new TableColumn<>("Create time");
        createTimeColumn.setMinWidth(150);
        createTimeColumn.setCellValueFactory(param -> param.getValue().createTimeProperty());
        tableView.getColumns().add(createTimeColumn);
        extractors.put(createTimeColumn, ModerationReportFX::getCreateTime);

        applyCopyContextMenus(tableView, extractors);
    }


    public static void buildQuestionTable(TableView<VotingQuestionFX> tableView, VotingService votingService, Logger log, Runnable refresh) {
        tableView.setEditable(true);
        HashMap<TableColumn<VotingQuestionFX, ?>, Function<VotingQuestionFX, ?>> extractors = new HashMap<>();

        makeTableCopyable(tableView, true);

        TableColumn<VotingQuestionFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
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


        Function<VotingQuestionFX, Float> averageOrdinalFunction = votingQuestionFX -> {
            int averageSum = 0;
            for (VotingChoiceFX choice : votingQuestionFX.getVotingChoices()) {
                averageSum += choice.getOrdinal() * choice.getNumberOfAnswers();
            }
            return (float) (averageSum) / votingQuestionFX.getNumberOfAnswers();
        };

        TableColumn<VotingQuestionFX, Float> averageOrdinalColumn = new TableColumn<>("Average ordinal for answers");
        averageOrdinalColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(averageOrdinalFunction.apply(param.getValue())));
        averageOrdinalColumn.setEditable(false);
        averageOrdinalColumn.setMinWidth(170);
        tableView.getColumns().add(averageOrdinalColumn);
        extractors.put(averageOrdinalColumn, averageOrdinalFunction);

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
                    return Integer.parseInt(string);
                } catch (Exception e) {
                    log.error("Error parsing integer", e);
                }
                return 0;
            }
        }));
    }

    public static void buildChoiceTable(TableView<VotingChoiceFX> tableView, VotingService votingService, Logger log, Runnable refresh) {
        tableView.setEditable(true);
        makeTableCopyable(tableView, true);
        HashMap<TableColumn<VotingChoiceFX, ?>, Function<VotingChoiceFX, ?>> extractors = new HashMap<>();

        TableColumn<VotingChoiceFX, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(o -> o.getValue().idProperty());
        idColumn.setComparator(Comparator.comparingInt(Integer::parseInt));
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

    public static void exceptionDialog(String title, String detail, Throwable throwable, Optional<String> url) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(detail);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        String exceptionText = stringWriter.toString();

        Label label = new Label("The stacktrace was:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expandableContent = new GridPane();
        expandableContent.setMaxWidth(Double.MAX_VALUE);
        expandableContent.add(label, 0, 0);
        expandableContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expandableContent);

        url.ifPresent(s -> textArea.setText(textArea.getText() + "\n\nThe called url was:\n" + s));

        alert.showAndWait();
    }

    @SuppressWarnings("rawtypes")
    public static void copySelectionToClipboard(final TableView<?> table) {
        final Set<Integer> rows = new TreeSet<>();
        for (final TablePosition tablePosition : table.getSelectionModel().getSelectedCells()) {
            rows.add(tablePosition.getRow());
        }
        final StringBuilder strb = new StringBuilder();
        boolean firstRow = true;
        for (final Integer row : rows) {
            if (!firstRow) {
                strb.append('\n');
            }
            firstRow = false;
            boolean firstCol = true;
            for (final TableColumn<?, ?> column : table.getColumns()) {
                if (!firstCol) {
                    strb.append('\t');
                }
                firstCol = false;
                final Object cellData = column.getCellData(row);
                strb.append(cellData == null ? "" : cellData.toString());
            }
        }
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(strb.toString());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public static void makeTableCopyable(final TableView<?> table, boolean allowControllC) {
        if (allowControllC) {
            final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
            table.setOnKeyPressed(event -> {
                if (keyCodeCopy.match(event)) {
                    copySelectionToClipboard(table);
                }
            });
        }
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        MenuItem item = new MenuItem("Copy Everything");
        item.setOnAction(event ->
                copySelectionToClipboard(table));
        ContextMenu menu = table.getContextMenu() == null ? new ContextMenu() : table.getContextMenu();
        menu.getItems().add(item);
        table.setContextMenu(menu);
    }

}
