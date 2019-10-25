package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.AvatarService;
import com.faforever.moderatorclient.ui.domain.AvatarFX;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class AvatarInfoController implements Controller<Pane> {
    private final AvatarService avatarService;

    public Runnable onSaveRunnable;
    public GridPane root;
    public TextField tooltipTextField;
    public ImageView imageView;
    public Hyperlink hyperlink;
    private AvatarFX avatarFX;
    private File avatarImageFile;

    @Override
    public Pane getRoot() {
        return root;
    }

    public void setAvatar(AvatarFX avatarFX) {
        this.avatarFX = avatarFX;
        tooltipTextField.setText(avatarFX.getTooltip());
        if (avatarFX.getUrl() != null && avatarFX.getUrl().length() > 0) {
            imageView.setImage(new Image(avatarFX.getUrl()));
            hyperlink.setText(avatarFX.getUrl());
        } else {
            hyperlink.setText("");
            hyperlink.setDisable(true);
        }
    }

    public boolean validate() {
        List<String> validationErrors = new ArrayList<>();

        if (tooltipTextField.getText() == null || tooltipTextField.getText().length() < 3) {
            validationErrors.add("Name / tooltip text is too short");
        }

        if (avatarFX.getId() == null && avatarImageFile == null) {
            validationErrors.add("No image file selected");
        }

        if (validationErrors.size() > 0) {
            ViewHelper.errorDialog("Validation failed",
                    String.join("\n", validationErrors)
            );

            return false;
        }

        return true;
    }

    public void onChooseFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        File selectedFile = fileChooser.showOpenDialog(getRoot().getScene().getWindow());
        if (selectedFile != null) {
            BufferedImage bufferedImage = ImageIO.read(selectedFile);

            if (bufferedImage.getWidth() == 40 && bufferedImage.getHeight() == 20) {
                avatarImageFile = selectedFile;
                imageView.setImage(new Image(new FileInputStream(avatarImageFile)));
            } else {
                ViewHelper.errorDialog("Invalid image", "The avatar size has to be 40x20px");
            }

        }
    }

    public void onSave() {
        Assert.notNull(avatarFX, "You can't save if avatarFX is null.");

        if (!validate()) {
            return;
        }

        if (avatarFX.getId() != null) {
            if (avatarImageFile == null) {
                avatarService.updateAvatarMetadata(avatarFX.getId(), tooltipTextField.getText());
            } else {
                final boolean avatarUpdateConfirmed = ViewHelper.confirmDialog("Update Avatar", "Do you really want to override the avatar?");
                if (!avatarUpdateConfirmed) {
                    return;
                }
                avatarService.reuploadAvatar(avatarFX.getId(), tooltipTextField.getText(), avatarImageFile);
            }
            avatarFX.setTooltip(tooltipTextField.getText());
        } else {
            avatarService.uploadAvatar(tooltipTextField.getText(), avatarImageFile);
        }

        close();
    }

    public void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
