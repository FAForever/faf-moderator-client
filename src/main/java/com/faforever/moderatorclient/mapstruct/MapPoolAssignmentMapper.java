package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.MapPoolAssignment;
import com.faforever.moderatorclient.ui.domain.MapPoolAssignmentFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, MapVersionMapper.class})
public abstract class MapPoolAssignmentMapper {
    public abstract MapPoolAssignmentFX map(MapPoolAssignment dto);

    public abstract MapPoolAssignment map(MapPoolAssignmentFX fxBean);

    public abstract List<MapPoolAssignmentFX> mapToFX(List<MapPoolAssignment> dtoList);

    public abstract List<MapPoolAssignment> mapToDTO(List<MapPoolAssignmentFX> fxBeanList);
}
