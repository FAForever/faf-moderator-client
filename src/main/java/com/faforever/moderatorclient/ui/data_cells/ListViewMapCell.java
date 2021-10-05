package com.faforever.moderatorclient.ui.data_cells;

import com.faforever.commons.api.dto.NeroxisGeneratorParams;
import com.faforever.moderatorclient.ui.caches.SmallThumbnailCache;
import com.faforever.moderatorclient.ui.domain.MapPoolAssignmentFX;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ListViewMapCell extends ListCell<MapPoolAssignmentFX> {

    @FXML private Label mapNameLabel;
    @FXML private Label mapVersionLabel;
    @FXML private Label mapSizeLabel;
    @FXML private Label mapParamsLabel;
    @FXML private Label weightLabel;
    @FXML private Pane cellContainer;
    @FXML private ImageView previewImageView;
    @FXML private Button weightDownButton;
    @FXML private Button weightUpButton;
    private FXMLLoader mLLoader;

    private final SmallThumbnailCache smallThumbnailCache;

    public void initialize() {
        mapSizeLabel.managedProperty().bind(mapSizeLabel.visibleProperty());
        mapNameLabel.managedProperty().bind(mapNameLabel.visibleProperty());
        mapParamsLabel.managedProperty().bind(mapParamsLabel.visibleProperty());
    }

    @SneakyThrows
    @Override
    protected void updateItem(MapPoolAssignmentFX mapPoolAssignmentFX, boolean empty) {
        super.updateItem(mapPoolAssignmentFX, empty);
        if (empty || mapPoolAssignmentFX == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/ui/main_window/poolListItem.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mapPoolAssignmentFX.getMapVersion() != null) {
                MapVersionFX mapVersionFX = mapPoolAssignmentFX.getMapVersion();
                int mapWidth = Math.round(mapVersionFX.getWidth() / 51.2f);
                int mapHeight = Math.round(mapVersionFX.getHeight() / 51.2f);
                mapNameLabel.setText(mapVersionFX.getMap().getDisplayName());
                mapNameLabel.setVisible(true);
                mapVersionLabel.setText(mapVersionFX.getVersion().getCanonical());
                mapVersionLabel.setVisible(true);
                mapSizeLabel.setText(String.format("%dx%dkm", mapWidth, mapHeight));
                mapSizeLabel.setVisible(true);
                previewImageView.setImage(smallThumbnailCache.fromIdAndString(mapVersionFX.getId(), mapVersionFX.getThumbnailUrlLarge().toString()));
                mapParamsLabel.setVisible(false);
                setText(null);
                setGraphic(cellContainer);
            } else if (mapPoolAssignmentFX.getMapParams() instanceof NeroxisGeneratorParams){
                NeroxisGeneratorParams neroxisParams = (NeroxisGeneratorParams) mapPoolAssignmentFX.getMapParams();
                int mapSize = Math.round(neroxisParams.getSize() / 51.2f);
                mapNameLabel.setVisible(false);
                mapSizeLabel.setVisible(false);
                mapVersionLabel.setVisible(false);
                mapParamsLabel.setText(String.format("Version: %s\nSpawns: %d\nSize: %d\n",
                        neroxisParams.getVersion(),
                        neroxisParams.getSpawns(),
                        neroxisParams.getSize()));
                mapParamsLabel.setVisible(true);
                previewImageView.setImage(new Image(new ClassPathResource("/media/generatedMapIcon.png").getURL().toString(), true));
                setText(null);
                setGraphic(cellContainer);
            }
            weightDownButton.setOnAction(event -> mapPoolAssignmentFX.setWeight(Math.max(0, mapPoolAssignmentFX.getWeight() - 1)));
            weightUpButton.setOnAction(event -> mapPoolAssignmentFX.setWeight(mapPoolAssignmentFX.getWeight() + 1));
            weightLabel.textProperty().bind(Bindings.format("%d", mapPoolAssignmentFX.weightProperty()));
        }
    }
}


