package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MatchmakerQueueMapPoolFX extends AbstractEntityFX {
    private final DoubleProperty minRating;
    private final DoubleProperty maxRating;
    private final IntegerProperty vetoTokensPerPlayer;
    private final IntegerProperty maxTokensPerMap;
    private final DoubleProperty minimumMapsAfterVeto;
    private final ObjectProperty<MatchmakerQueueFX> matchmakerQueue;
    private final ObjectProperty<MapPoolFX> mapPool;

    public MatchmakerQueueMapPoolFX() {
        minRating = new SimpleDoubleProperty();
        maxRating = new SimpleDoubleProperty();
        vetoTokensPerPlayer = new SimpleIntegerProperty();
        maxTokensPerMap = new SimpleIntegerProperty();
        minimumMapsAfterVeto = new SimpleDoubleProperty();
        matchmakerQueue = new SimpleObjectProperty<>();
        mapPool = new SimpleObjectProperty<>();
    }

    public double getMinRating() {
        return minRating.get();
    }

    public DoubleProperty minRatingProperty() {
        return minRating;
    }

    public void setMinRating(double minRating) {
        this.minRating.set(minRating);
    }

    public double getMaxRating() {
        return maxRating.get();
    }

    public DoubleProperty maxRatingProperty() {
        return maxRating;
    }

    public void setMaxRating(double maxRating) {
        this.maxRating.set(maxRating);
    }

    public int getVetoTokensPerPlayer() {
        return vetoTokensPerPlayer.get();
    }

    public IntegerProperty vetoTokensPerPlayerProperty() {
        return vetoTokensPerPlayer;
    }

    public void setVetoTokensPerPlayer(int vetoTokensPerPlayer) {
        this.vetoTokensPerPlayer.set(vetoTokensPerPlayer);
    }

    public Integer getMaxTokensPerMap() {
        return maxTokensPerMap.get();
    }

    public IntegerProperty maxTokensPerMapProperty() {
        return maxTokensPerMap;
    }

    public void setMaxTokensPerMap(int maxTokensPerMap) {
        this.maxTokensPerMap.set(maxTokensPerMap);
    }

    public double getMinimumMapsAfterVeto() {
        return minimumMapsAfterVeto.get();
    }

    public DoubleProperty minimumMapsAfterVetoProperty() {
        return minimumMapsAfterVeto;
    }

    public void setMinimumMapsAfterVeto(double minimumMapsAfterVeto) {
        this.minimumMapsAfterVeto.set(minimumMapsAfterVeto);
    }

    public MatchmakerQueueFX getMatchmakerQueue() {
        return matchmakerQueue.get();
    }

    public ObjectProperty<MatchmakerQueueFX> matchmakerQueueProperty() {
        return matchmakerQueue;
    }

    public void setMatchmakerQueue(MatchmakerQueueFX matchmakerQueue) {
        this.matchmakerQueue.set(matchmakerQueue);
    }

    public MapPoolFX getMapPool() {
        return mapPool.get();
    }

    public ObjectProperty<MapPoolFX> mapPoolProperty() {
        return mapPool;
    }

    public void setMapPool(MapPoolFX mapPool) {
        this.mapPool.set(mapPool);
    }
}
