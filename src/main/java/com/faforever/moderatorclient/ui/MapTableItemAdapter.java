package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.dto.Map;
import com.faforever.moderatorclient.api.dto.MapVersion;
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

    public String getName() {
        return isMap() ? map.getDisplayName() : null;
    }

    public String getDescription() {
        return isMapVersion() ? mapVersion.getDescription() : null;
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
        return isMapVersion() ? (mapVersion.isRanked() ? "yes" : "no") : null;
    }

    public String isHidden() {
        return isMapVersion() ? (mapVersion.isHidden() ? "yes" : "no") : null;
    }

    public URL getThumbnailUrlLarge() {
        return isMapVersion() ? mapVersion.getThumbnailUrlLarge() : null;
    }
}
