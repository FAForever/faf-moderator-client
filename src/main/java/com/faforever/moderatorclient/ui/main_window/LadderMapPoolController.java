package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.MapPoolAssignment;
import com.faforever.commons.api.dto.MapVersion;
import com.faforever.commons.api.dto.MatchmakerQueue;
import com.faforever.commons.api.dto.MatchmakerQueueMapPool;
import com.faforever.commons.api.dto.NeroxisGeneratorParams;
import com.faforever.moderatorclient.api.domain.MapService;
import com.faforever.moderatorclient.mapstruct.MapPoolAssignmentMapper;
import com.faforever.moderatorclient.mapstruct.MatchmakerQueueMapPoolMapper;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.MapTableItemAdapter;
import com.faforever.moderatorclient.ui.UiService;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.caches.LargeThumbnailCache;
import com.faforever.moderatorclient.ui.domain.MapPoolAssignmentFX;
import com.faforever.moderatorclient.ui.domain.MapPoolFX;
import com.faforever.moderatorclient.ui.domain.MatchmakerQueueMapPoolFX;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LadderMapPoolController implements Controller<SplitPane> {
    public static final double KM_TO_MAP_PIXELS_FACTOR = 51.2;
    public static final double MIN_MAP_SIZE_STEP = 1.25;
    private final MapService mapService;
    private final UiService uiService;
    private final MatchmakerQueueMapPoolMapper matchmakerQueueMapPoolMapper;
    private final MapPoolAssignmentMapper mapPoolAssignmentMapper;
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
    public ComboBox<ComparableVersion> neroxisVersionComboBox;
    public Spinner<Double> neroxisSizeSpinner;
    public Spinner<Integer> neroxisSpawnsSpinner;
    public Label mapParamsLabel;

    private final ObjectProperty<MapPoolAssignmentFX> selectedMap = new SimpleObjectProperty<>();
    private final BiPredicate<MapPoolAssignmentFX, MapPoolAssignmentFX> matchingPoolAssignmentPredicate = (assignmentFX1, assignmentFX2) ->
            Objects.equals(assignmentFX1.getMapVersion(), assignmentFX2.getMapVersion())
                    && Objects.equals(assignmentFX1.getMapParams(), assignmentFX2.getMapParams());
    private final ObservableList<String> neroxisMapSizes = FXCollections.observableArrayList("5km", "10km", "20km");
    private final int[] neroxisMapSizeValues = new int[]{256, 512, 1024};

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildMapTreeView(mapVaultView);
        bindSelectedMapPropertyToImageView();
        mapVaultView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getValue() != null && newValue.getValue().isMapVersion()) {
                MapVersion mapVersion = newValue.getValue().getMapVersion();
                MapPoolAssignment mapPoolAssignment = new MapPoolAssignment();
                mapPoolAssignment.setMapVersion(mapVersion);
                selectedMap.setValue(mapPoolAssignmentMapper.map(mapPoolAssignment));
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

        neroxisVersionComboBox.setItems(FXCollections.observableArrayList(mapService.getGeneratorVersions()));
        neroxisVersionComboBox.getSelectionModel().selectFirst();
        neroxisSpawnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 16, 2, 2));
        neroxisSizeSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(5, 20, 10, MIN_MAP_SIZE_STEP));

        bracketsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapParamsLabel.managedProperty().bind(mapParamsLabel.visibleProperty());
    }

    public void queueComboAction(ActionEvent event) {
        clearUiAndLoadQueue(queueComboBox.getValue());
    }

    public void clearUiAndLoadQueue(MatchmakerQueue matchmakerQueue) {
        clearUI();
        if (matchmakerQueue != null) {
            loadMatchMakerQueue(matchmakerQueue);
        }
    }

    private void clearUI() {
        bracketListContainer.getChildren().clear();
        bracketHeaderContainer.getChildren().clear();
        addButtonsContainer.getChildren().clear();
        ladderPoolImageView.setImage(null);
    }


    private void loadMatchMakerQueue(MatchmakerQueue matchmakerQueue) {
        List<MatchmakerQueueMapPool> brackets = mapService.getListOfBracketsInMatchmakerQueue(matchmakerQueue);
        List<MapPoolAssignment> mapPoolAssignments = mapService.getListOfMapsInBrackets(brackets);
        List<MatchmakerQueueMapPoolFX> bracketsFX = matchmakerQueueMapPoolMapper.mapToFx(brackets);
        List<MapPoolAssignmentFX> mapPoolAssignmentsFX = mapPoolAssignmentMapper.mapToFX(mapPoolAssignments);
        List<List<MapPoolAssignmentFX>> bracketLists = new ArrayList<>();

        for (MatchmakerQueueMapPoolFX bracketFX : bracketsFX) {
            List<MapPoolAssignmentFX> bracketAssignmentList = mapPoolAssignmentsFX.stream()
                    .filter(mapPoolAssignmentFX -> bracketFX.getMapPool().getId().equals(mapPoolAssignmentFX.getMapPool().getId()))
                    .collect(Collectors.toList());
            ObservableList<MapPoolAssignmentFX> bracketAssignments = FXCollections.observableArrayList(bracketAssignmentList);
            bracketLists.add(bracketAssignments);

            // create the bracket header labels
            BracketRatingController ratingLabelController = uiService.loadFxml("ui/main_window/bracketRatingLabel.fxml");
            ratingLabelController.setRatingLabelText(getBracketRatingString(bracketFX));
            ratingLabelController.root.prefWidthProperty().bind((bracketsScrollPane.widthProperty().divide(bracketsFX.size())).subtract(16 / bracketsFX.size()));
            bracketHeaderContainer.getChildren().add(ratingLabelController.getRoot());

            // create the bracket list views
            BracketListViewController listViewController = uiService.loadFxml("ui/main_window/bracketListView.fxml");
            listViewController.setMaps(bracketAssignments);
            listViewController.mapListView.prefWidthProperty().bind((bracketsScrollPane.widthProperty().divide(bracketsFX.size())).subtract(16 / bracketsFX.size()));
            bracketListContainer.getChildren().add(listViewController.getRoot());

            // create the add/remove buttons
            AddBracketController addBracketController = uiService.loadFxml("ui/main_window/addBracketLabelAndButton.fxml");
            addBracketController.setRatingLabelText(getBracketRatingString(bracketFX));
            addButtonsContainer.getChildren().add(addBracketController.getRoot());

            bindListViewSelectionToSelectedMapProperty(listViewController.mapListView);
            bindSelectedMapPropertyToAddRemoveButtons(bracketAssignments, addBracketController, bracketFX.getMapPool());
        }
        uploadToDatabaseButton.setOnAction(event -> {
            List<MapPoolAssignment> oldMapPoolAssignments = mapService.getListOfMapsInBrackets(brackets);
            List<MapPoolAssignmentFX> oldMapPoolAssignmentsFX = mapPoolAssignmentMapper.mapToFX(oldMapPoolAssignments);
            List<MapPoolAssignmentFX> bracketMapPoolAssignments = bracketLists.stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            List<MapPoolAssignmentFX> newMapPoolAssignments = bracketMapPoolAssignments.stream()
                    .filter(mapPoolAssignment -> !oldMapPoolAssignmentsFX.contains(mapPoolAssignment))
                    .collect(Collectors.toList());
            List<MapPoolAssignmentFX> removedMapPoolAssignments = oldMapPoolAssignmentsFX.stream()
                    .filter(mapPoolAssignment -> !bracketMapPoolAssignments.contains(mapPoolAssignment))
                    .collect(Collectors.toList());
            List<MapPoolAssignmentFX> changedMapPoolAssignments = bracketMapPoolAssignments.stream()
                    .filter(mapPoolAssignment -> oldMapPoolAssignmentsFX.stream()
                            .anyMatch(assignmentFX -> Objects.equals(mapPoolAssignment, assignmentFX)
                                    && !mapPoolAssignment.getWeight().equals(assignmentFX.getWeight())))
                    .collect(Collectors.toList());
            mapService.postMapPoolAssignments(mapPoolAssignmentMapper.mapToDTO(newMapPoolAssignments));
            mapService.patchMapPoolAssignments(mapPoolAssignmentMapper.mapToDTO(changedMapPoolAssignments));
            mapService.deleteMapPoolAssignments(mapPoolAssignmentMapper.mapToDTO(removedMapPoolAssignments));
            refresh();
        });
    }

    private void bindSelectedMapPropertyToImageView() {
        selectedMap.addListener((observable) -> {
            MapPoolAssignmentFX newValue = selectedMap.getValue();
            if (newValue == null) return;
            if (newValue.getMapVersion() != null) {
                URL thumbnailUrlLarge = newValue.getMapVersion().getThumbnailUrlLarge();
                if (thumbnailUrlLarge != null) {
                    ladderPoolImageView.setImage(largeThumbnailCache.fromIdAndString(newValue.getMapVersion().getId(), thumbnailUrlLarge.toString()));
                } else {
                    ladderPoolImageView.setImage(null);
                }
                mapParamsLabel.setVisible(false);
            } else if (newValue.getMapParams() instanceof NeroxisGeneratorParams) {
                NeroxisGeneratorParams neroxisParams = (NeroxisGeneratorParams) newValue.getMapParams();
                try {
                    ladderPoolImageView.setImage(new Image(new ClassPathResource("/media/generatedMapIcon.png").getURL().toString(), true));
                } catch (IOException e) {
                    log.warn("Could not load generated map icon", e);
                }
                mapParamsLabel.setText(String.format("Version: %s\nSpawns: %d\nSize: %d\n",
                        neroxisParams.getVersion(),
                        neroxisParams.getSpawns(),
                        neroxisParams.getSize()));
                mapParamsLabel.setVisible(true);
            }
        });
    }

    private void bindListViewSelectionToSelectedMapProperty(ListView<MapPoolAssignmentFX> listView) {
        ChangeListener<MapPoolAssignmentFX> listener = (observable, oldValue, newValue) -> {
            if (newValue == null) return;
            selectedMap.setValue(newValue);
        };

        listView.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            MultipleSelectionModel<MapPoolAssignmentFX> selectionModel = listView.getSelectionModel();
            ReadOnlyObjectProperty<MapPoolAssignmentFX> selectedItemProperty = selectionModel.selectedItemProperty();
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

    private void bindSelectedMapPropertyToAddRemoveButtons(ObservableList<MapPoolAssignmentFX> mapList, AddBracketController controller, MapPoolFX bracketPool) {
        selectedMap.addListener((observable) -> {
            MapPoolAssignmentFX newValue = selectedMap.getValue();
            if (newValue == null) {
                return;
            }
            Button addButton = controller.addToBracketButton;
            Button removeButton = controller.removeFromBracketButton;
            // enable add/remove buttons
            addButton.setDisable(mapList.stream().anyMatch(assignmentFX -> matchingPoolAssignmentPredicate.test(newValue, assignmentFX)));
            removeButton.setDisable(!addButton.isDisable());
            //bind actions for add and remove buttons
            addButton.setOnAction(event -> {
                MapPoolAssignmentFX newAssignment = new MapPoolAssignmentFX()
                        .setWeight(1)
                        .setMapParams(newValue.getMapParams())
                        .setMapVersion(newValue.getMapVersion())
                        .setMapPool(bracketPool);
                mapList.add(newAssignment);
                addButton.setDisable(true);
                removeButton.setDisable(false);
            });
            removeButton.setOnAction(event -> {
                mapList.removeIf(assignmentFX -> matchingPoolAssignmentPredicate.test(newValue, assignmentFX));
                // prevents the client from changing the selection when removing the currently selected map
                selectedMap.setValue(newValue);
                removeButton.setDisable(true);
                addButton.setDisable(false);
            });
        });
    }

    private String getBracketRatingString(MatchmakerQueueMapPoolFX bracket) {
        int min = (int) bracket.getMinRating();
        int max = (int) bracket.getMaxRating();
        if (min == 0) return String.format("<%d", max);
        if (max == 0) return String.format(">%d", min);
        return String.format("%d - %d", min, max);
    }

    public void refresh() {
        MapPoolAssignmentFX currentMap = selectedMap.getValue();
        selectedMap.setValue(null);
        queueComboAction(null);
        selectedMap.setValue(currentMap);
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

    public void onGeneratedMapButton() {
        NeroxisGeneratorParams neroxisGeneratorParams = new NeroxisGeneratorParams();
        neroxisGeneratorParams.setSpawns(neroxisSpawnsSpinner.getValue());
        neroxisGeneratorParams.setSize((int) (neroxisSizeSpinner.getValue() * KM_TO_MAP_PIXELS_FACTOR));
        neroxisGeneratorParams.setVersion(neroxisVersionComboBox.getValue().toString());
        MapPoolAssignment mapPoolAssignment = new MapPoolAssignment();
        mapPoolAssignment.setMapParams(neroxisGeneratorParams);
        selectedMap.setValue(mapPoolAssignmentMapper.map(mapPoolAssignment));
    }

}
