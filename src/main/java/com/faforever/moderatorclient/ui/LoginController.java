package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.MeResult;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.config.ApplicationProperties;
import com.faforever.moderatorclient.config.EnvironmentProperties;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginController implements Controller<Pane> {
    private final FafApiCommunicationService fafApiCommunicationService;
    private final ApplicationProperties applicationProperties;

    public DialogPane root;
    public TextField usernameField;
    public PasswordField passwordField;
    public Label errorMessageLabel;
    public ComboBox<String> environmentComboBox;

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        errorMessageLabel.managedProperty().bind(errorMessageLabel.visibleProperty());
        errorMessageLabel.setVisible(false);

        applicationProperties.getEnvironments().forEach(
                (key, environmentProperties) -> environmentComboBox.getItems().add(key)
        );

        environmentComboBox.getSelectionModel().select(0);
    }

    public void onLoginClicked() {
        EnvironmentProperties environmentProperties = applicationProperties.getEnvironments()
                .get(environmentComboBox.getSelectionModel().getSelectedItem());
        fafApiCommunicationService.initialize(environmentProperties);

        MeResult meResult = fafApiCommunicationService.login(usernameField.getText(), passwordField.getText());

        if (meResult == null) {
            errorMessageLabel.setText("Login failed. Please check your credentials.");
            errorMessageLabel.setVisible(true);
        } else {
            root.getScene().getWindow().hide();
        }
    }

    public void onQuitClicked() {
        System.exit(0);
    }
}
