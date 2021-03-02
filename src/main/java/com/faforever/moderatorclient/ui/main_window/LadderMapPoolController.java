package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.domain.MapService;
import com.faforever.moderatorclient.common.MatchmakerQueue;
import com.faforever.moderatorclient.common.MatchmakerQueueMapPool;
import com.faforever.moderatorclient.mapstruct.MapMapper;
import com.faforever.moderatorclient.mapstruct.MapVersionMapper;
import com.faforever.moderatorclient.mapstruct.MatchmakerQueueMapPoolMapper;
import com.faforever.moderatorclient.mapstruct.MatchmakerQueueMapper;
import com.faforever.moderatorclient.ui.*;
import com.faforever.moderatorclient.ui.caches.LargeThumbnailCache;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import com.faforever.moderatorclient.ui.domain.MatchmakerQueueMapPoolFX;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LadderMapPoolController implements Controller<SplitPane> {
    private final MapService mapService;
    private final MapMapper mapMapper;
    private final MapVersionMapper mapVersionMapper;
    private final UiService uiService;
    private final MatchmakerQueueMapper matchmakerQueueMapper;
    private final MatchmakerQueueMapPoolMapper matchmakerQueueMapPoolMapper;
    private final LargeThumbnailCache largeThumbnailCache;

    public SplitPane root;

    public HBox bracketListContainer;
    public HBox bracketHeaderContainer;
    public TreeTableView<MapTableItemAdapter> mapVaultView;
    public CheckBox filterByMapNameCheckBox;
    public TextField mapNamePatternTextField;
    public ImageView ladderPoolImageView;
    public Button refreshButton;
    public Button uploadToDatabaseButton;
    public ComboBox<MatchmakerQueue> queueComboBox;
    public ScrollPane bracketsScrollPane;
    public VBox addButtonsContainer;

    private ObjectProperty<MapVersionFX> selectedMap = new SimpleObjectProperty<>();

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildMapTreeView(mapVaultView);
        bindSelectedMapPropertyToImageView();
        mapVaultView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null || newValue.getValue().isMapVersion()) {
                selectedMap.setValue(mapVersionMapper.map(newValue.getValue().getMapVersion()));
            }
        });

        queueComboBox.setItems(FXCollections.observableArrayList(mapService.getAllMatchmakerQueues()));
        queueComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(MatchmakerQueue object) {
                return object.getTechnicalName();
            }

            @Override
            public MatchmakerQueue fromString(String string) {
                return null;
            }
        });

        bracketsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    public void queueComboAction(ActionEvent event) {
        clearUiAndLoadQueue(queueComboBox.getValue());
    }

    public void clearUiAndLoadQueue(MatchmakerQueue matchmakerQueue) {
        clearUI();
        loadMatchMakerQueue(matchmakerQueue);
    }

    private void clearUI() {
        bracketListContainer.getChildren().clear();
        bracketHeaderContainer.getChildren().clear();
        addButtonsContainer.getChildren().clear();
        ladderPoolImageView.setImage(null);
    }


    private void loadMatchMakerQueue(MatchmakerQueue matchmakerQueue) {
        List<MatchmakerQueueMapPool> brackets = mapService.getListOfBracketsInMatchmakerQueue(matchmakerQueue);
        List<MatchmakerQueueMapPoolFX> bracketsFX = matchmakerQueueMapPoolMapper.mapToFx(brackets);

        for (MatchmakerQueueMapPoolFX bracketFX : bracketsFX) {
            ObservableList<MapVersionFX> mapList = bracketFX.getMapPool().getMapVersions();

            // create the bracket header labels
            BracketRatingController ratingLabelController = uiService.loadFxml("ui/main_window/bracketRatingLabel.fxml");
            ratingLabelController.setRatingLabelText(getBracketRatingString(bracketFX));
            ratingLabelController.root.prefWidthProperty().bind((bracketsScrollPane.widthProperty().divide(bracketsFX.size())).subtract(16 / bracketsFX.size()));
            bracketHeaderContainer.getChildren().add(ratingLabelController.getRoot());

            // create the bracket list views
            BracketListViewController listViewController = uiService.loadFxml("ui/main_window/bracketListView.fxml");
            listViewController.setMaps(mapList);
            listViewController.mapListView.prefWidthProperty().bind((bracketsScrollPane.widthProperty().divide(bracketsFX.size())).subtract(16 / bracketsFX.size()));
            bracketListContainer.getChildren().add(listViewController.getRoot());

            // create the add/remove buttons
            AddBracketController addBracketController = uiService.loadFxml("ui/main_window/addBracketLabelAndButton.fxml");
            addBracketController.setRatingLabelText(getBracketRatingString(bracketFX));
            addButtonsContainer.getChildren().add(addBracketController.getRoot());

            bindListViewSelectionToSelectedMapProperty(listViewController.mapListView);
            bindSelectedMapPropertyToAddRemoveButtons(mapList, addBracketController);

            uploadToDatabaseButton.setOnAction(event -> {
                for (MatchmakerQueueMapPoolFX bracket : bracketsFX) {
                    mapService.patchMapPool(bracket.getMapPool());
                }
            });
        }
    }

    private void bindSelectedMapPropertyToImageView() {
        selectedMap.addListener( (observable, oldValue, newValue) -> {
            if (newValue == null) return;
            URL thumbnailUrlLarge = newValue.getThumbnailUrlLarge();
            if (thumbnailUrlLarge != null) {
                ladderPoolImageView.setImage(largeThumbnailCache.fromIdAndString(newValue.getId(), thumbnailUrlLarge.toString()));
            } else {
                ladderPoolImageView.setImage(null);
            }
        });
    }

    private void bindListViewSelectionToSelectedMapProperty(ListView<MapVersionFX> listView) {
        ChangeListener<MapVersionFX> listener = (observable, oldValue, newValue) -> {
            if (newValue == null) return;
            selectedMap.setValue(newValue);
        };

        listView.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            MultipleSelectionModel<MapVersionFX> selectionModel = listView.getSelectionModel();
            ReadOnlyObjectProperty<MapVersionFX> selectedItemProperty = selectionModel.selectedItemProperty();
            if (newValue) {
                selectedItemProperty.addListener(listener);
                if (selectedItemProperty.getValue() != null) {
                    selectedMap.setValue(selectedItemProperty.getValue());
                }
            } else {
                selectedItemProperty.removeListener(listener);
                selectionModel.clearSelection();
            }
        }));
    }

    private void bindSelectedMapPropertyToAddRemoveButtons(ObservableList<MapVersionFX> mapList, AddBracketController controller) {
        selectedMap.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            Button addButton = controller.addToBracketButton;
            Button removeButton = controller.removeFromBracketButton;
            // enable add/remove buttons
            addButton.setDisable(mapList.contains(newValue));
            removeButton.setDisable(!mapList.contains(newValue));
            //bind actions for add and remove buttons
            addButton.setOnAction(event -> {
                    mapList.add(newValue);
                    addButton.setDisable(true);
                    removeButton.setDisable(false);
            });
            removeButton.setOnAction(event -> {
                    mapList.remove(newValue);
                    // prevents the client from changing the selection when removing the currently selected map
                    selectedMap.setValue(newValue);
                    removeButton.setDisable(true);
                    addButton.setDisable(false);
            });
        });
    }

    private String getBracketRatingString(MatchmakerQueueMapPoolFX bracket) {
        var min = bracket.getMinRating();
        var max = bracket.getMaxRating();
        if (min == 0) return String.format("<%d", (int)max);
        if (max == 0) return String.format(">%d", (int)min);
        return String.format("%d - %d", (int)min, (int)max);
    }

    public void refresh() {
        clearUI();
    }

    public void onSearchMapVault() {
        mapVaultView.getRoot().getChildren().clear();
        mapVaultView.getSortOrder().clear();
        String mapNamePattern = null;

        if (filterByMapNameCheckBox.isSelected()) {
            mapNamePattern = mapNamePatternTextField.getText();
        }

        ViewHelper.fillMapTreeView(mapVaultView,
                mapService.findMaps(mapNamePattern).stream());
    }

}
