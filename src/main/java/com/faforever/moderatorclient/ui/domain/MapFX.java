package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.OffsetDateTime;
import java.util.List;

public class MapFX extends AbstractEntityFX {
    private final StringProperty id;
    private final StringProperty battleType;
    private final ObjectProperty<OffsetDateTime> createTime;
    private final ObjectProperty<OffsetDateTime> updateTime;
    private final StringProperty displayName;
    private final ObjectProperty<PlayerFX> author;
    private final StringProperty mapType;
    private final ObjectProperty<MapVersionFX> latestVersion;
    private final ObservableList<MapVersionFX> versions;

    public MapFX() {
        id = new SimpleStringProperty();
        battleType = new SimpleStringProperty();
        createTime = new SimpleObjectProperty<>();
        updateTime = new SimpleObjectProperty<>();
        displayName = new SimpleStringProperty();
        author = new SimpleObjectProperty<>();
        mapType = new SimpleStringProperty();
        latestVersion = new SimpleObjectProperty<>();
        versions = FXCollections.observableArrayList();
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

    public String getBattleType() {
        return battleType.get();
    }

    public void setBattleType(String battleType) {
        this.battleType.set(battleType);
    }

    public StringProperty battleTypeProperty() {
        return battleType;
    }

    @Override
    public OffsetDateTime getCreateTime() {
        return createTime.get();
    }

    public void setCreateTime(OffsetDateTime createTime) {
        this.createTime.set(createTime);
    }

    @Override
    public ObjectProperty<OffsetDateTime> createTimeProperty() {
        return createTime;
    }

    @Override
    public OffsetDateTime getUpdateTime() {
        return updateTime.get();
    }

    public void setUpdateTime(OffsetDateTime updateTime) {
        this.updateTime.set(updateTime);
    }

    @Override
    public ObjectProperty<OffsetDateTime> updateTimeProperty() {
        return updateTime;
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
        versions.addAll(versionFXList);
    }

    //    @Relationship("author")
//    private Player author;
//
//    @Relationship("statistics")
//    private MapStatistics statistics;

}
