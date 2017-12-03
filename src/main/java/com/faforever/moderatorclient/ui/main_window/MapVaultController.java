package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.dto.Map;
import com.faforever.moderatorclient.api.rest.domain.MapService;
import com.faforever.moderatorclient.mapstruct.MapMapper;
import com.faforever.moderatorclient.mapstruct.MapVersionMapper;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.MapFX;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MapVaultController implements Controller<SplitPane> {
    private final MapService mapService;
    private final MapMapper mapMapper;
    private final MapVersionMapper mapVersionMapper;

    public SplitPane root;

    public RadioButton searchMapByMapNameRadioButton;
    public RadioButton searchMapByAuthorIdRadioButton;
    public RadioButton searchMapByAuthorNameRadioButton;
    public TextField mapSearchTextField;
    public CheckBox excludeHiddenMapVersionsCheckbox;
    public ImageView mapVersionPreviewImageView;
    public TableView<MapFX> mapSearchTableView;
    public TableView<MapVersionFX> mapVersionTableView;
    public Button toggleMapVersionHidingButton;
    public Button toggleMapVersionRatingButton;

    public MapVaultController(MapService mapService, MapMapper mapMapper, MapVersionMapper mapVersionMapper) {
        this.mapService = mapService;
        this.mapMapper = mapMapper;
        this.mapVersionMapper = mapVersionMapper;
    }

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildMapTableView(mapSearchTableView);
        ViewHelper.buildMapVersionTableView(mapVersionTableView);

        mapSearchTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mapVersionTableView.getItems().clear();
            Optional.ofNullable(newValue).ifPresent(map -> mapVersionTableView.getItems().addAll(
                    map.getVersions()));
        });

        mapVersionTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                mapVersionPreviewImageView.setImage(null);
                toggleMapVersionHidingButton.setDisable(true);
                toggleMapVersionRatingButton.setDisable(true);
            } else {
                mapVersionPreviewImageView.setImage(new Image(newValue.getThumbnailUrlLarge().toString()));
                toggleMapVersionHidingButton.setDisable(false);
                toggleMapVersionRatingButton.setDisable(false);
            }
        });
    }

    public void onSearchMaps() {
        mapSearchTableView.getItems().clear();
        mapSearchTableView.getSortOrder().clear();

        List<Map> mapsFound = Collections.emptyList();
        String searchPattern = mapSearchTextField.getText();
        if (searchMapByMapNameRadioButton.isSelected()) {
            mapsFound = mapService.findMapsByName(searchPattern, excludeHiddenMapVersionsCheckbox.isSelected());
        } else if (searchMapByAuthorIdRadioButton.isSelected()) {
            mapsFound = mapService.findMapsByAuthorId(searchPattern, excludeHiddenMapVersionsCheckbox.isSelected());
        } else if (searchMapByAuthorNameRadioButton.isSelected()) {
            mapsFound = mapService.findMapsByAuthorName(searchPattern, excludeHiddenMapVersionsCheckbox.isSelected());
        }

        mapSearchTableView.getItems().addAll(mapMapper.map(mapsFound));
    }

    public void onToggleMapVersionHiding() {
        MapVersionFX mapVersion = mapVersionTableView.getSelectionModel().getSelectedItem();

        Assert.notNull(mapVersion, "You can only edit a selected MapVersion");

        mapVersion.setHidden(!mapVersion.isHidden());
        mapService.patchMapVersion(mapVersionMapper.map(mapVersion));
    }

    public void onToggleMapVersionRanking() {
        MapVersionFX mapVersion = mapVersionTableView.getSelectionModel().getSelectedItem();

        Assert.notNull(mapVersion, "You can only edit a selected MapVersion");

        mapVersion.setRanked(!mapVersion.isRanked());
        mapService.patchMapVersion(mapVersionMapper.map(mapVersion));
    }
}
