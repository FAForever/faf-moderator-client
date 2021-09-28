package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.PermissionService;
import com.faforever.moderatorclient.ui.data_cells.StringListCell;
import com.faforever.moderatorclient.ui.domain.GroupPermissionFX;
import com.faforever.moderatorclient.ui.domain.UserGroupFX;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;


@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class AddGroupController implements Controller<Pane> {
    private final PermissionService permissionService;

    public VBox root;
    public TextField technicalName;
    public TextField nameKey;
    public ListView<GroupPermissionFX> groupPermissionsListView;
    public ListView<UserGroupFX> groupChildrenListView;
    public CheckBox publicCheckBox;

    private Consumer<UserGroupFX> addedListener;

    public void addAddedListener(Consumer<UserGroupFX> listener) {
        this.addedListener = listener;
    }

    @FXML
    public void initialize() {
        permissionService.getAllGroupPermissions().thenAccept(permissions -> groupPermissionsListView.getItems().addAll(permissions));
        groupPermissionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        groupPermissionsListView.setCellFactory(param -> new StringListCell<>(GroupPermissionFX::getTechnicalName));
        permissionService.getAllUserGroups().thenAccept(userGroups ->
                groupChildrenListView.getItems().addAll(userGroups));
        groupChildrenListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        groupChildrenListView.setCellFactory(param -> new StringListCell<>(UserGroupFX::getTechnicalName));
    }

    @Override
    public Pane getRoot() {
        return root;
    }

    public void onSave() {
        if (technicalName.getText().isBlank()) {
            throw new IllegalArgumentException("Technical Name cannot be blank");
        }

        if (nameKey.getText().isBlank()) {
            throw new IllegalArgumentException("Technical Name cannot be blank");
        }

        UserGroupFX userGroupFX = new UserGroupFX();

        List<GroupPermissionFX> permissionsToAdd = groupPermissionsListView.getSelectionModel().getSelectedItems();
        List<UserGroupFX> childrenToAdd = groupChildrenListView.getSelectionModel().getSelectedItems();

        userGroupFX.getPermissions().addAll(permissionsToAdd);
        userGroupFX.setPublic_(publicCheckBox.isSelected());
        userGroupFX.setTechnicalName(technicalName.getText());
        userGroupFX.setNameKey(nameKey.getText());
        UserGroupFX newGroup = permissionService.postUserGroup(userGroupFX);

        childrenToAdd.forEach(child -> {
            child.setParent(newGroup);
            permissionService.patchUserGroup(child);
        });

        if (addedListener != null) {
            addedListener.accept(userGroupFX);
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
