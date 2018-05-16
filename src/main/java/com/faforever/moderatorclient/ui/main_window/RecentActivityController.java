package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.domain.UserService;
import com.faforever.moderatorclient.ui.BanInfoController;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.UiService;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import com.faforever.moderatorclient.ui.domain.TeamkillFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RecentActivityController implements Controller<VBox> {
    private final UserService userService;
    private final ObservableList<PlayerFX> users;
    private final ObservableList<TeamkillFX> teamkills;

    public VBox root;
    public TableView<PlayerFX> userRegistrationFeedTableView;
    public TableView<TeamkillFX> teamkillFeedTableView;
    private final UiService uiService;

    public RecentActivityController(UserService userService, UiService uiService) {
        this.userService = userService;
        this.uiService = uiService;

        users = FXCollections.observableArrayList();
        teamkills = FXCollections.observableArrayList();
    }

    @Override
    public VBox getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildUserTableView(userRegistrationFeedTableView, users, this::addBan);
        ViewHelper.buildTeamkillTableView(teamkillFeedTableView, teamkills, true, this::addBan);
    }

    private void addBan(PlayerFX playerFX) {
        BanInfoController banInfoController = uiService.loadFxml("ui/banInfo.fxml");
        BanInfoFX banInfo = new BanInfoFX();
        banInfo.setPlayer(playerFX);
        banInfoController.setBanInfo(banInfo);
        banInfoController.addPostedListener(banInfoFX -> refresh());
        Stage banInfoDialog = new Stage();
        banInfoDialog.setTitle("Apply new ban");
        banInfoDialog.setScene(new Scene(banInfoController.getRoot()));
        banInfoDialog.showAndWait();

    }

    public void refresh() {
        users.setAll(userService.findLatestRegistrations());
        userRegistrationFeedTableView.getSortOrder().clear();

        teamkills.setAll(userService.findLatestTeamkills());
        teamkillFeedTableView.getSortOrder().clear();
    }
}
