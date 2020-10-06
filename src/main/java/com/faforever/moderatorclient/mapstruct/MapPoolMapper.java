package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.common.MapPool;
import com.faforever.moderatorclient.ui.domain.MapPoolFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, MapVersionMapper.class})
public abstract class MapPoolMapper {
    public abstract MapPoolFX mapToFx(MapPool dto);
    public abstract MapPool mapToDto(MapPoolFX fxBean);
    public abstract List<MapPoolFX> mapToFx(List<MapPool> dtoList);
    public abstract List<MapPool> mapToDto(List<MapPoolFX> fxBeanList);

}
