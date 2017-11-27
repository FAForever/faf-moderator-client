package com.faforever.moderatorclient.ui.domain;

import com.faforever.moderatorclient.api.dto.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.OffsetDateTime;

public class GameFX extends AbstractEntityFX {
    private final StringProperty id;
    private final StringProperty name;
    private final ObjectProperty<OffsetDateTime> startTime;
    private final ObjectProperty<OffsetDateTime> endTime;
    private final ObjectProperty<Validity> validity;
    private final ObjectProperty<VictoryCondition> victoryCondition;
    private final ObservableList<GameReview> reviews;
    private final ObservableList<GamePlayerStats> playerStats;
    private final ObjectProperty<Player> host;
    private final ObjectProperty<FeaturedMod> featuredMod;
    private final ObjectProperty<MapVersion> mapVersion;

    public GameFX() {
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.startTime = new SimpleObjectProperty<>();
        this.endTime = new SimpleObjectProperty<>();
        this.validity = new SimpleObjectProperty<>();
        this.victoryCondition = new SimpleObjectProperty<>();
        this.reviews = FXCollections.observableArrayList();
        this.playerStats = FXCollections.observableArrayList();
        this.host = new SimpleObjectProperty<>();
        this.featuredMod = new SimpleObjectProperty<>();
        this.mapVersion = new SimpleObjectProperty<>();
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

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public OffsetDateTime getStartTime() {
        return startTime.get();
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime.set(startTime);
    }

    public ObjectProperty<OffsetDateTime> startTimeProperty() {
        return startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime.get();
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime.set(endTime);
    }

    public ObjectProperty<OffsetDateTime> endTimeProperty() {
        return endTime;
    }

    public Validity getValidity() {
        return validity.get();
    }

    public void setValidity(Validity validity) {
        this.validity.set(validity);
    }

    public ObjectProperty<Validity> validityProperty() {
        return validity;
    }

    public VictoryCondition getVictoryCondition() {
        return victoryCondition.get();
    }

    public void setVictoryCondition(VictoryCondition victoryCondition) {
        this.victoryCondition.set(victoryCondition);
    }

    public ObjectProperty<VictoryCondition> victoryConditionProperty() {
        return victoryCondition;
    }

    public ObservableList<GameReview> getReviews() {
        return reviews;
    }

    public ObservableList<GamePlayerStats> getPlayerStats() {
        return playerStats;
    }

    public Player getHost() {
        return host.get();
    }

    public void setHost(Player host) {
        this.host.set(host);
    }

    public ObjectProperty<Player> hostProperty() {
        return host;
    }

    public FeaturedMod getFeaturedMod() {
        return featuredMod.get();
    }

    public void setFeaturedMod(FeaturedMod featuredMod) {
        this.featuredMod.set(featuredMod);
    }

    public ObjectProperty<FeaturedMod> featuredModProperty() {
        return featuredMod;
    }

    public MapVersion getMapVersion() {
        return mapVersion.get();
    }

    public void setMapVersion(MapVersion mapVersion) {
        this.mapVersion.set(mapVersion);
    }

    public ObjectProperty<MapVersion> mapVersionProperty() {
        return mapVersion;
    }

    public String getReplayUrl(String baseUrlFormat) {
        return String.format(baseUrlFormat, id.get());
    }
}
