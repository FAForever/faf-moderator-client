package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.AvatarAssignment;
import com.faforever.moderatorclient.ui.domain.AvatarAssignmentFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, AvatarMapper.class, PlayerMapper.class, CycleAvoidingMappingContext.class})
public abstract class AvatarAssignmentMapper {
    public abstract AvatarAssignmentFX map(AvatarAssignment dto);

    public abstract AvatarAssignment map(AvatarAssignmentFX fxBean);

    public abstract List<AvatarAssignmentFX> mapToFX(List<AvatarAssignment> dtoList);

    public abstract List<AvatarAssignment> mapToDto(List<AvatarAssignmentFX> fxBeanList);
}
