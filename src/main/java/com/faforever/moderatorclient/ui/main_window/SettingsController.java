package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.config.local.LocalPreferences;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.MainController;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@Slf4j
@RequiredArgsConstructor
public class SettingsController implements Controller<Pane> {
    private final LocalPreferences localPreferences;
    private final MainController mainController;

    public VBox root;
    public CheckBox rememberLoginCheckBox;
    public CheckBox darkModeCheckBox;
    public ComboBox<Tab> defaultActiveTabComboBox;

    @Override
    public VBox getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        defaultActiveTabComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Tab tab) {
                if (tab == null) return "";
                else return tab.getText();
            }

            @Override
            public Tab fromString(String s) {
                return null;
            }
        });

        rememberLoginCheckBox.setSelected(localPreferences.getAutoLogin().isEnabled());
        darkModeCheckBox.setSelected(localPreferences.getUi().isDarkMode());

        mainController.getRoot().getTabs().forEach(tab -> {
            defaultActiveTabComboBox.getItems().add(tab);
            if (Objects.equals(tab.getId(), localPreferences.getUi().getStartUpTab())) {
                defaultActiveTabComboBox.getSelectionModel().select(tab);
            }
        });
    }

    public void onSave() {
        log.info("Saving settings");

        localPreferences.getAutoLogin().setEnabled(rememberLoginCheckBox.isSelected());
        localPreferences.getUi().setDarkMode(darkModeCheckBox.isSelected());
        localPreferences.getUi().setStartUpTab(defaultActiveTabComboBox.getSelectionModel().getSelectedItem().getId());

        Scene scene = root.getScene();
        String styleSheet = "/style/main-light.css";
        if (darkModeCheckBox.isSelected()) {
            styleSheet = "/style/main-dark.css";
        }

        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(styleSheet).toExternalForm());
    }
}
