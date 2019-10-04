package com.faforever.moderatorclient.ui.voting;

import com.faforever.commons.api.dto.VotingQuestion;
import com.faforever.commons.api.dto.VotingSubject;
import com.faforever.moderatorclient.api.domain.VotingService;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import javafx.fxml.FXML;
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class VotingQuestionAddController implements Controller<Pane> {
    private final VotingService votingService;

    public GridPane root;
    public TextField questionMessageKeyTextField;
    public TextField descriptionKeyTextFiled;
    public TextField maxAnswersTextField;
    public TextField votingSubjectIdTextField;
    public CheckBox alternativeQuestionCheckBox;
    public TextField votingSubjectOrdinal;

    private Runnable onSaveRunnable;

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
    }

    public void setVotingSubjectId(String id) {
        votingSubjectIdTextField.setText(id);
    }

    public void onSave() {
        if (!validate()) {
            return;
        }
        VotingQuestion votingQuestion = new VotingQuestion();
        votingQuestion.setQuestionKey(questionMessageKeyTextField.getText());
        votingQuestion.setDescriptionKey(descriptionKeyTextFiled.getText());
        if (!maxAnswersTextField.getText().isEmpty()) {
            votingQuestion.setMaxAnswers(Integer.parseInt(maxAnswersTextField.getText()));
        }
        votingQuestion.setAlternativeQuestion(alternativeQuestionCheckBox.isSelected());
        votingQuestion.setOrdinal(Integer.valueOf(votingSubjectOrdinal.getText()));

        VotingSubject votingSubject = new VotingSubject();
        votingSubject.setId(votingSubjectIdTextField.getText());
        votingQuestion.setVotingSubject(votingSubject);

        try {
            if (votingService.create(votingQuestion) == null) {
                ViewHelper.errorDialog("Saving failed", "Not saved due to unknown error");
                return;
            }
        } catch (Exception e) {
            ViewHelper.errorDialog("Saving failed", MessageFormat.format("Unable to save question error is:`{0}`", e.getMessage()));
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

        if (questionMessageKeyTextField.getText().isEmpty()) {
            validationErrors.add("Question key can not be empty");
        }
        if (!maxAnswersTextField.getText().isEmpty()) {
            try {
                Integer.parseInt(maxAnswersTextField.getText());
            } catch (Exception e) {
                validationErrors.add("Invalid max answers");
            }
        }
        if (votingSubjectOrdinal.getText().isEmpty()) {
            validationErrors.add("Ordinal must be set");
        } else {
            try {
                Integer.parseInt(votingSubjectOrdinal.getText());
            } catch (Exception e) {
                validationErrors.add("Ordinal must be valid number");
            }
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


