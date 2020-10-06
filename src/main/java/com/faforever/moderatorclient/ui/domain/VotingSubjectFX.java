package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.OffsetDateTime;

public class VotingSubjectFX extends AbstractEntityFX {
    private final StringProperty subjectKey;
    private final StringProperty subject;
    private final IntegerProperty numberOfVotes;
    private final StringProperty topicUrl;
    private final ObjectProperty<OffsetDateTime> beginOfVoteTime;
    private final ObjectProperty<OffsetDateTime> endOfVoteTime;
    private final IntegerProperty minGamesToVote;
    private final StringProperty descriptionKey;
    private final StringProperty description;
    private final BooleanProperty revealWinner;
    private final ObservableList<VotingQuestionFX> votingQuestions;

    public VotingSubjectFX() {
        this.descriptionKey = new SimpleStringProperty();
        this.subjectKey = new SimpleStringProperty();
        this.subject = new SimpleStringProperty();
        this.numberOfVotes = new SimpleIntegerProperty();
        this.topicUrl = new SimpleStringProperty();
        this.beginOfVoteTime = new SimpleObjectProperty<>();
        this.endOfVoteTime = new SimpleObjectProperty<>();
        this.minGamesToVote = new SimpleIntegerProperty();
        this.description = new SimpleStringProperty();
        this.votingQuestions = FXCollections.observableArrayList();
        revealWinner = new SimpleBooleanProperty(false);
    }

    public boolean getRevealWinner() {
        return revealWinner.get();
    }

    public void setRevealWinner(boolean revealWinner) {
        this.revealWinner.set(revealWinner);
    }

    public BooleanProperty revealWinnerProperty() {
        return revealWinner;
    }

    public String getSubjectKey() {
        return subjectKey.get();
    }

    public void setSubjectKey(String subjectKey) {
        this.subjectKey.set(subjectKey);
    }

    public StringProperty subjectKeyProperty() {
        return subjectKey;
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public int getNumberOfVotes() {
        return numberOfVotes.get();
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes.set(numberOfVotes);
    }

    public IntegerProperty numberOfVotesProperty() {
        return numberOfVotes;
    }

    public String getTopicUrl() {
        return topicUrl.get();
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl.set(topicUrl);
    }

    public StringProperty topicUrlProperty() {
        return topicUrl;
    }

    public OffsetDateTime getBeginOfVoteTime() {
        return beginOfVoteTime.get();
    }

    public void setBeginOfVoteTime(OffsetDateTime beginOfVoteTime) {
        this.beginOfVoteTime.set(beginOfVoteTime);
    }

    public ObjectProperty<OffsetDateTime> beginOfVoteTimeProperty() {
        return beginOfVoteTime;
    }

    public OffsetDateTime getEndOfVoteTime() {
        return endOfVoteTime.get();
    }

    public void setEndOfVoteTime(OffsetDateTime endOfVoteTime) {
        this.endOfVoteTime.set(endOfVoteTime);
    }

    public ObjectProperty<OffsetDateTime> endOfVoteTimeProperty() {
        return endOfVoteTime;
    }

    public int getMinGamesToVote() {
        return minGamesToVote.get();
    }

    public void setMinGamesToVote(int minGamesToVote) {
        this.minGamesToVote.set(minGamesToVote);
    }

    public IntegerProperty minGamesToVoteProperty() {
        return minGamesToVote;
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

    public ObservableList<VotingQuestionFX> getVotingQuestions() {
        return votingQuestions;
    }

    @Override
    public String toString() {
        return String.format("%s (id=%s)", getSubject(), getId());
    }

}
