package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.domain.TutorialService;
import com.faforever.moderatorclient.api.domain.events.MessagesChangedEvent;
import com.faforever.moderatorclient.ui.CategoryAddController;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.TutorialAddController;
import com.faforever.moderatorclient.ui.UiService;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.TutorialCategoryFX;
import com.faforever.moderatorclient.ui.domain.TutorialFx;
import javafx.application.Platform;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TutorialController implements Controller<Node> {
    private final UiService uiService;
    private final TutorialService tutorialService;
    private final ObservableList<TutorialFx> tutorialList = FXCollections.observableArrayList();
    private final FilteredList<TutorialFx> filterTutorials = new FilteredList(tutorialList);
    private final HashMap<TutorialFx, WeakChangeListener<Boolean>> weakChangeListenersByTutorial = new HashMap<>();

    public SplitPane root;
    public TableView<TutorialFx> tutorialTableView;
    public TableView<TutorialCategoryFX> categoryTableView;
    public Button addTutorialButton;

    private void setUpTutorialFilter() {
        SortedList<TutorialFx> sortedList = new SortedList<>(filterTutorials);
        sortedList.comparatorProperty().bind(tutorialTableView.comparatorProperty());
        tutorialTableView.setItems(sortedList);

        filterTutorials.setPredicate(this::hasSelectedCategory);


        categoryTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> filterTutorials.setPredicate(this::hasSelectedCategory));
    }

    private boolean hasSelectedCategory(TutorialFx tutorialFx) {
        TutorialCategoryFX selectedItem = categoryTableView.getSelectionModel().getSelectedItem();
        return selectedItem != null && selectedItem.getId() == tutorialFx.getCategory().getId();
    }

    @Override
    public Node getRoot() {
        return root;
    }

    public void onRefreshTutorials() {
        tutorialService.getAllTutorials().thenAccept(tutorialFxes -> Platform.runLater(() -> {
            tutorialFxes.forEach(tutorialFx -> {
                WeakChangeListener<Boolean> changeListener = new WeakChangeListener<>((observable, oldValue, newValue) -> updateTutorialLauchable(tutorialFx));
                if (weakChangeListenersByTutorial.containsKey(tutorialFx)) {
                    tutorialFx.launchableProperty().removeListener(weakChangeListenersByTutorial.get(tutorialFx));
                }
                weakChangeListenersByTutorial.put(tutorialFx, changeListener);
                tutorialFx.launchableProperty().addListener(changeListener);
            });
            tutorialList.clear();
            tutorialList.setAll(tutorialFxes);
        })).exceptionally(throwable -> {
            log.error("error loading tutorials", throwable);
            return null;
        });
    }

    private void updateTutorialLauchable(TutorialFx tutorialFx) {
        tutorialService.updateTutorial(tutorialFx);
    }

    public void addTutorial() {
        TutorialAddController tutorialAddController = uiService.loadFxml("ui/tutorial_add.fxml");
        tutorialAddController.setCategoryId(categoryTableView.getSelectionModel().getSelectedItem().getId());
        tutorialAddController.setOnSave(this::onRefreshTutorials);
        Stage newTutorialDialog = new Stage();
        newTutorialDialog.setTitle("Add new tutorial");
        newTutorialDialog.setScene(new Scene(tutorialAddController.getRoot()));
        newTutorialDialog.showAndWait();
    }

    public void deleteTutorial() {
        TutorialFx selectedItem = tutorialTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            log.info("Could not delete tutorial, there was no tutorial selected");
            return;
        }
        tutorialService.deleteTutorial(selectedItem);
        onRefreshTutorials();
    }

    public void onRefreshCategorys() {
        tutorialService.getAllCategories().thenAccept(categoryFXList -> Platform.runLater(() -> {
            categoryTableView.setItems(FXCollections.observableList(categoryFXList));
        })).exceptionally(throwable -> {
            log.error("error loading tutorials", throwable);
            return null;
        });
    }

    public void deleteCategory() {
        TutorialCategoryFX selectedItem = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            log.info("Could not delete category, there was no category selected");
            return;
        }
        tutorialService.deleteCategory(selectedItem);
        onRefreshCategorys();
    }

    public void addCategory() {
        CategoryAddController categoryAddController = uiService.loadFxml("ui/category_add.fxml");
        categoryAddController.setOnSave(this::onRefreshCategorys);
        Stage newCategoryDialog = new Stage();
        newCategoryDialog.setTitle("Add new category");
        newCategoryDialog.setScene(new Scene(categoryAddController.getRoot()));
        newCategoryDialog.showAndWait();
    }

    @EventListener
    public void onMessagesChangedEvent(MessagesChangedEvent messagesChangedEvent) {
        onRefreshTutorials();
        onRefreshCategorys();
    }

    public void initialize() {
        ViewHelper.buildTutorialTable(tutorialTableView, tutorialService, log, this::onRefreshTutorials);
        ViewHelper.buildCategoryTable(categoryTableView, tutorialService, this::onRefreshCategorys);
        setUpTutorialFilter();
        addTutorialButton.disableProperty().bind(categoryTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    public void onRefresh() {
        onRefreshCategorys();
        onRefreshTutorials();
    }
}
