package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.Faction;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.OffsetDateTime;
import java.util.List;

public class GamePlayerStatsFX extends AbstractEntityFX {
    private final StringProperty id;
    private final BooleanProperty ai;
    private final ObjectProperty<Faction> faction;
    private final ObjectProperty<Byte> color;
    private final ObjectProperty<Byte> team;
    private final ObjectProperty<Byte> startSpot;
    private final ObjectProperty<Byte> score;
    private final ObjectProperty<OffsetDateTime> scoreTime;
    private final ObjectProperty<GameFX> game;
    private final ObjectProperty<PlayerFX> player;
    private final ObjectProperty<Number> ratingChange;
    private final ObjectProperty<Integer> beforeRating;
    private final ObjectProperty<Integer> afterRating;
    private final ObservableList<LeaderboardRatingJournalFX> leaderboardRatingJournals;

    public GamePlayerStatsFX() {
        id = new SimpleStringProperty();
        ai = new SimpleBooleanProperty();
        faction = new SimpleObjectProperty<>();
        color = new SimpleObjectProperty<>();
        team = new SimpleObjectProperty<>();
        startSpot = new SimpleObjectProperty<>();
        score = new SimpleObjectProperty<>();
        scoreTime = new SimpleObjectProperty<>();
        game = new SimpleObjectProperty<>();
        player = new SimpleObjectProperty<>();
        leaderboardRatingJournals = FXCollections.observableArrayList();
        ratingChange = new SimpleObjectProperty<>();
        beforeRating = new SimpleObjectProperty<>();
        afterRating = new SimpleObjectProperty<>();
        leaderboardRatingJournals.addListener((InvalidationListener) observable -> leaderboardRatingJournals.stream().findFirst().ifPresent(ratingJournal -> {
            beforeRating.unbind();
            afterRating.unbind();
            ratingChange.unbind();
            beforeRating.bind(Bindings.createObjectBinding(() -> {
                Double beforeDeviation = ratingJournal.getDeviationBefore();
                Double beforeMean = ratingJournal.getMeanBefore();

                if (beforeDeviation == null || beforeMean == null) {
                    return null;
                }

                return (int) (beforeMean - 3 * beforeDeviation);
            }, ratingJournal.deviationBeforeProperty(), ratingJournal.meanBeforeProperty()));

            afterRating.bind(Bindings.createObjectBinding(() -> {
                Double afterDeviation = ratingJournal.getDeviationAfter();
                Double afterMean = ratingJournal.getMeanAfter();

                if (afterDeviation == null || afterMean == null) {
                    return null;
                }

                return (int) (afterMean - 3 * afterDeviation);
            }, ratingJournal.deviationAfterProperty(), ratingJournal.meanAfterProperty()));
            ratingChange.bind(Bindings.createObjectBinding(() -> {
                Integer after = afterRating.get();
                Integer before = beforeRating.get();

                if (after == null || before == null) {
                    return null;
                }

                return after - before;
            }, afterRating, beforeRating));
        }));
    }

    public int getBeforeRating() {
        return beforeRating.get();
    }

    public void setBeforeRating(int beforeRating) {
        this.beforeRating.set(beforeRating);
    }

    public ObjectProperty<Integer> beforeRatingProperty() {
        return beforeRating;
    }

    public int getAfterRating() {
        return afterRating.get();
    }

    public void setAfterRating(int afterRating) {
        this.afterRating.set(afterRating);
    }

    public ObjectProperty<Integer> afterRatingProperty() {
        return afterRating;
    }

    public Number getRatingChange() {
        return ratingChange.get();
    }

    public void setRatingChange(int ratingChange) {
        this.ratingChange.set(ratingChange);
    }

    public ObjectProperty<Number> ratingChangeProperty() {
        return ratingChange;
    }

    @Override
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    @Override
    public StringProperty idProperty() {
        return id;
    }

    public boolean isAi() {
        return ai.get();
    }

    public void setAi(boolean ai) {
        this.ai.set(ai);
    }

    public BooleanProperty aiProperty() {
        return ai;
    }

    public Faction getFaction() {
        return faction.get();
    }

    public void setFaction(Faction faction) {
        this.faction.set(faction);
    }

    public ObjectProperty<Faction> factionProperty() {
        return faction;
    }

    public Byte getColor() {
        return color.get();
    }

    public void setColor(Byte color) {
        this.color.set(color);
    }

    public ObjectProperty<Byte> colorProperty() {
        return color;
    }

    public Byte getTeam() {
        return team.get();
    }

    public void setTeam(Byte team) {
        this.team.set(team);
    }

    public ObjectProperty<Byte> teamProperty() {
        return team;
    }

    public Byte getStartSpot() {
        return startSpot.get();
    }

    public void setStartSpot(Byte startSpot) {
        this.startSpot.set(startSpot);
    }

    public ObjectProperty<Byte> startSpotProperty() {
        return startSpot;
    }

    public Byte getScore() {
        return score.get();
    }

    public void setScore(Byte score) {
        this.score.set(score);
    }

    public ObjectProperty<Byte> scoreProperty() {
        return score;
    }

    public OffsetDateTime getScoreTime() {
        return scoreTime.get();
    }

    public void setScoreTime(OffsetDateTime scoreTime) {
        this.scoreTime.set(scoreTime);
    }

    public ObjectProperty<OffsetDateTime> scoreTimeProperty() {
        return scoreTime;
    }

    public GameFX getGame() {
        return game.get();
    }

    public void setGame(GameFX game) {
        this.game.set(game);
    }

    public ObjectProperty<GameFX> gameProperty() {
        return game;
    }

    public PlayerFX getPlayer() {
        return player.get();
    }

    public void setPlayer(PlayerFX playerFX) {
        this.player.set(playerFX);
    }

    public ObjectProperty<PlayerFX> playerProperty() {
        return player;
    }

    public ObservableList<LeaderboardRatingJournalFX> getLeaderboardRatingJournals() {
        return leaderboardRatingJournals;
    }

    public void setLeaderboardRatingJournals(List<LeaderboardRatingJournalFX> ratingJournals) {
        if (ratingJournals != null) {
            this.leaderboardRatingJournals.setAll(ratingJournals);
        }
    }
}
