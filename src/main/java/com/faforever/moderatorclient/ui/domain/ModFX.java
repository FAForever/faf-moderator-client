package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.OffsetDateTime;

public class ModFX extends AbstractEntityFX {
    private final StringProperty id;
    private final StringProperty displayName;
    private final StringProperty author;
    private final ObjectProperty<PlayerFX> uploader;
    private final ObjectProperty<OffsetDateTime> createTime;
    private final ObservableList<ModVersionFX> versions;
    private final ObjectProperty<ModVersionFX> latestVersion;

    public ModFX() {
        uploader = new SimpleObjectProperty<>();
        versions = FXCollections.observableArrayList();
        latestVersion = new SimpleObjectProperty<>();
        id = new SimpleStringProperty();
        displayName = new SimpleStringProperty();
        author = new SimpleStringProperty();
        createTime = new SimpleObjectProperty<>();
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

    public String getDisplayName() {
        return displayName.get();
    }

    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public StringProperty authorProperty() {
        return author;
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

    public ObservableList<ModVersionFX> getVersions() {
        return versions;
    }

    public ModVersionFX getLatestVersion() {
        return latestVersion.get();
    }

    public void setLatestVersion(ModVersionFX latestVersion) {
        this.latestVersion.set(latestVersion);
    }

    public ObjectProperty<ModVersionFX> latestVersionProperty() {
        return latestVersion;
    }

    public PlayerFX getUploader() {
        return uploader.get();
    }

    public void setUploader(PlayerFX uploader) {
        this.uploader.set(uploader);
    }

    public ObjectProperty<PlayerFX> uploaderProperty() {
        return uploader;
    }
}
