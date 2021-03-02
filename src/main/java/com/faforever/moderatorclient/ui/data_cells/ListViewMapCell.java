package com.faforever.moderatorclient.ui.data_cells;

import com.faforever.moderatorclient.ui.caches.SmallThumbnailCache;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ListViewMapCell extends ListCell<MapVersionFX> {

    @FXML private Label mapNameLabel;
    @FXML private Label mapSizeLabel;
    @FXML private Pane cellContainer;
    @FXML private ImageView previewImageView;
    private FXMLLoader mLLoader;

    private final SmallThumbnailCache smallThumbnailCache;

    @Override
    protected void updateItem(MapVersionFX mapVersionFX, boolean empty) {
        super.updateItem(mapVersionFX, empty);
        if (empty || mapVersionFX == null) {
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
            mapNameLabel.setText(mapVersionFX.getMap().getDisplayName());
            int mapWidth = Math.round(mapVersionFX.getWidth() / 51.2f);
            int mapHeight = Math.round(mapVersionFX.getHeight() / 51.2f);
            mapSizeLabel.setText(String.format("%dx%dkm", mapWidth, mapHeight));
            previewImageView.setImage(smallThumbnailCache.fromIdAndString(mapVersionFX.getId(), mapVersionFX.getThumbnailUrlLarge().toString()));
            setText(null);
            setGraphic(cellContainer);
        }
    }
}


