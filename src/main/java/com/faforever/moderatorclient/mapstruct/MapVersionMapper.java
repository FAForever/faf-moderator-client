package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.api.dto.MapVersion;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, MapMapper.class, CycleAvoidingMappingContext.class})
public abstract class MapVersionMapper {
    public abstract MapVersionFX map(MapVersion mapVersion);

    public abstract MapVersion map(MapVersionFX mapVersionFX);

    public abstract List<MapVersionFX> mapToFX(List<MapVersion> mapVersionList);

    public abstract List<MapVersion> mapToDTO(List<MapVersionFX> mapVersionFXList);
}
