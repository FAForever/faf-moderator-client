package com.faforever.moderatorclient.ui;

import jakarta.annotation.Nullable;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadingStateManager {
    private record State(boolean disabled, @Nullable Node placeholder) {
    }

    private final Map<Control, State> controls = new HashMap<>();
    private final AtomicBoolean applied = new AtomicBoolean(false);
    private final ProgressIndicator progressIndicator = new ProgressIndicator();

    public LoadingStateManager add(Control control) {
        if (applied.get()) throw new IllegalStateException("LoadingStateManager already applied");

        controls.put(control, null);

        return this;
    }

    public void applyLoadingState() {
        if (applied.get()) throw new IllegalStateException("LoadingStateManager already applied");

        applied.set(true);

        controls.keySet().forEach(control -> {
            // gather the state
            Node placeholder = switch (control) {
                case TableView<?> tableView -> tableView.getPlaceholder();
                case ListView<?> listView -> listView.getPlaceholder();
                case TreeTableView<?> treeTableView -> treeTableView.getPlaceholder();
                default -> null;
            };

            controls.put(control, new State(control.isDisabled(), placeholder));

            // set to loading state
            switch (control) {
                case TableView<?> tableView -> tableView.setPlaceholder(progressIndicator);
                case ListView<?> listView -> listView.setPlaceholder(progressIndicator);
                case TreeTableView<?> treeTableView -> treeTableView.setPlaceholder(progressIndicator);
                default -> {
                }
            }
            ;

            progressIndicator.setVisible(true);
            control.setDisable(true);
        });
    }

    public void revertLoadingState() {
        controls.forEach((control, state) -> {
            switch (control) {
                case TableView<?> tableView -> tableView.setPlaceholder(state.placeholder());
                case ListView<?> listView -> listView.setPlaceholder(state.placeholder());
                case TreeTableView<?> treeTableView -> treeTableView.setPlaceholder(state.placeholder());
                default -> {
                }
            }
            ;

            control.setDisable(state.disabled());
        });

        applied.set(false);
    }
}
