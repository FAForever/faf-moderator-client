package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.Player;
import com.faforever.commons.api.dto.Teamkill;
import com.faforever.moderatorclient.api.domain.UserService;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RecentActivityController implements Controller<VBox> {
    private final UserService userService;

    public VBox root;
    public TableView<Player> userRegistrationFeedTableView;
    public TableView<Teamkill> teamkillFeedTableView;

    public RecentActivityController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public VBox getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildUserTableView(userRegistrationFeedTableView);
        ViewHelper.buildTeamkillTableView(teamkillFeedTableView, true);
    }

    public void refresh() {
        userRegistrationFeedTableView.getItems().clear();
        userRegistrationFeedTableView.getSortOrder().clear();
        userRegistrationFeedTableView.getItems().addAll(userService.findLatestRegistrations());

        teamkillFeedTableView.getItems().clear();
        userRegistrationFeedTableView.getSortOrder().clear();
        teamkillFeedTableView.getItems().addAll(userService.findLatestTeamkills());
    }
}
