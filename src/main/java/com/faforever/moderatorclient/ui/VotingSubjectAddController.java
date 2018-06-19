package com.faforever.moderatorclient.ui;


import com.faforever.moderatorclient.api.domain.VotingService;
import com.faforever.moderatorclient.mapstruct.VotingSubjectFX;
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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@Scope("prototype")
@Slf4j
public class VotingSubjectAddController implements Controller<Pane> {
    private final VotingService votingService;
    public GridPane root;
    public Label errorLabel;
    public TextField subjectKeyTextField;
    public TextField descriptionKeyTextFiled;
    public TextField topicUrlTextField;
    public TextField minGamesTextField;
    public TextField beginTimeTextField;
    public TextField endTimeTextField;

    private Runnable onSaveRunnable;

    @Inject
    public VotingSubjectAddController(VotingService votingService) {
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
        beginTimeTextField.setPromptText(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()));
        endTimeTextField.setPromptText(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()));

    }

    public void onSave() {
        if (!validate()) {
            return;
        }
        VotingSubjectFX votingSubjectFX = new VotingSubjectFX();
        votingSubjectFX.setSubjectKey(subjectKeyTextField.getText());
        votingSubjectFX.setDescriptionKey(descriptionKeyTextFiled.getText());
        votingSubjectFX.setMinGamesToVote(Integer.parseInt(minGamesTextField.getText()));
        votingSubjectFX.setTopicUrl(topicUrlTextField.getText());
        if (!beginTimeTextField.getText().isEmpty()) {
            OffsetDateTime beginTime = OffsetDateTime.of(LocalDateTime.parse(beginTimeTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
            votingSubjectFX.setBeginOfVoteTime(beginTime);
        }

        OffsetDateTime endTime = OffsetDateTime.of(LocalDateTime.parse(endTimeTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
        votingSubjectFX.setEndOfVoteTime(endTime);

        try {
            if (votingService.create(votingSubjectFX) == null) {
                error("Not saved due to unknown error");
                return;
            }
        } catch (Exception e) {
            error(MessageFormat.format("Unable to save Subject error is:`{0}`", e.getMessage()));
            log.warn("Subject not saved", e);
            return;
        }

        close();
        if (onSaveRunnable != null) {
            onSaveRunnable.run();
        }
    }

    private boolean validate() {
        if (subjectKeyTextField.getText().isEmpty()) {
            return error("Subject can not be empty");
        }
        if (topicUrlTextField.getText().isEmpty()) {
            return error("Topic url name must be set");
        }
        try {
            Integer.parseInt(minGamesTextField.getText());
        } catch (Exception e) {
            return error("Min games to vote invalid");
        }
        try {
            OffsetDateTime.of(LocalDateTime.parse(endTimeTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
        } catch (Exception e) {
            return error("Invalid End Time (valid example: 2011-12-30T10:15:30)");
        }
        try {
            OffsetDateTime.of(LocalDateTime.parse(beginTimeTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
        } catch (Exception e) {
            return error("Invalid begin time (valid example: 2011-12-30T10:15:30)");
        }
        return true;
    }

    private boolean error(String message) {
        errorLabel.setVisible(true);
        errorLabel.setText(message);
        log.info("Could not save VotingSubject error: {}", message);
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

