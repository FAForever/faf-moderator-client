package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.Mod;
import com.faforever.commons.api.dto.ModType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.net.URL;

public class ModVersionFX extends AbstractEntityFX {
    private final ObjectProperty<String> uid;
    private final ObjectProperty<ModType> type;
    private final ObjectProperty<String> description;
    private final ObjectProperty<ComparableVersion> version;
    private final ObjectProperty<String> filename;
    private final ObjectProperty<String> icon;
    private final BooleanProperty ranked;
    private final BooleanProperty hidden;
    private final ObjectProperty<URL> thumbnailUrl;
    private final ObjectProperty<URL> downloadUrl;
    private final ObjectProperty<Mod> mod;


    public ModVersionFX() {
        uid = new SimpleObjectProperty<>();
        type = new SimpleObjectProperty<>();
        description = new SimpleObjectProperty<>();
        version = new SimpleObjectProperty<>();
        filename = new SimpleObjectProperty<>();
        icon = new SimpleObjectProperty<>();
        ranked = new SimpleBooleanProperty();
        hidden = new SimpleBooleanProperty();
        thumbnailUrl = new SimpleObjectProperty<>();
        downloadUrl = new SimpleObjectProperty<>();
        mod = new SimpleObjectProperty<>();
    }

    public String getUid() {
        return uid.get();
    }

    public void setUid(String uid) {
        this.uid.set(uid);
    }

    public ObjectProperty<String> uidProperty() {
        return uid;
    }

    public ModType getType() {
        return type.get();
    }

    public void setType(ModType type) {
        this.type.set(type);
    }

    public ObjectProperty<ModType> typeProperty() {
        return type;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public ObjectProperty<String> descriptionProperty() {
        return description;
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

    public String getFilename() {
        return filename.get();
    }

    public void setFilename(String filename) {
        this.filename.set(filename);
    }

    public ObjectProperty<String> filenameProperty() {
        return filename;
    }

    public String getIcon() {
        return icon.get();
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }

    public ObjectProperty<String> iconProperty() {
        return icon;
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

    public URL getThumbnailUrl() {
        return thumbnailUrl.get();
    }

    public void setThumbnailUrl(URL thumbnailUrl) {
        this.thumbnailUrl.set(thumbnailUrl);
    }

    public ObjectProperty<URL> thumbnailUrlProperty() {
        return thumbnailUrl;
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

    public Mod getMod() {
        return mod.get();
    }

    public void setMod(Mod mod) {
        this.mod.set(mod);
    }

    public ObjectProperty<Mod> modProperty() {
        return mod;
    }
}
