package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.PermissionService;
import com.faforever.moderatorclient.ui.data_cells.StringListCell;
import com.faforever.moderatorclient.ui.domain.GroupPermissionFX;
import com.faforever.moderatorclient.ui.domain.UserGroupFX;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class GroupAddPermissionController implements Controller<Pane> {
    private final PermissionService permissionService;

    public VBox root;
    public TextField affectedGroupTextField;
    public ListView<GroupPermissionFX> groupPermissionsListView;

    @Getter
    private UserGroupFX userGroupFX;
    private Consumer<List<GroupPermissionFX>> addedListener;

    public void addAddedListener(Consumer<List<GroupPermissionFX>> listener) {
        this.addedListener = listener;
    }

    @FXML
    public void initialize() {
        permissionService.getAllGroupPermissions().thenAccept(permissions ->
                groupPermissionsListView.getItems().addAll(permissions.stream().filter(permission -> !userGroupFX.getPermissions().contains(permission))
                        .collect(Collectors.toList())));
        groupPermissionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        groupPermissionsListView.setCellFactory(param -> new StringListCell<>(GroupPermissionFX::getTechnicalName));
    }

    @Override
    public Pane getRoot() {
        return root;
    }

    public void setGroup(UserGroupFX userGroupFX) {
        Assert.notNull(userGroupFX, "Group must not be null");
        this.userGroupFX = userGroupFX;

        affectedGroupTextField.textProperty().bind(userGroupFX.technicalNameProperty());
    }

    public void onSave() {
        List<GroupPermissionFX> permissionsToAdd = groupPermissionsListView.getSelectionModel().getSelectedItems();
        Assert.notNull(userGroupFX, "You can't save if userGroupFX is null.");

        if (!permissionsToAdd.isEmpty()) {
            userGroupFX.getPermissions().addAll(permissionsToAdd);
            permissionService.patchUserGroup(userGroupFX);

            if (addedListener != null) {
                addedListener.accept(permissionsToAdd);
            }
        }
        close();
    }

    public void onAbort() {
        close();
    }

    private void close() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
