package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.dto.LegacyAccessLevel;
import com.faforever.moderatorclient.api.rest.FafApiCommunicationService;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginController implements Controller<Pane> {
    private final FafApiCommunicationService fafApiCommunicationService;
    public DialogPane root;
    public TextField usernameField;
    public PasswordField passwordField;
    public Label errorMessageLabel;

    public LoginController(FafApiCommunicationService fafApiCommunicationService) {
        this.fafApiCommunicationService = fafApiCommunicationService;

    }

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        errorMessageLabel.managedProperty().bind(errorMessageLabel.visibleProperty());
        errorMessageLabel.setVisible(false);
    }

    public void onLoginClicked() {
        LegacyAccessLevel accessLevel = fafApiCommunicationService.login(usernameField.getText(), passwordField.getText());

        if (accessLevel == null) {
            errorMessageLabel.setText("Login failed. Please check your credentials.");
            errorMessageLabel.setVisible(true);
        } else if (accessLevel == LegacyAccessLevel.ROLE_USER) {
            errorMessageLabel.setText("You do not have moderator permissions.");
            errorMessageLabel.setVisible(true);
        } else {
            root.getScene().getWindow().hide();
        }
    }

    public void onQuitClicked() {
        System.exit(0);
    }
}
