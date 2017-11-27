package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.api.dto.MapVersion;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class})
public interface MapVersionMapper {
    @Mapping(target = "map", ignore = true)
    MapVersionFX map(MapVersion mapVersion);

    @Mapping(target = "map", ignore = true)
    MapVersion map(MapVersionFX mapVersionFX);

    List<MapVersionFX> mapToFX(List<MapVersion> mapVersionList);

    List<MapVersion> mapToDTO(List<MapVersionFX> mapVersionFXList);
}
