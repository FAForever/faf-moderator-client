package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.ui.main_window.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MainController implements Controller<TabPane> {
    public Tab userManagementTab;
    public Tab ladderMapPoolTab;
    public Tab mapVaultTab;
    public Tab modVaultTab;
    public Tab avatarsTab;


    private final UiService uiService;

    public TabPane root;
    public Tab recentActivityTab;
    private UserManagementController userManagementController;
    private LadderMapPoolController ladderMapPoolController;
    private MapVaultController mapVaultController;
    private AvatarsController avatarsController;
    private RecentActivityController recentActivityController;

    public MainController(UiService uiService) {
        this.uiService = uiService;
    }

    @Override
    public TabPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        initUserManagementTab();
        initLadderMapPoolTab();
        initMapVaultTab();
        initAvatarTab();
        initRecentActivityTab();
    }

    private void initUserManagementTab() {
        userManagementController = uiService.loadFxml("ui/main_window/userManagement.fxml");
        userManagementTab.setContent(userManagementController.getRoot());
    }

    private void initLadderMapPoolTab() {
        ladderMapPoolController = uiService.loadFxml("ui/main_window/ladderMapPool.fxml");
        ladderMapPoolTab.setContent(ladderMapPoolController.getRoot());
    }

    private void initMapVaultTab() {
        mapVaultController = uiService.loadFxml("ui/main_window/mapVault.fxml");
        mapVaultTab.setContent(mapVaultController.getRoot());
    }

    private void initAvatarTab() {
        avatarsController = uiService.loadFxml("ui/main_window/avatars.fxml");
        avatarsTab.setContent(avatarsController.getRoot());
    }

    private void initRecentActivityTab() {
        recentActivityController = uiService.loadFxml("ui/main_window/recentActivity.fxml");
        recentActivityTab.setContent(recentActivityController.getRoot());
    }

    public void display() {
        LoginController loginController = uiService.loadFxml("ui/login.fxml");

        Stage loginDialog = new Stage();
        loginDialog.setOnCloseRequest(event -> System.exit(0));
        loginDialog.setAlwaysOnTop(true);
        loginDialog.setTitle("FAF Moderator Client");
        loginDialog.setScene(new Scene(loginController.getRoot()));
        loginDialog.initStyle(StageStyle.UTILITY);
        loginDialog.showAndWait();

        ladderMapPoolController.refresh();
        refreshAvatars();
        refreshRecentActivity();
    }


    public void refreshAvatars() {
        avatarsController.refresh();
    }

    public void refreshRecentActivity() {
        recentActivityController.refresh();
    }
}