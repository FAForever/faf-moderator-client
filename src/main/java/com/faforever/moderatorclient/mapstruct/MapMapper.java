package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.api.dto.Map;
import com.faforever.moderatorclient.ui.domain.MapFX;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, MapVersionMapper.class})
public abstract class MapMapper {
    public abstract MapFX map(Map map);

    @AfterMapping
    protected void addBackReferences(@MappingTarget MapFX mapFX) {
        mapFX.getVersions().forEach(mapVersionFX -> mapVersionFX.setMap(mapFX));
        mapFX.getLatestVersion().setMap(mapFX);
    }

    public abstract Map map(MapFX mapFX);

    @AfterMapping
    protected void addBackReferences(@MappingTarget Map map) {
        map.getVersions().forEach(mapVersionFX -> mapVersionFX.setMap(map));
        map.getLatestVersion().setMap(map);
    }

    public abstract List<MapFX> map(List<Map> mapList);
}
