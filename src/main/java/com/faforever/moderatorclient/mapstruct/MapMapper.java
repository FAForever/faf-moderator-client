package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Map;
import com.faforever.moderatorclient.ui.domain.MapFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, MapVersionMapper.class, PlayerMapper.class, CycleAvoidingMappingContext.class})
public abstract class MapMapper {
    public abstract MapFX map(Map map);

    public abstract Map map(MapFX mapFX);

    public abstract List<MapFX> map(List<Map> mapList);
}
