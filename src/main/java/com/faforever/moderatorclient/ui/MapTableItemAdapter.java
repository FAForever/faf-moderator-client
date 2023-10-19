package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.Map;
import com.faforever.commons.api.dto.MapVersion;
import lombok.Getter;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.net.URL;

@Getter
public class MapTableItemAdapter {
    private final Map map;
    private final MapVersion mapVersion;

    public MapTableItemAdapter(Map map) {
        this.map = map;
        mapVersion = null;
    }

    public MapTableItemAdapter(MapVersion mapVersion) {
        this.mapVersion = mapVersion;
        this.map = null;
    }

    public boolean isMap() {
        return map != null;
    }

    public boolean isMapVersion() {
        return mapVersion != null;
    }

    public String getId() {
        return isMap() ? map.getId() : mapVersion.getId();
    }

    public String getNameOrDescription() {
        return isMap() ? map.getDisplayName() : mapVersion.getDescription();
    }

    public ComparableVersion getVersion() {
        return isMapVersion() ? mapVersion.getVersion() : null;
    }

    public String getSize() {
        return isMapVersion() ? String.format("%sx%s (%s slots)", mapVersion.getWidth(), mapVersion.getHeight(), mapVersion.getMaxPlayers()) : null;
    }

    public String getFilename() {
        return isMapVersion() ? mapVersion.getFilename() : null;
    }

    public String isRanked() {
        return isMapVersion() ? (mapVersion.getRanked() ? "yes" : "no") : null;
    }

    public String isHidden() {
        return isMapVersion() ? (mapVersion.getHidden() ? "yes" : "no") : null;
    }

    public URL getThumbnailUrlLarge() {
        return isMapVersion() ? mapVersion.getThumbnailUrlLarge() : null;
    }

    public MapTableItemAdapter getThis() {
        return this;
    }
}
