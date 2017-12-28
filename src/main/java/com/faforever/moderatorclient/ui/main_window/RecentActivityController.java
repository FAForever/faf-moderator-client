package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.domain.UserService;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import com.faforever.moderatorclient.ui.domain.TeamkillFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
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

    public RecentActivityController(UserService userService) {
        this.userService = userService;

        users = FXCollections.observableArrayList();
        teamkills = FXCollections.observableArrayList();
    }

    @Override
    public VBox getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildUserTableView(userRegistrationFeedTableView, users);
        ViewHelper.buildTeamkillTableView(teamkillFeedTableView, teamkills, true);
    }

    public void refresh() {
        users.clear();
        users.addAll(userService.findLatestRegistrations());
        userRegistrationFeedTableView.getSortOrder().clear();

        teamkills.clear();
        teamkills.addAll(userService.findLatestTeamkills());
        userRegistrationFeedTableView.getSortOrder().clear();
    }
}
