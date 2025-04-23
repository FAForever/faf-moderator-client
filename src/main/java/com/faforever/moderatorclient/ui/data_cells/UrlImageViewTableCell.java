package com.faforever.moderatorclient.ui.data_cells;

import com.faforever.moderatorclient.ui.caches.AvatarCache;
import com.faforever.moderatorclient.ui.domain.AvatarAssignmentFX;
import com.faforever.moderatorclient.ui.domain.AvatarFX;
import javafx.application.Platform;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
public class UrlImageViewTableCell<T> extends TableCell<T, String> {
    private final ImageView imageView = new ImageView();
    private String currentUrl;

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setGraphic(null);
            return;
        }

        if (!Objects.equals(currentUrl, item)) {
            Thread.startVirtualThread(() -> {
                currentUrl = item;
                Image img;
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    String cacheKey = cacheKeyFrom(item, getTableRow().getItem());
                    if (AvatarCache.getInstance().containsKey(cacheKey)) {
                        img = AvatarCache.getInstance().get(cacheKey);
                    } else {
                        log.debug("Loading avatar image from url: {}", item);
                        img = new Image(item, true);
                        AvatarCache.getInstance().put(cacheKey, img);
                    }
                } else {
                    img = new Image(item);
                }

                Platform.runLater(() -> {
                    imageView.setImage(img);
                    setGraphic(imageView);
                });
            });
        } else {
            setGraphic(imageView);
        }
    }

    private String cacheKeyFrom(String item, Object rawData) {
        OffsetDateTime updateTime = null;
        if (rawData instanceof AvatarFX) {
            updateTime = ((AvatarFX) rawData).getUpdateTime();
        } else if (rawData instanceof AvatarAssignmentFX) {
            updateTime = ((AvatarAssignmentFX) rawData).getUpdateTime();
        }
        return updateTime != null ? item + updateTime.toString() : item;
    }
}