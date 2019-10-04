package com.faforever.moderatorclient.ui;


import com.faforever.moderatorclient.api.domain.MapService;
import com.faforever.moderatorclient.api.domain.TutorialService;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import com.faforever.moderatorclient.ui.domain.TutorialCategoryFX;
import com.faforever.moderatorclient.ui.domain.TutorialFx;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class TutorialAddController implements Controller<Pane> {
    private final TutorialService tutorialService;
    private final MapService mapService;

    public TextField descriptionField;
    public TextField titleField;
    public TextField imageField;
    public TextField categoryField;
    public TextField ordinalField;
    public TextField mapField;
    public GridPane root;
    public CheckBox launchableBox;
    public TextField technicalNameField;

    private Runnable onSaveRunnable;

    @Override
    public Pane getRoot() {
        return root;
    }

    public void setCategoryId(int id) {
        categoryField.setText(String.valueOf(id));
    }

    public void onSave() {
        if (!validate()) {
            return;
        }
        TutorialFx tutorialFx = new TutorialFx();
        tutorialFx.setTitleKey(titleField.getText());
        tutorialFx.setDescriptionKey(descriptionField.getText());
        tutorialFx.setLaunchable(launchableBox.isSelected());
        String mapId = mapField.getText();
        if (!mapId.isEmpty()) {
            MapVersionFX mapVersionFX = new MapVersionFX();
            mapVersionFX.setId(mapId);
            tutorialFx.setMapVersion(mapVersionFX);
        }
        int categoryId = Integer.parseInt(categoryField.getText());
        TutorialCategoryFX tutorialCategoryFX = new TutorialCategoryFX();
        tutorialCategoryFX.setId(categoryId);
        tutorialFx.setCategory(tutorialCategoryFX);
        tutorialFx.setImage(imageField.getText());
        tutorialFx.setOrdinal(Integer.parseInt(ordinalField.getText()));
        tutorialFx.setTechnicalName(technicalNameField.getText());
        try {
            if (tutorialService.create(tutorialFx) == null) {
                ViewHelper.errorDialog("Error", "Not saved unknown error");
                return;
            }
        } catch (Exception e) {
            ViewHelper.errorDialog("Unable to save Tutorial", e.getMessage());
            log.warn("Tutorial not saved", e);
            return;
        }

        close();
        if (onSaveRunnable != null) {
            onSaveRunnable.run();
        }
    }

    private boolean validate() {
        List<String> validationErrors = new ArrayList<>();

        if (!mapField.getText().isEmpty()) {
            try {
                if (!mapService.doesMapVersionExist(Integer.parseInt(mapField.getText()))) {
                    log.warn("Could not get map version with id:{}", mapField.getText());
                    validationErrors.add(String.format("Could not find Map Version with id: %s", mapField.getText()));
                }
            } catch (Exception e) {
                log.info("Map id must be a valid Number", e);
                validationErrors.add("Map version id must be a valid Number");
            }
        }
        if (titleField.getText().isEmpty()) {
            validationErrors.add("Title can not be empty");
        }
        if (technicalNameField.getText().isEmpty()) {
            validationErrors.add("Technical name must be set");
        }
        if (descriptionField.getText().isEmpty()) {
            validationErrors.add("Description can not be empty");
        }
        if (imageField.getText().isEmpty()) {
            validationErrors.add("Image url can not be empty");
        }
        try {
            Integer.parseInt(ordinalField.getText());
        } catch (Exception e) {
            validationErrors.add("Ordinal must be a valid Number and not empty");
        }
        if (validationErrors.size() > 0) {
            ViewHelper.errorDialog("Validation failed",
                    String.join("\n", validationErrors));
            return false;
        }

        return true;
    }

    private void close() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void setOnSave(Runnable onSaveRunnable) {
        this.onSaveRunnable = onSaveRunnable;
    }
}

