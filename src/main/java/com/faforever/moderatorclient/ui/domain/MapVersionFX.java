package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.*;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.net.URL;

public class MapVersionFX extends AbstractEntityFX {
    private final StringProperty description;
    private final IntegerProperty maxPlayers;
    private final IntegerProperty width;
    private final IntegerProperty height;
    private final ObjectProperty<ComparableVersion> version;
    private final StringProperty folderName;
    // TODO name consistently with folderName
    private final StringProperty filename;
    private final BooleanProperty ranked;
    private final BooleanProperty hidden;
    private final ObjectProperty<URL> thumbnailUrlSmall;
    private final ObjectProperty<URL> thumbnailUrlLarge;
    private final ObjectProperty<URL> downloadUrl;

    private final ObjectProperty<MapFX> map;

    public MapVersionFX() {
        description = new SimpleStringProperty();
        maxPlayers = new SimpleIntegerProperty();
        width = new SimpleIntegerProperty();
        height = new SimpleIntegerProperty();
        version = new SimpleObjectProperty<>();
        folderName = new SimpleStringProperty();
        filename = new SimpleStringProperty();
        ranked = new SimpleBooleanProperty();
        hidden = new SimpleBooleanProperty();
        thumbnailUrlSmall = new SimpleObjectProperty<>();
        thumbnailUrlLarge = new SimpleObjectProperty<>();
        downloadUrl = new SimpleObjectProperty<>();
        map = new SimpleObjectProperty<>();
    }

//
//    @Relationship("statistics")
//    private MapVersionStatistics statistics;
//
//    @Nullable
//    @Relationship("ladder1v1Map")
//    private Ladder1v1Map ladder1v1Map;
//
//    @Relationship("reviews")
//    private List<MapVersionReview> reviews;

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public int getMaxPlayers() {
        return maxPlayers.get();
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers.set(maxPlayers);
    }

    public IntegerProperty maxPlayersProperty() {
        return maxPlayers;
    }

    public int getWidth() {
        return width.get();
    }

    public void setWidth(int width) {
        this.width.set(width);
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public int getHeight() {
        return height.get();
    }

    public void setHeight(int height) {
        this.height.set(height);
    }

    public IntegerProperty heightProperty() {
        return height;
    }

    public ComparableVersion getVersion() {
        return version.get();
    }

    public void setVersion(ComparableVersion version) {
        this.version.set(version);
    }

    public ObjectProperty<ComparableVersion> versionProperty() {
        return version;
    }

    public String getFolderName() {
        return folderName.get();
    }

    public void setFolderName(String folderName) {
        this.folderName.set(folderName);
    }

    public StringProperty folderNameProperty() {
        return folderName;
    }

    public String getFilename() {
        return filename.get();
    }

    public void setFilename(String filename) {
        this.filename.set(filename);
    }

    public StringProperty filenameProperty() {
        return filename;
    }

    public boolean isRanked() {
        return ranked.get();
    }

    public void setRanked(boolean ranked) {
        this.ranked.set(ranked);
    }

    public BooleanProperty rankedProperty() {
        return ranked;
    }

    public boolean isHidden() {
        return hidden.get();
    }

    public void setHidden(boolean hidden) {
        this.hidden.set(hidden);
    }

    public BooleanProperty hiddenProperty() {
        return hidden;
    }

    public URL getThumbnailUrlSmall() {
        return thumbnailUrlSmall.get();
    }

    public void setThumbnailUrlSmall(URL thumbnailUrlSmall) {
        this.thumbnailUrlSmall.set(thumbnailUrlSmall);
    }

    public ObjectProperty<URL> thumbnailUrlSmallProperty() {
        return thumbnailUrlSmall;
    }

    public URL getThumbnailUrlLarge() {
        return thumbnailUrlLarge.get();
    }

    public void setThumbnailUrlLarge(URL thumbnailUrlLarge) {
        this.thumbnailUrlLarge.set(thumbnailUrlLarge);
    }

    public ObjectProperty<URL> thumbnailUrlLargeProperty() {
        return thumbnailUrlLarge;
    }

    public URL getDownloadUrl() {
        return downloadUrl.get();
    }

    public void setDownloadUrl(URL downloadUrl) {
        this.downloadUrl.set(downloadUrl);
    }

    public ObjectProperty<URL> downloadUrlProperty() {
        return downloadUrl;
    }

    public MapFX getMap() {
        return map.get();
    }

    public void setMap(MapFX map) {
        this.map.set(map);
    }

    public ObjectProperty<MapFX> mapProperty() {
        return map;
    }
}
