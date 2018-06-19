package com.faforever.moderatorclient.ui;


import com.faforever.commons.api.dto.VotingChoice;
import com.faforever.commons.api.dto.VotingQuestion;
import com.faforever.moderatorclient.api.domain.VotingService;
import javafx.fxml.FXML;
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
public class VotingChoiceAddController implements Controller<Pane> {
    private final VotingService votingService;
    public GridPane root;
    public Label errorLabel;
    public TextField choiceKeyTextField;
    public TextField descriptionKeyTextFiled;
    public TextField ordinalTextField;
    public TextField questionTextField;


    private Runnable onSaveRunnable;

    @Inject
    public VotingChoiceAddController(VotingService votingService) {
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
        VotingChoice votingChoice = new VotingChoice();
        votingChoice.setChoiceTextKey(choiceKeyTextField.getText());
        votingChoice.setDescriptionKey(descriptionKeyTextFiled.getText());
        votingChoice.setOrdinal(Integer.parseInt(ordinalTextField.getText()));

        VotingQuestion votingQuestion = new VotingQuestion();
        votingQuestion.setId(questionTextField.getText());
        votingChoice.setVotingQuestion(votingQuestion);

        try {
            if (votingService.create(votingChoice) == null) {
                error("Not saved due to unknown error");
                return;
            }
        } catch (Exception e) {
            error(MessageFormat.format("Unable to save choice error is:`{0}`", e.getMessage()));
            log.warn("Question not saved", e);
            return;
        }

        close();
        if (onSaveRunnable != null) {
            onSaveRunnable.run();
        }
    }

    private boolean validate() {
        if (choiceKeyTextField.getText().isEmpty()) {
            return error("Choice Text key can not be empty");
        }
        try {
            Integer.parseInt(ordinalTextField.getText());
        } catch (Exception e) {
            return error("Invalid ordinal");
        }

        try {
            Integer.parseInt(questionTextField.getText());
        } catch (Exception e) {
            return error("Invalid voting question ID");
        }
        return true;
    }

    private boolean error(String message) {
        errorLabel.setVisible(true);
        errorLabel.setText(message);
        log.info("Could not save VotingChoice error: {}", message);
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
