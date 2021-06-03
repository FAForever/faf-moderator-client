package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class MapFX extends AbstractEntityFX {
    private final BooleanProperty recommended;
    private final StringProperty battleType;
    private final StringProperty displayName;
    private final ObjectProperty<PlayerFX> author;
    private final StringProperty mapType;
    private final ObjectProperty<MapVersionFX> latestVersion;
    private final ObservableList<MapVersionFX> versions;

    public MapFX() {
        recommended = new SimpleBooleanProperty();
        battleType = new SimpleStringProperty();
        displayName = new SimpleStringProperty();
        author = new SimpleObjectProperty<>();
        mapType = new SimpleStringProperty();
        latestVersion = new SimpleObjectProperty<>();
        versions = FXCollections.observableArrayList();
    }

    public boolean isRecommended() {
        return recommended.get();
    }

    public void setRecommended(boolean recommended) {
        this.recommended.set(recommended);
    }

    public BooleanProperty recommendedProperty() {
        return recommended;
    }

    public String getBattleType() {
        return battleType.get();
    }

    public void setBattleType(String battleType) {
        this.battleType.set(battleType);
    }

    public StringProperty battleTypeProperty() {
        return battleType;
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    public PlayerFX getAuthor() {
        return author.get();
    }

    public void setAuthor(PlayerFX author) {
        this.author.set(author);
    }

    public ObjectProperty<PlayerFX> authorProperty() {
        return author;
    }

    public String getMapType() {
        return mapType.get();
    }

    public void setMapType(String mapType) {
        this.mapType.set(mapType);
    }

    public StringProperty mapTypeProperty() {
        return mapType;
    }

    public MapVersionFX getLatestVersion() {
        return latestVersion.get();
    }

    public void setLatestVersion(MapVersionFX latestVersion) {
        this.latestVersion.set(latestVersion);
    }

    public ObjectProperty<MapVersionFX> latestVersionProperty() {
        return latestVersion;
    }

    public ObservableList<MapVersionFX> getVersions() {
        return versions;
    }

    public void setVersions(List<MapVersionFX> versionFXList) {
        versions.clear();

        if (versionFXList != null) {
            versions.addAll(versionFXList);
        }
    }

//    @Relationship("statistics")
//    private MapStatistics statistics;

}
