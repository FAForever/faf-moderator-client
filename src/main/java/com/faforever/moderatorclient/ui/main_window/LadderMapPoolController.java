package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.Map;
import com.faforever.moderatorclient.api.domain.MapService;
import com.faforever.moderatorclient.mapstruct.MapMapper;
import com.faforever.moderatorclient.mapstruct.MapVersionMapper;
import com.faforever.moderatorclient.ui.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Set;

@Slf4j
@Component
public class LadderMapPoolController implements Controller<SplitPane> {
    private final MapService mapService;
    private final MapMapper mapMapper;
    private final MapVersionMapper mapVersionMapper;

    public SplitPane root;

    public TreeTableView<MapTableItemAdapter> ladderPoolView;
    public TreeTableView<MapTableItemAdapter> mapVaultView;
    public CheckBox filterByMapNameCheckBox;
    public TextField mapNamePatternTextField;
    public ImageView ladderPoolImageView;
    public ImageView mapVaultImageView;
    public Button removeFromPoolButton;
    public Button addToPoolButton;
    private final UiService uiService;
    private Set<Map> mapsInLadder1v1Pool;

    public LadderMapPoolController(MapService mapService, MapMapper mapMapper, MapVersionMapper mapVersionMapper, UiService uiService) {
        this.mapService = mapService;
        this.mapMapper = mapMapper;
        this.mapVersionMapper = mapVersionMapper;
        this.uiService = uiService;
    }

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildMapTreeView(ladderPoolView);
        ViewHelper.bindMapTreeViewToImageView(ladderPoolView, ladderPoolImageView);

        ladderPoolView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                removeFromPoolButton.setDisable(true);
            } else {
                removeFromPoolButton.setDisable(!newValue.getValue().isMapVersion());
            }
        });

        ViewHelper.buildMapTreeView(mapVaultView);
        ViewHelper.bindMapTreeViewToImageView(mapVaultView, mapVaultImageView);
        mapVaultView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                addToPoolButton.setDisable(true);
            } else {
                addToPoolButton.setDisable(!newValue.getValue().isMapVersion());
            }
        });
    }

    public void refresh() {
        ladderPoolView.getRoot().getChildren().clear();
        ladderPoolView.getSortOrder().clear();
        mapsInLadder1v1Pool = mapService.findMapsInLadder1v1Pool();
        mapsInLadder1v1Pool
                .forEach(map -> {
                    TreeItem<MapTableItemAdapter> mapItem = new TreeItem<>(new MapTableItemAdapter(map));
                    ladderPoolView.getRoot().getChildren().add(mapItem);

                    map.getVersions().forEach(mapVersion -> mapItem.getChildren().add(new TreeItem<>(new MapTableItemAdapter(mapVersion))));
                });
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
        MapTableItemAdapter itemAdapter = ladderPoolView.getSelectionModel().getSelectedItem().getValue();
        Assert.isTrue(itemAdapter.isMapVersion(), "Only map version can be removed");

        mapService.removeMapVersionFromLadderPool(itemAdapter.getMapVersion());
        refresh();
    }

    public void onAddToLadderPool() {
        MapTableItemAdapter itemAdapter = mapVaultView.getSelectionModel().getSelectedItem().getValue();
        Assert.isTrue(itemAdapter.isMapVersion(), "Only map version can be added");

        mapService.addMapVersionToLadderPool(itemAdapter.getMapVersion());
        refresh();
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
