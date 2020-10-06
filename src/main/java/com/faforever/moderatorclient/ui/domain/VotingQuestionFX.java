package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VotingQuestionFX extends AbstractEntityFX {
    public static final String TYPE_NAME = "votingQuestion";
    private final IntegerProperty numberOfAnswers;
    private final StringProperty question;
    private final StringProperty description;
    private final StringProperty questionKey;
    private final StringProperty descriptionKey;
    private final IntegerProperty maxAnswers;
    private final IntegerProperty ordinal;
    private final BooleanProperty alternativeQuestion;
    private final ObjectProperty<VotingSubjectFX> votingSubject;
    private final ObservableList<VotingChoiceFX> winners;
    private final ObservableList<VotingChoiceFX> votingChoices;

    public VotingQuestionFX() {
        this.numberOfAnswers = new SimpleIntegerProperty();
        this.question = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.questionKey = new SimpleStringProperty();
        this.descriptionKey = new SimpleStringProperty();
        this.maxAnswers = new SimpleIntegerProperty();
        this.alternativeQuestion = new SimpleBooleanProperty();
        this.votingSubject = new SimpleObjectProperty<>();
        this.winners = FXCollections.observableArrayList();
        this.votingChoices = FXCollections.observableArrayList();
        this.ordinal = new SimpleIntegerProperty();
    }

    public static String getTypeName() {
        return TYPE_NAME;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers.get();
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers.set(numberOfAnswers);
    }

    public IntegerProperty numberOfAnswersProperty() {
        return numberOfAnswers;
    }

    public String getQuestion() {
        return question.get();
    }

    public void setQuestion(String question) {
        this.question.set(question);
    }

    public StringProperty questionProperty() {
        return question;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getQuestionKey() {
        return questionKey.get();
    }

    public void setQuestionKey(String questionKey) {
        this.questionKey.set(questionKey);
    }

    public StringProperty questionKeyProperty() {
        return questionKey;
    }

    public String getDescriptionKey() {
        return descriptionKey.get();
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey.set(descriptionKey);
    }

    public StringProperty descriptionKeyProperty() {
        return descriptionKey;
    }

    public int getMaxAnswers() {
        return maxAnswers.get();
    }

    public void setMaxAnswers(int maxAnswers) {
        this.maxAnswers.set(maxAnswers);
    }

    public IntegerProperty maxAnswersProperty() {
        return maxAnswers;
    }

    public boolean isAlternativeQuestion() {
        return alternativeQuestion.get();
    }

    public void setAlternativeQuestion(boolean alternativeQuestion) {
        this.alternativeQuestion.set(alternativeQuestion);
    }

    public BooleanProperty alternativeQuestionProperty() {
        return alternativeQuestion;
    }

    public VotingSubjectFX getVotingSubject() {
        return votingSubject.get();
    }

    public void setVotingSubject(VotingSubjectFX votingSubject) {
        this.votingSubject.set(votingSubject);
    }

    public ObjectProperty<VotingSubjectFX> votingSubjectProperty() {
        return votingSubject;
    }

    public ObservableList<VotingChoiceFX> getWinners() {
        return winners;
    }

    public ObservableList<VotingChoiceFX> getVotingChoices() {
        return votingChoices;
    }

    public int getOrdinal() {
        return ordinal.get();
    }

    public void setOrdinal(int ordinal) {
        this.ordinal.set(ordinal);
    }

    public IntegerProperty ordinalProperty() {
        return ordinal;
    }

    @Override
    public String toString() {
        return String.format("%s (id=%s)", getQuestion(), getId());
    }

}
