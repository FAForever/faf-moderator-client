package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.domain.UserService;
import com.faforever.moderatorclient.mapstruct.GamePlayerStatsMapper;
import com.faforever.moderatorclient.ui.*;
import com.faforever.moderatorclient.ui.domain.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class UserManagementController implements Controller<SplitPane> {
    private final UiService uiService;
    private final PlatformService platformService;
    private final UserService userService;
    private final GamePlayerStatsMapper gamePlayerStatsMapper;

    private final ObservableList<PlayerFX> users;
    private final ObservableList<UserNoteFX> userNotes;
    private final ObservableList<BanInfoFX> bans;
    private final ObservableList<NameRecordFX> nameRecords;
    private final ObservableList<TeamkillFX> teamkills;
    private final ObservableList<AvatarAssignmentFX> avatarAssignments;

    private final String replayDownLoadFormat;

    public SplitPane root;

    public RadioButton searchUserByIdRadioButton;
    public RadioButton searchUserByCurrentNameRadioButton;
    public RadioButton searchUserByPreviousNamesRadioButton;
    public RadioButton searchUserByEmailRadioButton;
    public RadioButton searchUserBySteamIdRadioButton;
    public RadioButton searchUserByIpRadioButton;
    public TextField userSearchTextField;
    public TableView<UserNoteFX> userNoteTableView;
    public Button addNoteButton;
    public Button editNoteButton;
    public Button newBanButton;
    public Button editBanButton;
    public TableView<PlayerFX> userSearchTableView;
    public TableView<NameRecordFX> userNameHistoryTableView;
    public TableView<BanInfoFX> userBansTableView;
    public TableView<TeamkillFX> userTeamkillsTableView;
    public TableView<AvatarAssignmentFX> userAvatarsTableView;
    public TableView<GamePlayerStatsFX> userLastGamesTable;
    public ChoiceBox<FeaturedModFX> featuredModFilterChoiceBox;
    public Button loadMoreGamesButton;
    private Runnable loadMoreGamesRunnable;
    private int userGamesPage = 1;

    public UserManagementController(UiService uiService, PlatformService platformService, UserService userService, GamePlayerStatsMapper gamePlayerStatsMapper, @Value("${faforever.vault.replayDownloadUrlFormat}") String replayDownLoadFormat) {
        this.uiService = uiService;
        this.platformService = platformService;
        this.userService = userService;
        this.gamePlayerStatsMapper = gamePlayerStatsMapper;
        this.replayDownLoadFormat = replayDownLoadFormat;
        users = FXCollections.observableArrayList();
        userNotes = FXCollections.observableArrayList();
        bans = FXCollections.observableArrayList();
        nameRecords = FXCollections.observableArrayList();
        teamkills = FXCollections.observableArrayList();
        avatarAssignments = FXCollections.observableArrayList();
    }

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildUserTableView(userSearchTableView, users);
        ViewHelper.buildNotesTableView(userNoteTableView, userNotes, false);
        ViewHelper.buildNameHistoryTableView(userNameHistoryTableView, nameRecords);
        ViewHelper.buildBanTableView(userBansTableView, bans);
        ViewHelper.buildPlayersGamesTable(userLastGamesTable, replayDownLoadFormat, platformService);

        addNoteButton.disableProperty().bind(userSearchTableView.getSelectionModel().selectedItemProperty().isNull());
        editNoteButton.disableProperty().bind(userNoteTableView.getSelectionModel().selectedItemProperty().isNull());

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
            userGamesPage = 1;
            if (loadMoreGamesRunnable != null) loadMoreGamesRunnable.run();
        });

        featuredModFilterChoiceBox.getItems().add(null);
        featuredModFilterChoiceBox.getSelectionModel().select(0);
        CompletableFuture.supplyAsync(userService::getFeaturedMods)
                .thenAccept(featuredMods -> Platform.runLater(() -> featuredModFilterChoiceBox.getItems().addAll(featuredMods)));

        ViewHelper.buildTeamkillTableView(userTeamkillsTableView, teamkills, false);
        ViewHelper.buildUserAvatarsTableView(userAvatarsTableView, avatarAssignments);

        userSearchTableView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedUser);
        editBanButton.disableProperty().bind(userBansTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    private void onSelectedUser(ObservableValue<? extends PlayerFX> observable, PlayerFX oldValue, PlayerFX newValue) {
        nameRecords.clear();
        userNameHistoryTableView.getSortOrder().clear();
        bans.clear();
        userBansTableView.getSortOrder().clear();

        userLastGamesTable.getItems().clear();
        userLastGamesTable.getSortOrder().clear();

        userTeamkillsTableView.getSortOrder().clear();
        teamkills.clear();

        userNoteTableView.getSortOrder().clear();
        userNotes.clear();

        avatarAssignments.clear();
        userAvatarsTableView.getSortOrder().clear();

        if (newValue != null) {
            userNotes.addAll(userService.getUserNotes(newValue.getId()));
            nameRecords.addAll(newValue.getNames());
            bans.addAll(newValue.getBans());
            teamkills.addAll(userService.findTeamkillsByUserId(newValue.getId()));
            avatarAssignments.addAll(newValue.getAvatarAssignments());

            userGamesPage = 1;
            loadMoreGamesRunnable = () -> CompletableFuture.supplyAsync(() -> gamePlayerStatsMapper.map(userService.getLastHundredPlayedGamesByFeaturedMod(newValue.getId(), userGamesPage, featuredModFilterChoiceBox.getSelectionModel().getSelectedItem())))
                    .thenAccept(gamePlayerStats -> Platform.runLater(() -> userLastGamesTable.getItems().addAll(gamePlayerStats)));
            loadMoreGamesRunnable.run();
        }

        newBanButton.setDisable(newValue == null);
    }

    public void onUserSearch() {
        users.clear();
        userSearchTableView.getSortOrder().clear();

        List<PlayerFX> usersFound = Collections.emptyList();
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

        users.addAll(usersFound);
    }

    public void onNewBan() {
        PlayerFX selectedPlayer = userSearchTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedPlayer, "You need to select a player to create a ban.");

        BanInfoFX banInfoFX = new BanInfoFX()
                .setPlayer(selectedPlayer);

        openBanDialog(banInfoFX, true);
    }

    public void onEditBan() {
        BanInfoFX selectedBan = userBansTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedBan, "You need to select a ban to edit it.");

        openBanDialog(selectedBan, false);
    }

    private void openBanDialog(BanInfoFX banInfoFX, boolean isNew) {
        BanInfoController banInfoController = uiService.loadFxml("ui/banInfo.fxml");
        banInfoController.setBanInfo(banInfoFX);
        if (isNew) {
            banInfoController.addPostedListener(bans::add);
        }

        Stage banInfoDialog = new Stage();
        banInfoDialog.setTitle(isNew ? "Apply new ban" : "Edit ban");
        banInfoDialog.setScene(new Scene(banInfoController.getRoot()));
        banInfoDialog.showAndWait();
    }

    public void addNote() {
        PlayerFX selectedPlayer = userSearchTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedPlayer, "You need to select a player to create a userNote.");

        UserNoteFX userNoteFX = new UserNoteFX();
        userNoteFX.setPlayer(selectedPlayer);

        openUserNoteDialog(userNoteFX, true);
    }

    public void editNote() {
        UserNoteFX selectedUserNote = userNoteTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedUserNote, "You need to select a player note to edit it.");

        openUserNoteDialog(selectedUserNote, false);
    }

    private void openUserNoteDialog(UserNoteFX userNoteFX, boolean isNew) {
        UserNoteController userNoteController = uiService.loadFxml("ui/userNote.fxml");
        userNoteController.setUserNoteFX(userNoteFX);
        if (isNew) {
            userNoteController.addPostedListener(userNotes::add);
        }

        Stage userNoteDialog = new Stage();
        userNoteDialog.setTitle(isNew ? "Add new player note" : "Edit player note");
        userNoteDialog.setScene(new Scene(userNoteController.getRoot()));
        userNoteDialog.showAndWait();
    }

    public void loadMoreGames() {
        userGamesPage++;
        loadMoreGamesRunnable.run();
    }
}
