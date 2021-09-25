package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.PermissionService;
import com.faforever.moderatorclient.ui.data_cells.StringListCell;
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
public class GroupAddChildController implements Controller<Pane> {
    private final PermissionService permissionService;

    public VBox root;
    public TextField affectedGroupTextField;
    public ListView<UserGroupFX> groupChildrenListView;

    @Getter
    private UserGroupFX userGroupFX;
    private Consumer<List<UserGroupFX>> addedListener;

    public void addAddedListener(Consumer<List<UserGroupFX>> listener) {
        this.addedListener = listener;
    }

    @FXML
    public void initialize() {
        permissionService.getAllUserGroups().thenAccept(userGroups ->
                groupChildrenListView.getItems().addAll(userGroups.stream()
                        .filter(userGroup -> !userGroup.equals(userGroupFX) && !userGroupFX.getChildren().contains(userGroup))
                        .collect(Collectors.toList())));
        groupChildrenListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        groupChildrenListView.setCellFactory(param -> new StringListCell<>(UserGroupFX::getTechnicalName));
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
        List<UserGroupFX> childrenToAdd = groupChildrenListView.getSelectionModel().getSelectedItems();
        Assert.notNull(userGroupFX, "You can't save if userGroupFX is null.");

        if (!childrenToAdd.isEmpty()) {
            childrenToAdd.forEach(childGroup -> {
                childGroup.setParent(userGroupFX);
                permissionService.patchUserGroup(childGroup);
            });

            if (addedListener != null) {
                addedListener.accept(childrenToAdd);
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
