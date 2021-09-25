package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.domain.PermissionService;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.UiService;
import javafx.scene.control.SplitPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PermissionController implements Controller<SplitPane> {
    private final PermissionService permissionService;
    private final UiService uiService;

    public SplitPane root;

    @Override
    public SplitPane getRoot() {
        return root;
    }

    public void onRefreshPermissions() {
        permissionService.getAllGroupPermissions();
        permissionService.getAllUserGroups();
    }
}
