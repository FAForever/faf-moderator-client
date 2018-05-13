package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.*;

import java.time.OffsetDateTime;

public class TeamkillFX {
    private final StringProperty id;
    private final ObjectProperty<PlayerFX> teamkiller;
    private final ObjectProperty<PlayerFX> victim;
    private final ObjectProperty<GameFX> game;
    private final LongProperty gameTime;
    private final ObjectProperty<OffsetDateTime> reportedAt;

    public TeamkillFX() {
        id = new SimpleStringProperty();
        teamkiller = new SimpleObjectProperty<>();
        victim = new SimpleObjectProperty<>();
        game = new SimpleObjectProperty<>();
        gameTime = new SimpleLongProperty();
        reportedAt = new SimpleObjectProperty<>();
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public PlayerFX getTeamkiller() {
        return teamkiller.get();
    }

    public void setTeamkiller(PlayerFX teamkiller) {
        this.teamkiller.set(teamkiller);
    }

    public ObjectProperty<PlayerFX> teamkillerProperty() {
        return teamkiller;
    }

    public PlayerFX getVictim() {
        return victim.get();
    }

    public void setVictim(PlayerFX victim) {
        this.victim.set(victim);
    }

    public ObjectProperty<PlayerFX> victimProperty() {
        return victim;
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

    public long getGameTime() {
        return gameTime.get();
    }

    public void setGameTime(long gameTime) {
        this.gameTime.set(gameTime);
    }

    public LongProperty gameTimeProperty() {
        return gameTime;
    }

    public OffsetDateTime getReportedAt() {
        return reportedAt.get();
    }

    public void setReportedAt(OffsetDateTime reportedAt) {
        this.reportedAt.set(reportedAt);
    }

    public ObjectProperty<OffsetDateTime> reportedAtProperty() {
        return reportedAt;
    }
}
