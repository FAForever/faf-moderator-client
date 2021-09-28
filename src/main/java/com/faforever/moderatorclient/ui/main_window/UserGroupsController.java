package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.domain.PermissionService;
import com.faforever.moderatorclient.ui.AddGroupController;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.GroupAddChildController;
import com.faforever.moderatorclient.ui.GroupAddPermissionController;
import com.faforever.moderatorclient.ui.UiService;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.GroupPermissionFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import com.faforever.moderatorclient.ui.domain.UserGroupFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserGroupsController implements Controller<HBox> {
    private final PermissionService permissionService;
    private final UiService uiService;

    public HBox root;
    public TableView<UserGroupFX> groupsTableView;
    public TableView<GroupPermissionFX> groupPermissionsTableView;
    public TableView<UserGroupFX> groupChildrenTableView;
    public TableView<PlayerFX> membersTableView;

    public ObservableList<UserGroupFX> userGroups = FXCollections.observableArrayList();
    public ObservableList<GroupPermissionFX> groupPermissions = FXCollections.observableArrayList();
    public ObservableList<UserGroupFX> childUserGroups = FXCollections.observableArrayList();
    public ObservableList<PlayerFX> members = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ViewHelper.buildUserGroupsTableView(groupsTableView, userGroups);
        ViewHelper.buildUserPermissionsTableView(groupPermissionsTableView, groupPermissions);
        ViewHelper.buildUserGroupsTableView(groupChildrenTableView, childUserGroups);
        ViewHelper.buildSimpleUserTableView(membersTableView, members);
        permissionService.getAllUserGroups().thenAccept(userGroups -> this.userGroups.addAll(userGroups));
        groupsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            groupPermissions.clear();
            childUserGroups.clear();
            members.clear();

            if (newValue != null) {
                groupPermissions.addAll(newValue.getPermissions());
                childUserGroups.addAll(newValue.getChildren());
                members.addAll(newValue.getMembers());
            }
        });
    }

    @Override
    public HBox getRoot() {
        return root;
    }

    public void onRefreshGroups() {
        permissionService.getAllUserGroups().thenAccept(userGroups -> this.userGroups.setAll(userGroups));
    }

    public void openGroupDialog() {
        AddGroupController addGroupController = uiService.loadFxml("ui/addGroup.fxml");
        addGroupController.addAddedListener(group -> onRefreshGroups());

        Stage userGroupDialog = new Stage();
        userGroupDialog.setTitle("Add Group");
        userGroupDialog.setScene(new Scene(addGroupController.getRoot()));
        userGroupDialog.showAndWait();
    }

    public void openPermissionDialog() {
        UserGroupFX selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedGroup, "You need to select a user group.");

        GroupAddPermissionController groupPermissionController = uiService.loadFxml("ui/groupAddPermission.fxml");
        groupPermissionController.setGroup(selectedGroup);
        groupPermissionController.addAddedListener(group -> onRefreshGroups());

        Stage userGroupDialog = new Stage();
        userGroupDialog.setTitle("Add Permission");
        userGroupDialog.setScene(new Scene(groupPermissionController.getRoot()));
        userGroupDialog.showAndWait();
    }

    public void openChildDialog() {
        UserGroupFX selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedGroup, "You need to select a user group.");

        GroupAddChildController groupChildController = uiService.loadFxml("ui/groupAddChildren.fxml");
        groupChildController.setGroup(selectedGroup);
        groupChildController.addAddedListener(group -> onRefreshGroups());

        Stage userGroupDialog = new Stage();
        userGroupDialog.setTitle("Add Permission");
        userGroupDialog.setScene(new Scene(groupChildController.getRoot()));
        userGroupDialog.showAndWait();
    }

    public void onTogglePublic() {
        UserGroupFX selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedGroup, "You need to select a user group.");

        selectedGroup.setPublic_(!selectedGroup.isPublic_());

        permissionService.patchUserGroup(selectedGroup);
    }

    public void onRemoveGroup() {
        UserGroupFX selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedGroup, "You need to select a user group.");

        permissionService.deleteUserGroup(selectedGroup);

        userGroups.remove(selectedGroup);
    }

    public void onRemovePermission() {
        UserGroupFX selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedGroup, "You need to select a user group.");

        GroupPermissionFX selectedPermission = groupPermissionsTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedPermission, "You need to select a permission.");

        if (selectedGroup.getPermissions().remove(selectedPermission)) {
            permissionService.patchUserGroup(selectedGroup);
        }

        groupPermissions.remove(selectedPermission);
    }

    public void onRemoveChild() {
        UserGroupFX selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedGroup, "You need to select a user group.");

        UserGroupFX selectedChild = groupChildrenTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedChild, "You need to select a child group.");

        if (selectedGroup.getChildren().remove(selectedChild)) {
            permissionService.patchUserGroup(selectedGroup);
        }

        childUserGroups.remove(selectedChild);
    }

    public void onRemoveMember() {
        UserGroupFX selectedGroup = groupsTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedGroup, "You need to select a user group.");

        PlayerFX selectedMember = membersTableView.getSelectionModel().getSelectedItem();
        Assert.notNull(selectedMember, "You need to select a member.");

        if (selectedGroup.getMembers().remove(selectedMember)) {
            permissionService.patchUserGroup(selectedGroup);
        }

        members.remove(selectedMember);
    }
}
