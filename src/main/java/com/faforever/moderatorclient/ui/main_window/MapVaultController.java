package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.GroupPermission;
import com.faforever.commons.api.dto.Map;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.domain.MapService;
import com.faforever.moderatorclient.mapstruct.MapMapper;
import com.faforever.moderatorclient.mapstruct.MapVersionMapper;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.MapFX;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MapVaultController implements Controller<SplitPane> {
    private final FafApiCommunicationService communicationService;
    private final MapService mapService;
    private final MapMapper mapMapper;
    private final MapVersionMapper mapVersionMapper;
    private final ObservableList<MapFX> maps = FXCollections.observableArrayList();
    private final ObservableList<MapVersionFX> mapVersions = FXCollections.observableArrayList();

    private boolean canEdit = false;

    public SplitPane root;

    public RadioButton searchMapByMapNameRadioButton;
    public RadioButton searchMapByAuthorIdRadioButton;
    public RadioButton searchMapByAuthorNameRadioButton;
    public TextField mapSearchTextField;
    public CheckBox excludeHiddenMapVersionsCheckbox;
    public ImageView mapVersionPreviewImageView;
    public TableView<MapFX> mapSearchTableView;
    public TableView<MapVersionFX> mapVersionTableView;
    public Button hideMapButton;
    public Button toggleMapRecommendationButton;
    public Button toggleMapVersionHidingButton;
    public Button toggleMapVersionRatingButton;

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildMapTableView(mapSearchTableView, maps);
        ViewHelper.buildMapVersionTableView(mapVersionTableView, mapVersions);

        canEdit = communicationService.hasPermission(GroupPermission.ROLE_ADMIN_MAP);

        mapSearchTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mapVersions.clear();

            if (newValue == null) {
                hideMapButton.setDisable(true);
                toggleMapRecommendationButton.setDisable(true);
            } else {
                mapVersions.addAll(newValue.getVersions());
                hideMapButton.setDisable(!canEdit);
                toggleMapRecommendationButton.setDisable(!canEdit);
            }
        });

        mapVersionTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                mapVersionPreviewImageView.setImage(null);
                toggleMapVersionHidingButton.setDisable(true);
                toggleMapVersionRatingButton.setDisable(true);
            } else {
                mapVersionPreviewImageView.setImage(new Image(newValue.getThumbnailUrlLarge().toString()));
                toggleMapVersionHidingButton.setDisable(!canEdit);
                toggleMapVersionRatingButton.setDisable(!canEdit);
            }
        });
    }

    public void onSearchMaps() {
        maps.clear();
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

        maps.addAll(mapMapper.map(mapsFound));
    }

    public void onHideAllVersions() {
        MapFX map = mapSearchTableView.getSelectionModel().getSelectedItem();

        Assert.notNull(map, "You can only edit a selected Map");

        if (ViewHelper.confirmDialog("Bulk map update", MessageFormat.format(
                "You are about to update {0} map versions.\n" +
                        "This might take a while and the client will freeze.\n" +
                        "Do you want to update now?",
                map.getVersions().size()))) {
            map.getVersions().parallelStream()
                    .peek(mapVersionFX -> mapVersionFX.setHidden(true))
                    .map(mapVersionMapper::map)
                    .forEach(mapService::patchMapVersion);
        }
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

    public void onToggleMapRecommendation() {
        MapFX map = mapSearchTableView.getSelectionModel().getSelectedItem();

        Assert.notNull(map, "You can only edit a selected Map");

        map.setRecommended(!map.isRecommended());
        mapService.patchMap(mapMapper.map(map));
    }
}
