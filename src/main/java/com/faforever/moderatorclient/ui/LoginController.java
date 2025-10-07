package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.FafUserCommunicationService;
import com.faforever.moderatorclient.api.TokenService;
import com.faforever.moderatorclient.api.event.ApiAuthorizedEvent;
import com.faforever.moderatorclient.config.ApplicationProperties;
import com.faforever.moderatorclient.config.EnvironmentProperties;
import com.faforever.moderatorclient.config.local.LocalPreferences;
import com.faforever.moderatorclient.login.OAuthValuesReceiver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginController implements Controller<Pane> {
    private final ApplicationProperties applicationProperties;
    private final LocalPreferences localPreferences;
    private final FafApiCommunicationService fafApiCommunicationService;
    private final FafUserCommunicationService fafUserCommunicationService;
    private final TokenService tokenService;
    private final OAuthValuesReceiver oAuthValuesReceiver;

    public VBox root;
    public ComboBox<String> environmentComboBox;
    public CheckBox rememberLoginCheckBox;

    private CompletableFuture<Void> loginFuture;

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        applicationProperties.getEnvironments().forEach(
                (key, environmentProperties) -> environmentComboBox.getItems().add(key)
        );

        environmentComboBox.getSelectionModel().select(0);
    }

    public void rememberLogin() {
        localPreferences.getAutoLogin().setEnabled(rememberLoginCheckBox.isSelected());
    }

    public void attemptLogin() {
        EnvironmentProperties environmentProperties = applicationProperties.getEnvironments().get(environmentComboBox.getValue());
        fafApiCommunicationService.initialize(environmentProperties);
        fafUserCommunicationService.initialize(environmentProperties);
        localPreferences.getAutoLogin().setEnvironment(environmentComboBox.getValue());
        tokenService.prepare(environmentProperties);
        loginFuture = oAuthValuesReceiver.receiveValues(environmentProperties).thenAccept(tokenService::loginWithAuthorizationCode);
    }

    public void cancelLogin() {
        if (loginFuture != null) {
            loginFuture.cancel(true);
        }
    }

    @EventListener
    public void onApiAuthorized(ApiAuthorizedEvent event) {
        if (root == null) return;

        Platform.runLater(() -> root.getScene().getWindow().hide());
    }
}
