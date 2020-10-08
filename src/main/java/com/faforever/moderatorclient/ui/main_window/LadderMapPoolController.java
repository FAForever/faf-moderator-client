package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.Map;
import com.faforever.moderatorclient.api.domain.MapService;
import com.faforever.moderatorclient.common.MatchmakerQueue;
import com.faforever.moderatorclient.common.MatchmakerQueueMapPool;
import com.faforever.moderatorclient.mapstruct.MapMapper;
import com.faforever.moderatorclient.mapstruct.MapVersionMapper;
import com.faforever.moderatorclient.mapstruct.MatchmakerQueueMapPoolMapper;
import com.faforever.moderatorclient.mapstruct.MatchmakerQueueMapper;
import com.faforever.moderatorclient.ui.*;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import com.faforever.moderatorclient.ui.domain.MatchmakerQueueMapPoolFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

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

    public SplitPane root;

    public HBox bracketListContainer;
    public HBox bracketHeaderContainer;
    public TreeTableView<MapTableItemAdapter> mapVaultView;
    public CheckBox filterByMapNameCheckBox;
    public TextField mapNamePatternTextField;
    public ImageView ladderPoolImageView;
    public ImageView mapVaultImageView;
    public Button removeFromBracketButton;
    public Button removeFromAllBracketsButton;
    public Button addToPoolButton;
    public Button refreshButton;
    public Button uploadToDatabaseButton;
    public ComboBox<MatchmakerQueue> queueComboBox;
    public ScrollPane bracketsScrollPane;
    public VBox addButtonsContainer;
    private Set<Map> mapsInLadder1v1Pool;

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildMapTreeView(mapVaultView);
        ViewHelper.bindMapTreeViewToImageView(mapVaultView, mapVaultImageView);
        mapVaultView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                addToPoolButton.setDisable(true);
            } else {
                addToPoolButton.setDisable(!newValue.getValue().isMapVersion());
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
        disableRemoveMapButtons(true);
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
            listViewController.setMaps(bracketFX.getMapPool().getMapVersions());
            ViewHelper.bindListViewToImageView(listViewController.mapListView, ladderPoolImageView);
            listViewController.mapListView.prefWidthProperty().bind((bracketsScrollPane.widthProperty().divide(bracketsFX.size())).subtract(16 / bracketsFX.size()));
            bracketListContainer.getChildren().add(listViewController.getRoot());

            addListViewChangeLogic(listViewController.mapListView, mapList, bracketsFX);

            // create the add to bracket buttons
            AddBracketController addBracketController = uiService.loadFxml("ui/main_window/addBracketLabelAndButton.fxml");
            addBracketController.setRatingLabelText(getBracketRatingString(bracketFX));
            addButtonsContainer.getChildren().add(addBracketController.getRoot());

            addMapVaultAddButtonLogic(addBracketController.addToBracketButton, mapList);

            uploadToDatabaseButton.setOnAction(event -> {
                for (MatchmakerQueueMapPoolFX bracket : bracketsFX) {
                    mapService.patchMapPool(bracket.getMapPool());
                }
            });
        }
    }

    // enables the add map button for a bracket when appropriate
    private void addMapVaultAddButtonLogic(Button addButton, List<MapVersionFX> mapList) {
        mapVaultView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null || !newValue.getValue().isMapVersion()) {
                addButton.setDisable(true);
                return;
            }
            MapVersionFX map = mapVersionMapper.map(newValue.getValue().getMapVersion());
            if (mapList.contains(map)) {
                addButton.setDisable(true);
                return;
            }
            addButton.setOnAction(actionEvent -> {
                mapList.add(map);
                addButton.setDisable(true);
            });
            addButton.setDisable(false);
        });
    }

    // enables remove buttons when appropriate and prompts vault display to refresh
    private void addListViewChangeLogic(ListView<MapVersionFX> listView, ObservableList<MapVersionFX> mapList, List<MatchmakerQueueMapPoolFX> bracketsFX) {
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || !listView.getItems().contains(newValue)) return;
            disableRemoveMapButtons(false);
            removeFromBracketButton.setOnAction(actionEvent -> {
                mapList.remove(newValue);
                reselectMapVaultRow();                                                          // prompt refresh
                disableRemoveMapButtons(true);
                if (bracketsContainMapVersionFX(bracketsFX, newValue)) {
                    removeFromAllBracketsButton.setDisable(false);
                } else {
                    ladderPoolImageView.setImage(null);
                }
            });
            removeFromAllBracketsButton.setOnAction(actionEvent -> {
                for (MatchmakerQueueMapPoolFX bracket : bracketsFX) {
                    ObservableList<MapVersionFX> maps = bracket.getMapPool().getMapVersions();
                    maps.remove(newValue);
                }
                reselectMapVaultRow();
                disableRemoveMapButtons(true);
                ladderPoolImageView.setImage(null);
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

    // returns true if any of the provided brackets contain the map
    private boolean bracketsContainMapVersionFX(List<MatchmakerQueueMapPoolFX> brackets, MapVersionFX mapVersionFX) {
        return brackets.stream().map(it -> it.getMapPool().getMapVersions()).anyMatch(it -> it.contains(mapVersionFX));
    }

    private void reselectMapVaultRow() {
        var selectedRow = mapVaultView.getSelectionModel().getSelectedIndex();
        mapVaultView.getSelectionModel().clearSelection();
        mapVaultView.getSelectionModel().select(selectedRow);
    }

    private void disableRemoveMapButtons(boolean disable) {
        removeFromBracketButton.setDisable(disable);
        removeFromAllBracketsButton.setDisable(disable);
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

    public void onRemoveFromLadderPool() {
//        MapTableItemAdapter itemAdapter = ladderPoolView.getSelectionModel().getSelectedItem().getValue();
//        Assert.isTrue(itemAdapter.isMapVersion(), "Only map version can be removed");
//
//        mapService.removeMapVersionFromLadderPool(itemAdapter.getMapVersion());
//        refresh();
    }

    public void onAddToLadderPool() {
//        MapTableItemAdapter itemAdapter = mapVaultView.getSelectionModel().getSelectedItem().getValue();
//        Assert.isTrue(itemAdapter.isMapVersion(), "Only map version can be added");
//
//        mapService.addMapVersionToLadderPool(itemAdapter.getMapVersion());
//        refresh();
    }

    public void generateLadderMapReviewVote(ActionEvent actionEvent) {
        LadderMapVoteGenerationFormController ladderMapVoteGenerationFormController = uiService.loadFxml("ui/ladderMapVoteGenerationForm.fxml");
        ladderMapVoteGenerationFormController.setGivenMaps(mapsInLadder1v1Pool);
        Stage newCategoryDialog = new Stage();
        newCategoryDialog.setTitle("Generate ladder vote");
        newCategoryDialog.setScene(new Scene(ladderMapVoteGenerationFormController.getRoot()));
        newCategoryDialog.showAndWait();
    }
}
