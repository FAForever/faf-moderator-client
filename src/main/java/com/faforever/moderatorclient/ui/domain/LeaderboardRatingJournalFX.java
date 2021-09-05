package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a leaderboard rating
 */
public class LeaderboardRatingJournalFX extends AbstractEntityFX {

  private final ObjectProperty<Double> meanAfter = new SimpleObjectProperty<>();
  private final ObjectProperty<Double> deviationAfter = new SimpleObjectProperty<>();
  private final ObjectProperty<Double> meanBefore = new SimpleObjectProperty<>();
  private final ObjectProperty<Double> deviationBefore = new SimpleObjectProperty<>();
  private final ObjectProperty<GamePlayerStatsFX> gamePlayerStats = new SimpleObjectProperty<>();
  private final ObjectProperty<LeaderboardFX> leaderboard = new SimpleObjectProperty<>();

  public Double getMeanAfter() {
    return meanAfter.get();
  }

  public ObjectProperty<Double> meanAfterProperty() {
    return meanAfter;
  }

  public void setMeanAfter(Double meanAfter) {
    this.meanAfter.set(meanAfter);
  }

  public Double getDeviationAfter() {
    return deviationAfter.get();
  }

  public ObjectProperty<Double> deviationAfterProperty() {
    return deviationAfter;
  }

  public void setDeviationAfter(Double deviationAfter) {
    this.deviationAfter.set(deviationAfter);
  }

  public Double getMeanBefore() {
    return meanBefore.get();
  }

  public ObjectProperty<Double> meanBeforeProperty() {
    return meanBefore;
  }

  public void setMeanBefore(Double meanBefore) {
    this.meanBefore.set(meanBefore);
  }

  public Double getDeviationBefore() {
    return deviationBefore.get();
  }

  public ObjectProperty<Double> deviationBeforeProperty() {
    return deviationBefore;
  }

  public void setDeviationBefore(Double deviationBefore) {
    this.deviationBefore.set(deviationBefore);
  }

  public GamePlayerStatsFX getGamePlayerStats() {
    return gamePlayerStats.get();
  }

  public ObjectProperty<GamePlayerStatsFX> gamePlayerStatsProperty() {
    return gamePlayerStats;
  }

  public void setGamePlayerStats(GamePlayerStatsFX gamePlayerStats) {
    this.gamePlayerStats.set(gamePlayerStats);
  }

  public LeaderboardFX getLeaderboard() {
    return leaderboard.get();
  }

  public ObjectProperty<LeaderboardFX> leaderboardProperty() {
    return leaderboard;
  }

  public void setLeaderboard(LeaderboardFX leaderboard) {
    this.leaderboard.set(leaderboard);
  }
}
