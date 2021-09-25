package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.PermissionService;
import com.faforever.moderatorclient.ui.data_cells.StringListCell;
import com.faforever.moderatorclient.ui.data_cells.UserGroupStringConverter;
import com.faforever.moderatorclient.ui.domain.GroupPermissionFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import com.faforever.moderatorclient.ui.domain.UserGroupFX;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.function.Consumer;


@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class GroupAddUserController implements Controller<Pane> {
    private final PermissionService permissionService;

    public GridPane root;
    public TextField affectedUserTextField;
    public ComboBox<UserGroupFX> groupComboBox;
    public ListView<GroupPermissionFX> groupPermissionsListView;

    @Getter
    private PlayerFX playerFX;
    private Consumer<UserGroupFX> addedListener;

    public void addAddedListener(Consumer<UserGroupFX> listener) {
        this.addedListener = listener;
    }

    @FXML
    public void initialize() {
        permissionService.getAllUserGroups().thenAccept(userGroups -> groupComboBox.getItems().addAll(userGroups));
        groupComboBox.setConverter(new UserGroupStringConverter());
        groupComboBox.setCellFactory(param -> new StringListCell<>(UserGroupFX::getTechnicalName));
        groupPermissionsListView.setCellFactory(param -> new StringListCell<>(GroupPermissionFX::getTechnicalName));
        groupComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            groupPermissionsListView.getItems().clear();

            if (newValue != null) {
                groupPermissionsListView.getItems().addAll(newValue.getPermissions());
            }
        });
    }

    @Override
    public Pane getRoot() {
        return root;
    }

    public void setPlayer(PlayerFX playerFX) {
        Assert.notNull(playerFX, "Player must not be null");
        this.playerFX = playerFX;

        affectedUserTextField.textProperty().bind(playerFX.loginProperty());
    }

    public void onSave() {
        UserGroupFX userGroupFX = groupComboBox.getValue();
        Assert.notNull(userGroupFX, "You can't save if userGroupFX is null.");

        if (!userGroupFX.getMembers().contains(playerFX)) {
            log.debug("Adding player '{}' to user group '{}", playerFX.getLogin(), userGroupFX.getTechnicalName());
            userGroupFX.getMembers().add(playerFX);
            permissionService.patchUserGroup(userGroupFX);

            if (addedListener != null) {
                addedListener.accept(userGroupFX);
            }
        } else {
            log.debug("Player '{}' already in group '{}", playerFX.getLogin(), userGroupFX.getTechnicalName());
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
