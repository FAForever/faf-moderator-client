package com.faforever.moderatorclient.ui.voting;


import com.faforever.commons.api.dto.VotingChoice;
import com.faforever.commons.api.dto.VotingQuestion;
import com.faforever.moderatorclient.api.domain.VotingService;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class VotingChoiceAddController implements Controller<Pane> {
    private final VotingService votingService;

    public GridPane root;
    public TextField choiceKeyTextField;
    public TextField descriptionKeyTextFiled;
    public TextField ordinalTextField;
    public TextField questionIdField;

    private Runnable onSaveRunnable;

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
    }

    public void onSave() {
        if (!validate()) {
            return;
        }
        VotingChoice votingChoice = new VotingChoice();
        votingChoice.setChoiceTextKey(choiceKeyTextField.getText());
        votingChoice.setDescriptionKey(descriptionKeyTextFiled.getText());
        votingChoice.setOrdinal(Integer.parseInt(ordinalTextField.getText()));

        VotingQuestion votingQuestion = new VotingQuestion();
        votingQuestion.setId(questionIdField.getText());
        votingChoice.setVotingQuestion(votingQuestion);

        try {
            if (votingService.create(votingChoice) == null) {
                ViewHelper.errorDialog("Saving failed", "Could not saved due to unknown error");
                return;
            }
        } catch (Exception e) {
            ViewHelper.errorDialog("Saving failed", MessageFormat.format("Unable to save choice showError is:`{0}`", e.getMessage()));
            log.warn("Question not saved", e);
            return;
        }

        close();
        if (onSaveRunnable != null) {
            onSaveRunnable.run();
        }
    }

    public void onAbort() {
        close();
    }

    private boolean validate() {
        List<String> validationErrors = new ArrayList<>();

        if (choiceKeyTextField.getText().isEmpty()) {
            validationErrors.add("Choice Text key can not be empty");
        }

        try {
            Integer.parseInt(ordinalTextField.getText());
        } catch (Exception e) {
            validationErrors.add("Invalid ordinal");
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

    public void setVotingQuestionId(String id) {
        questionIdField.setText(id);
    }
}
