package com.faforever.moderatorclient.ui.voting;


import com.faforever.moderatorclient.api.domain.VotingService;
import com.faforever.moderatorclient.ui.domain.VotingSubjectFX;
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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class VotingSubjectAddController implements Controller<Pane> {
    private final VotingService votingService;

    public GridPane root;
    public TextField subjectKeyTextField;
    public TextField descriptionKeyTextFiled;
    public TextField topicUrlTextField;
    public TextField minGamesTextField;
    public TextField beginTimeTextField;
    public TextField endTimeTextField;

    private Runnable onSaveRunnable;

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        beginTimeTextField.setPromptText(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        endTimeTextField.setPromptText(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

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
                ViewHelper.errorDialog("Saving failed", "Not saved due to unknown error");
                return;
            }
        } catch (Exception e) {
            ViewHelper.errorDialog("Saving failed", MessageFormat.format("Unable to save Subject error is:`{0}`", e.getMessage()));
            log.warn("Subject not saved", e);
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

        if (subjectKeyTextField.getText().isEmpty()) {
            validationErrors.add("Subject can not be empty");
        }
        if (topicUrlTextField.getText().isEmpty()) {
            validationErrors.add("Topic url name must be set");
        }
        try {
            Integer.parseInt(minGamesTextField.getText());
        } catch (Exception e) {
            validationErrors.add("Min games to vote invalid");
        }
        try {
            OffsetDateTime.of(LocalDateTime.parse(endTimeTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
        } catch (Exception e) {
            validationErrors.add("Invalid End Time (valid example: 2011-12-30T10:15:30)");
        }
        try {
            OffsetDateTime.of(LocalDateTime.parse(beginTimeTextField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
        } catch (Exception e) {
            validationErrors.add("Invalid begin time (valid example: 2011-12-30T10:15:30)");
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

