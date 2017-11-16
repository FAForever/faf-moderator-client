package com.faforever.moderatorclient.ui;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class UrlImageViewTableCell<T> extends TableCell<T, String> {
    ImageView imageView = new ImageView();
    String currentUrl;

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            if (!Objects.equals(currentUrl, item)) {
                currentUrl = item;
                imageView.setImage(new Image(item));
            }
            setGraphic(imageView);
        } else {
            setGraphic(null);
        }
    }
}