package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.MessagesService;
import com.faforever.moderatorclient.ui.domain.MessageFx;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageAddController implements Controller<Parent> {
    private final MessagesService messagesService;

    public TextField keyField;
    public TextField languageField;
    public TextField regionField;
    public TextField valueField;
    public GridPane root;
    private Runnable onSaveRunnable;

    @Override
    public Parent getRoot() {
        return root;
    }

    public void onSave() {
        if (!validate()) {
            return;
        }
        MessageFx messageFx = new MessageFx();
        messageFx.setLanguage(languageField.getText());
        messageFx.setRegion(regionField.getText());
        messageFx.setValue(valueField.getText());
        messageFx.setKey(keyField.getText());

        try {
            if (messagesService.createMessage(messageFx) == null) {
                ViewHelper.errorDialog("Error", "Not saved unknown error");
                return;
            }
        } catch (Exception e) {
            ViewHelper.errorDialog("Unable to save Message error is:`{0}`", e.getMessage());
            log.warn("Message not saved", e);
            return;
        }

        close();
        if (onSaveRunnable != null) {
            onSaveRunnable.run();
        }
    }

    private boolean validate() {
        List<String> validationErrors = new ArrayList<>();

        if (languageField.getText().isEmpty()) {
            validationErrors.add("Language can not be empty");
        }
        if (languageField.getText().length() > 2) {
            validationErrors.add("Language can not have more than 2 characters");
        }
        if (regionField.getText().length() > 2) {
            validationErrors.add("Region can not have more than 2 characters");
        }
        if (valueField.getText().isEmpty()) {
            validationErrors.add("Value name must be set");
        }
        if (keyField.getText().isEmpty()) {
            validationErrors.add("Key can not be empty");
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
