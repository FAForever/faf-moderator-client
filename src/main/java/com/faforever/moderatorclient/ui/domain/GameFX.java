package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.GameReview;
import com.faforever.commons.api.dto.Validity;
import com.faforever.commons.api.dto.VictoryCondition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.OffsetDateTime;
import java.util.List;

public class GameFX extends AbstractEntityFX {
    private final StringProperty id;
    private final StringProperty name;
    private final ObjectProperty<OffsetDateTime> startTime;
    private final ObjectProperty<OffsetDateTime> endTime;
    private final ObjectProperty<Validity> validity;
    private final ObjectProperty<VictoryCondition> victoryCondition;
    private final ObservableList<GameReview> reviews;
    private final ObservableList<GamePlayerStatsFX> playerStats;
    private final ObjectProperty<PlayerFX> host;
    private final ObjectProperty<FeaturedModFX> featuredMod;
    private final ObjectProperty<MapVersionFX> mapVersion;

    public GameFX() {
        id = new SimpleStringProperty();
        name = new SimpleStringProperty();
        startTime = new SimpleObjectProperty<>();
        endTime = new SimpleObjectProperty<>();
        validity = new SimpleObjectProperty<>();
        victoryCondition = new SimpleObjectProperty<>();
        reviews = FXCollections.observableArrayList();
        playerStats = FXCollections.observableArrayList();
        host = new SimpleObjectProperty<>();
        featuredMod = new SimpleObjectProperty<>();
        mapVersion = new SimpleObjectProperty<>();
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

    public ObservableList<GamePlayerStatsFX> getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(List<GamePlayerStatsFX> statsFXList) {
        playerStats.clear();

        if (statsFXList != null) {
            playerStats.addAll(statsFXList);
        }
    }

    public PlayerFX getHost() {
        return host.get();
    }

    public void setHost(PlayerFX host) {
        this.host.set(host);
    }

    public ObjectProperty<PlayerFX> hostProperty() {
        return host;
    }

    public FeaturedModFX getFeaturedMod() {
        return featuredMod.get();
    }

    public void setFeaturedMod(FeaturedModFX featuredMod) {
        this.featuredMod.set(featuredMod);
    }

    public ObjectProperty<FeaturedModFX> featuredModProperty() {
        return featuredMod;
    }

    public MapVersionFX getMapVersion() {
        return mapVersion.get();
    }

    public void setMapVersion(MapVersionFX mapVersion) {
        this.mapVersion.set(mapVersion);
    }

    public ObjectProperty<MapVersionFX> mapVersionProperty() {
        return mapVersion;
    }

    public String getReplayUrl(String baseUrlFormat) {
        return String.format(baseUrlFormat, id.get());
    }
}
