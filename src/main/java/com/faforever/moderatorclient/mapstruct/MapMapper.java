package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Map;
import com.faforever.moderatorclient.ui.domain.MapFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, MapVersionMapper.class, PlayerMapper.class, CycleAvoidingMappingContext.class})
public abstract class MapMapper {
    public abstract MapFX map(Map dto);

    public abstract Map map(MapFX fxBean);

    public abstract List<MapFX> map(List<Map> dtoList);
}
