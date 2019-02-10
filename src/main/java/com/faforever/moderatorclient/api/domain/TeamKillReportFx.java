package com.faforever.moderatorclient.api.domain;

import com.faforever.moderatorclient.ui.domain.AbstractEntityFX;
import com.faforever.moderatorclient.ui.domain.GameFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.OffsetDateTime;

public class TeamKillReportFx extends AbstractEntityFX {

	private final IntegerProperty gameTime;
	private final ObjectProperty<OffsetDateTime> reportedAt;
	private final ObjectProperty<PlayerFX> teamkiller;
	private final ObjectProperty<PlayerFX> victim;
	private final ObjectProperty<GameFX> game;

	public TeamKillReportFx() {
		this.gameTime = new SimpleIntegerProperty();
		this.reportedAt = new SimpleObjectProperty<>();
		this.teamkiller = new SimpleObjectProperty<>();
		this.victim = new SimpleObjectProperty<>();
		this.game = new SimpleObjectProperty<>();
	}

	public int getGameTime() {
		return gameTime.get();
	}

	public void setGameTime(int gameTime) {
		this.gameTime.set(gameTime);
	}

	public IntegerProperty gameTimeProperty() {
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
}
