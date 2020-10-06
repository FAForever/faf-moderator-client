package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.Map;
import com.faforever.commons.api.dto.MapVersion;
import com.faforever.commons.api.dto.Message;
import com.faforever.moderatorclient.api.domain.MessagesService;
import com.faforever.moderatorclient.api.domain.VotingService;
import com.faforever.moderatorclient.ui.domain.VotingChoiceFX;
import com.faforever.moderatorclient.ui.domain.VotingQuestionFX;
import com.faforever.moderatorclient.ui.domain.VotingSubjectFX;
import com.faforever.moderatorclient.ui.domain.MessageFx;
import com.faforever.moderatorclient.ui.events.VotingRefreshEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LadderMapVoteGenerationFormController implements Controller<Node> {
    private final VotingService votingService;
    private final MessagesService messagesService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public VBox root;
    public TextField subjectTextKey;
    public TextField subjectForumThread;
    public TextField subjectDescriptionKey;
    public TextField questionText;
    public TextField startDate;
    public TextField endDate;
    public TextField numberOfChoices;
    public TextField choiceTextPattern;
    public TextField choiceDescriptionPattern;
    private Set<Map> givenMaps;

    @FXML
    public void initialize() {
        startDate.setPromptText(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()));
        endDate.setPromptText(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()));
        startDate.textProperty().addListener(observable -> onDurationTextChange(startDate));
        endDate.textProperty().addListener(observable -> onDurationTextChange(endDate));
    }

    public void setGivenMaps(Set<Map> givenMaps) {
        this.givenMaps = givenMaps;
    }

    private void onDurationTextChange(TextField textField) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(textField.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            textField.setStyle("-fx-text-fill: green");
        } catch (DateTimeParseException e) {
            textField.setStyle("-fx-text-fill: red");
        }
    }

    @Override
    public Parent getRoot() {
        return root;
    }

    public void close() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void submit() {
        try {
            if (!validate() || givenMaps == null) {
                return;
            }
            int subjectId = generateVotingSubject();
            List<Integer> questionIDs = new ArrayList<>(givenMaps.size());
            givenMaps.stream()
                    .map(Map::getVersions)
                    .flatMap(Collection::stream)
                    .forEach(mapVersion -> questionIDs.add(generateQuestion(mapVersion, subjectId)));
            questionIDs.forEach(questionId -> getDefaultChoices(questionId, Integer.parseInt(numberOfChoices.getText())));
            close();
        } catch (Exception e) {
            ViewHelper.errorDialog("Could not create ladder map vote.", "Create of ladder map vote failed, please delete eventually created Voting Subject.");
            log.error("Ladder map vote failed to be create", e);
            return;
        }
        ViewHelper.confirmDialog("Created vote", "Success!");
        applicationEventPublisher.publishEvent(new VotingRefreshEvent());
    }

    private int generateVotingSubject() {
        VotingSubjectFX votingSubjectFX = new VotingSubjectFX();
        votingSubjectFX.setTopicUrl(subjectForumThread.getText());
        votingSubjectFX.setSubjectKey(subjectTextKey.getText());
        votingSubjectFX.setDescriptionKey(subjectDescriptionKey.getText());
        votingSubjectFX.setMinGamesToVote(10);
        votingSubjectFX.setEndOfVoteTime(OffsetDateTime.of(LocalDateTime.parse(endDate.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC));
        votingSubjectFX.setBeginOfVoteTime(OffsetDateTime.of(LocalDateTime.parse(startDate.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC));
        return Integer.parseInt(votingService.create(votingSubjectFX).getId());
    }

    private int generateQuestion(MapVersion mapVersion, int subjectID) {
        VotingQuestionFX votingQuestionFX = new VotingQuestionFX();

        String questionContent = String.format(questionText.getText(), mapVersion.getMap().getDisplayName());
        MessageFx messageFx = new MessageFx();
        messageFx.setKey(String.format("ladder.vote.map.%s.question", mapVersion.getId()));
        messageFx.setLanguage("en");
        messageFx.setRegion("US");
        messageFx.setValue(questionContent);
        Message message = messagesService.putMessage(messageFx);
        votingQuestionFX.setQuestionKey(message.getKey());

        votingQuestionFX.setAlternativeQuestion(false);
        votingQuestionFX.setNumberOfAnswers(1);
        votingQuestionFX.setMaxAnswers(1);

        String descriptionContent = generateMapDescriptionHTML(mapVersion);
        MessageFx messageFxDescription = new MessageFx();
        messageFxDescription.setKey(String.format("ladder.vote.map.%s.description", mapVersion.getId()));
        messageFxDescription.setLanguage("en");
        messageFxDescription.setRegion("US");
        messageFxDescription.setValue(descriptionContent);
        Message messageDescription = messagesService.putMessage(messageFxDescription);
        votingQuestionFX.setDescriptionKey(messageDescription.getKey());

        VotingSubjectFX votingSubject = new VotingSubjectFX();
        votingSubject.setId(String.valueOf(subjectID));
        votingQuestionFX.setVotingSubject(votingSubject);
        return Integer.parseInt(votingService.create(votingQuestionFX).getId());
    }

    private void getDefaultChoices(int questionID, int count) {
        VotingQuestionFX votingQuestion = new VotingQuestionFX();
        votingQuestion.setId(String.valueOf(questionID));
        List<VotingChoiceFX> votingChoiceFXES = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            VotingChoiceFX votingChoiceFX = new VotingChoiceFX();
            votingChoiceFX.setOrdinal(i);
            votingChoiceFX.setChoiceTextKey(String.format(choiceTextPattern.getText(), i));
            votingChoiceFX.setDescriptionKey(String.format(choiceDescriptionPattern.getText(), i));
            votingChoiceFX.setVotingQuestion(votingQuestion);
            votingChoiceFXES.add(votingChoiceFX);
        }
        votingChoiceFXES.forEach(votingService::create);
    }

    private String generateMapDescriptionHTML(MapVersion mapVersion) {
        String formattedHtml;
        try {
            InputStream resourceAsStream = getClass().getResourceAsStream("/media/map_description_template.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
            String htmlTemplate = reader.lines().collect(Collectors.joining("\n"));

            formattedHtml = htmlTemplate.replaceAll("\\{map-player-count}", String.valueOf(mapVersion.getMaxPlayers()));
            formattedHtml = formattedHtml.replaceAll("\\{preview-source}", mapVersion.getThumbnailUrlLarge().toString());
            formattedHtml = formattedHtml.replaceAll("\\{map-name}", mapVersion.getMap().getDisplayName());
        } catch (Exception e) {
            log.error("Error getting template resource", e);
            ViewHelper.errorDialog("Error getting template resource", "Error getting template resource - contact a developer - Please delete the created Voting Subject and all it contains.");
            throw new RuntimeException(e);
        }
        return formattedHtml;
    }

    private boolean validate() {
        List<String> validationErrors = new ArrayList<>();

        if (choiceTextPattern.getText().isEmpty()) {
            validationErrors.add("Choice Text key pattern can not be empty");
        }
        if (choiceDescriptionPattern.getText().isEmpty()) {
            validationErrors.add("Choice Description key pattern can not be empty");
        }
        if (!choiceTextPattern.getText().contains("%d")) {
            validationErrors.add("Choice Text key pattern needs to have an %d as place holder for question ordinal");
        }
        if (!choiceTextPattern.getText().contains("%d")) {
            validationErrors.add("Choice Description key pattern needs to have an %d as place holder for question ordinal");
        }
        if (subjectTextKey.getText().isEmpty()) {
            validationErrors.add("Subject text key can not be empty");
        }
        if (subjectForumThread.getText().isEmpty()) {
            validationErrors.add("Subject forum thread can not be empty");
        }
        try {
            if (Integer.parseInt(numberOfChoices.getText()) < 2) {
                validationErrors.add("Number of choices needs to be grater equal 2");
            }
        } catch (Exception e) {
            validationErrors.add("Invalid number of choices, needs to be a number");
        }

        try {
            OffsetDateTime.of(LocalDateTime.parse(startDate.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
        } catch (Exception e) {
            validationErrors.add("Invalid start time (valid example: 2011-12-30T10:15:30)");
        }
        try {
            OffsetDateTime.of(LocalDateTime.parse(endDate.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
        } catch (Exception e) {
            validationErrors.add("Invalid end time (valid example: 2011-12-30T10:15:30)");
        }

        if (!questionText.getText().contains("%s")) {
            validationErrors.add("Please add a %s as substitute for the map name");
        }

        if (validationErrors.size() > 0) {
            ViewHelper.errorDialog("Validation failed",
                    String.join("\n", validationErrors));
            return false;
        }

        return true;
    }

    public void setDefault() {
        subjectTextKey.setText("ladder.vote.subject");
        subjectDescriptionKey.setText("ladder.vote.description");
        questionText.setText("How did you like map \"%s\" ?");
        choiceTextPattern.setText("ladder.vote.answer.%d");
        choiceDescriptionPattern.setText("ladder.vote.answer.description.%d");
        startDate.setText(OffsetDateTime.now().plusHours(1).atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        endDate.setText(OffsetDateTime.now().plusDays(3).atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        numberOfChoices.setText("5");
    }
}
