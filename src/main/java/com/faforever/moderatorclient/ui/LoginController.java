package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.MeResult;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import javafx.fxml.FXML;
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

    public DialogPane root;
    public TextField usernameField;
    public PasswordField passwordField;
    public Label errorMessageLabel;

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
