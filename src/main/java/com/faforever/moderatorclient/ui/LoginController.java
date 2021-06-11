package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.MeResult;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.TokenService;
import com.faforever.moderatorclient.api.event.ApiAuthorizedEvent;
import com.faforever.moderatorclient.api.event.TokenExpiredEvent;
import com.faforever.moderatorclient.config.ApplicationProperties;
import com.faforever.moderatorclient.config.EnvironmentProperties;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginController implements Controller<Pane> {
    private final ApplicationProperties applicationProperties;
    private final FafApiCommunicationService fafApiCommunicationService;
    private final TokenService tokenService;

    public DialogPane root;
    public ComboBox<String> environmentComboBox;
    public WebView loginWebView;
    public String state;

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        applicationProperties.getEnvironments().forEach(
                (key, environmentProperties) -> environmentComboBox.getItems().add(key)
        );

        environmentComboBox.setOnAction((event) -> loginWebView.getEngine().load(getHydraUrl()));

        environmentComboBox.getSelectionModel().select(0);

        loginWebView.getEngine().load(getHydraUrl());

        loginWebView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            int codeIndex = newValue.indexOf("code=");
            if (codeIndex >= 0) {
                int codeEnd = newValue.indexOf("&", codeIndex);
                String code = newValue.substring(codeIndex + 5, codeEnd);
                int stateIndex = newValue.indexOf("state=");
                int stateEnd = newValue.indexOf("&", stateIndex);
                String reportedState;
                if (stateEnd > 0) {
                    reportedState = newValue.substring(stateIndex + 6, stateEnd);
                } else {
                    reportedState = newValue.substring(stateIndex + 6);
                }

                if (!state.equals(reportedState)) {
                    log.error("States do not match We are under attack!");
                    return;
                }

                tokenService.loginWithAuthorizationCode(code);
            }
        });
    }

    public String getHydraUrl() {
        EnvironmentProperties environmentProperties = applicationProperties.getEnvironments().get(environmentComboBox.getValue());
        fafApiCommunicationService.initialize(environmentProperties);
        tokenService.prepare(environmentProperties);
        state = RandomStringUtils.randomAlphanumeric(50, 100);
        return String.format("%s/oauth2/auth?response_type=code&client_id=%s" +
                        "&state=%s&redirect_uri=%s" +
                        "&scope=%s",
                environmentProperties.getOauthBaseUrl(), environmentProperties.getClientId(), state, environmentProperties.getOauthRedirectUrl(), environmentProperties.getOauthScopes());
    }

    @EventListener
    public void onApiAuthorized(ApiAuthorizedEvent event) {
        root.getScene().getWindow().hide();
    }
}
