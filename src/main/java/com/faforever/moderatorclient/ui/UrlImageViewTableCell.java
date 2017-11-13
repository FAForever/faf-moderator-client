package com.faforever.moderatorclient.ui;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UrlImageViewTableCell<T> extends TableCell<T, String> {
    ImageView imageView = new ImageView();

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            if (imageView.getImage() == null) {
                imageView.setImage(new Image(item));
            }

            setGraphic(imageView);
        } else {
            setGraphic(null);
        }
    }
}