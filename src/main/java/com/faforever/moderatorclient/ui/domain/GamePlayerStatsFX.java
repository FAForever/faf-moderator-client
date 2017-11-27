package com.faforever.moderatorclient.ui.domain;

import com.faforever.moderatorclient.api.dto.Faction;
import com.faforever.moderatorclient.api.dto.Player;
import javafx.beans.property.*;

import java.time.OffsetDateTime;

public class GamePlayerStatsFX extends AbstractEntityFX {
    private final StringProperty id;
    private final BooleanProperty ai;
    private final ObjectProperty<Faction> faction;
    private final ObjectProperty<Byte> color;
    private final ObjectProperty<Byte> team;
    private final ObjectProperty<Byte> startSpot;
    private final FloatProperty beforeMean;
    private final FloatProperty beforeDeviation;
    private final FloatProperty afterMean;
    private final FloatProperty afterDeviation;
    private final ObjectProperty<Byte> score;
    private final ObjectProperty<OffsetDateTime> scoreTime;
    private final ObjectProperty<GameFX> game;
    private final ObjectProperty<Player> player;
    private final IntegerProperty ratingChange;
    private final IntegerProperty beforeRating;
    private final IntegerProperty afterRating;


    public GamePlayerStatsFX() {
        id = new SimpleStringProperty();
        ai = new SimpleBooleanProperty();
        faction = new SimpleObjectProperty<>();
        color = new SimpleObjectProperty<>();
        team = new SimpleObjectProperty<>();
        startSpot = new SimpleObjectProperty<>();
        beforeMean = new SimpleFloatProperty();
        beforeDeviation = new SimpleFloatProperty();
        afterMean = new SimpleFloatProperty();
        afterDeviation = new SimpleFloatProperty();
        score = new SimpleObjectProperty<>();
        scoreTime = new SimpleObjectProperty<>();
        game = new SimpleObjectProperty<>();
        player = new SimpleObjectProperty<>();
        ratingChange = new SimpleIntegerProperty();
        beforeRating = new SimpleIntegerProperty();
        afterRating = new SimpleIntegerProperty();
        beforeRating.bind(beforeMean.subtract(beforeDeviation.multiply(3)));
        afterRating.bind(afterMean.subtract(afterDeviation.multiply(3)));
        ratingChange.bind(afterRating.subtract(beforeRating));
    }

    public int getBeforeRating() {
        return beforeRating.get();
    }

    public void setBeforeRating(int beforeRating) {
        this.beforeRating.set(beforeRating);
    }

    public IntegerProperty beforeRatingProperty() {
        return beforeRating;
    }

    public int getAfterRating() {
        return afterRating.get();
    }

    public void setAfterRating(int afterRating) {
        this.afterRating.set(afterRating);
    }

    public IntegerProperty afterRatingProperty() {
        return afterRating;
    }

    public int getRatingChange() {
        return ratingChange.get();
    }

    public void setRatingChange(int ratingChange) {
        this.ratingChange.set(ratingChange);
    }

    public IntegerProperty ratingChangeProperty() {
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

    public float getBeforeMean() {
        return beforeMean.get();
    }

    public void setBeforeMean(float beforeMean) {
        this.beforeMean.set(beforeMean);
    }

    public FloatProperty beforeMeanProperty() {
        return beforeMean;
    }

    public float getBeforeDeviation() {
        return beforeDeviation.get();
    }

    public void setBeforeDeviation(float beforeDeviation) {
        this.beforeDeviation.set(beforeDeviation);
    }

    public FloatProperty beforeDeviationProperty() {
        return beforeDeviation;
    }

    public float getAfterMean() {
        return afterMean.get();
    }

    public void setAfterMean(float afterMean) {
        this.afterMean.set(afterMean);
    }

    public FloatProperty afterMeanProperty() {
        return afterMean;
    }

    public float getAfterDeviation() {
        return afterDeviation.get();
    }

    public void setAfterDeviation(float afterDeviation) {
        this.afterDeviation.set(afterDeviation);
    }

    public FloatProperty afterDeviationProperty() {
        return afterDeviation;
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

    public Player getPlayer() {
        return player.get();
    }

    public void setPlayer(Player player) {
        this.player.set(player);
    }

    public ObjectProperty<Player> playerProperty() {
        return player;
    }
}
