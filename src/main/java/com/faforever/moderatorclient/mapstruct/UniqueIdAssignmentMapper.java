package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.UniqueIdAssignment;
import com.faforever.moderatorclient.ui.domain.UniqueIdAssignmentFx;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, UniqueIdMapper.class, CycleAvoidingMappingContext.class})
public abstract class UniqueIdAssignmentMapper {
    public abstract UniqueIdAssignmentFx map(UniqueIdAssignment dto);

    public abstract UniqueIdAssignment map(UniqueIdAssignmentFx fxBean);

    public abstract List<UniqueIdAssignmentFx> mapToFX(List<UniqueIdAssignment> dtoList);

    public abstract List<UniqueIdAssignment> mapToDto(List<UniqueIdAssignmentFx> fxBeanList);

    public abstract Set<UniqueIdAssignmentFx> mapToFX(Set<UniqueIdAssignment> dtoList);

    public abstract Set<UniqueIdAssignment> mapToDto(Set<UniqueIdAssignmentFx> fxBeanList);
}
