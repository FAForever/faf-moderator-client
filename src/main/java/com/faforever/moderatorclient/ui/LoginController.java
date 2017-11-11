package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.FafApiCommunicationService;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoginController implements Controller<Pane> {
    private final FafApiCommunicationService fafApiCommunicationService;
    public DialogPane root;
    public TextField usernameField;
    public PasswordField passwordField;
    public Label loginFailedLabel;

    public LoginController(FafApiCommunicationService fafApiCommunicationService) {
        this.fafApiCommunicationService = fafApiCommunicationService;
    }

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        loginFailedLabel.setVisible(false);
    }

    public void onLoginClicked() {
        fafApiCommunicationService.authorize(usernameField.getText(), passwordField.getText());
        try {
            fafApiCommunicationService.getOne("/me", Object.class);
            // TODO: Check that the user is actually a moderator
        } catch (OAuth2AccessDeniedException e) {
            loginFailedLabel.setVisible(true);
            return;
        }
        root.getScene().getWindow().hide();
    }

    public void onQuitClicked() {
        System.exit(0);
    }
}
