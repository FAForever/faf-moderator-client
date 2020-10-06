package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.*;

public class VotingChoiceFX extends AbstractEntityFX {

    private final StringProperty choiceTextKey;
    private final StringProperty choiceText;
    private final StringProperty descriptionKey;
    private final StringProperty description;
    private final IntegerProperty numberOfAnswers;
    private final IntegerProperty ordinal;
    private final ObjectProperty<VotingQuestionFX> votingQuestion;

    public VotingChoiceFX() {
        this.choiceTextKey = new SimpleStringProperty();
        this.choiceText = new SimpleStringProperty();
        this.descriptionKey = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.numberOfAnswers = new SimpleIntegerProperty();
        this.ordinal = new SimpleIntegerProperty();
        this.votingQuestion = new SimpleObjectProperty<>();
    }

    public String getChoiceTextKey() {
        return choiceTextKey.get();
    }

    public void setChoiceTextKey(String choiceTextKey) {
        this.choiceTextKey.set(choiceTextKey);
    }

    public StringProperty choiceTextKeyProperty() {
        return choiceTextKey;
    }

    public String getChoiceText() {
        return choiceText.get();
    }

    public void setChoiceText(String choiceText) {
        this.choiceText.set(choiceText);
    }

    public StringProperty choiceTextProperty() {
        return choiceText;
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

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
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

    public int getOrdinal() {
        return ordinal.get();
    }

    public void setOrdinal(int ordinal) {
        this.ordinal.set(ordinal);
    }

    public IntegerProperty ordinalProperty() {
        return ordinal;
    }

    public VotingQuestionFX getVotingQuestion() {
        return votingQuestion.get();
    }

    public void setVotingQuestion(VotingQuestionFX votingQuestion) {
        this.votingQuestion.set(votingQuestion);
    }

    public ObjectProperty<VotingQuestionFX> votingQuestionProperty() {
        return votingQuestion;
    }

    @Override
    public String toString() {
        return String.format("%s (id=%s)", getChoiceText(), getId());
    }
}
