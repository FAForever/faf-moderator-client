package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.MapVersion;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, MapMapper.class, CycleAvoidingMappingContext.class})
public abstract class MapVersionMapper {
    public abstract MapVersionFX map(MapVersion dto);

    public abstract MapVersion map(MapVersionFX fxBean);

    public abstract List<MapVersionFX> mapToFX(List<MapVersion> dtoList);

    public abstract List<MapVersion> mapToDTO(List<MapVersionFX> fxBeanList);
}
