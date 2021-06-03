package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ModFX extends AbstractEntityFX {
    private final BooleanProperty recommended;
    private final StringProperty displayName;
    private final StringProperty author;
    private final ObjectProperty<PlayerFX> uploader;
    private final ObservableList<ModVersionFX> versions;
    private final ObjectProperty<ModVersionFX> latestVersion;

    public ModFX() {
        recommended = new SimpleBooleanProperty();
        uploader = new SimpleObjectProperty<>();
        versions = FXCollections.observableArrayList();
        latestVersion = new SimpleObjectProperty<>();
        displayName = new SimpleStringProperty();
        author = new SimpleStringProperty();
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
