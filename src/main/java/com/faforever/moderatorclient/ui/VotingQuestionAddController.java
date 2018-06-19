package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.VotingQuestion;
import com.faforever.commons.api.dto.VotingSubject;
import com.faforever.moderatorclient.api.domain.VotingService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.MessageFormat;

@Component
@Scope("prototype")
@Slf4j
public class VotingQuestionAddController implements Controller<Pane> {
    private final VotingService votingService;
    public GridPane root;
    public Label errorLabel;
    public TextField questionMessageKeyTextField;
    public TextField descriptionKeyTextFiled;
    public TextField maxAnswersTextField;
    public TextField votingSubjectIdTextField;
    public CheckBox alternativeQuestionCheckBox;

    private Runnable onSaveRunnable;

    @Inject
    public VotingQuestionAddController(VotingService votingService) {
        this.votingService = votingService;
    }

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
        errorLabel.setVisible(false);
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

        VotingSubject votingSubject = new VotingSubject();
        votingSubject.setId(votingSubjectIdTextField.getText());
        votingQuestion.setVotingSubject(votingSubject);

        try {
            if (votingService.create(votingQuestion) == null) {
                error("Not saved due to unknown error");
                return;
            }
        } catch (Exception e) {
            error(MessageFormat.format("Unable to save question error is:`{0}`", e.getMessage()));
            log.warn("Question not saved", e);
            return;
        }

        close();
        if (onSaveRunnable != null) {
            onSaveRunnable.run();
        }
    }

    private boolean validate() {
        if (questionMessageKeyTextField.getText().isEmpty()) {
            return error("Question key can not be empty");
        }
        if (!maxAnswersTextField.getText().isEmpty()) {
            try {
                Integer.parseInt(maxAnswersTextField.getText());
            } catch (Exception e) {
                return error("Invalid max answers");
            }
        }
        try {
            Integer.parseInt(votingSubjectIdTextField.getText());
        } catch (Exception e) {
            return error("Invalid voting subject ID");
        }
        return true;
    }

    private boolean error(String message) {
        errorLabel.setVisible(true);
        errorLabel.setText(message);
        log.info("Could not save VotingQuestion error: {}", message);
        return false;
    }

    private void close() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void setOnSave(Runnable onSaveRunnable) {
        this.onSaveRunnable = onSaveRunnable;
    }
}


