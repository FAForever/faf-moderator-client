package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.AvatarAssignment;
import com.faforever.moderatorclient.ui.domain.AvatarAssignmentFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, AvatarMapper.class, PlayerMapper.class, CycleAvoidingMappingContext.class})
public abstract class AvatarAssignmentMapper {
    public abstract AvatarAssignmentFX map(AvatarAssignment avatarAssignment);

    public abstract AvatarAssignment map(AvatarAssignmentFX avatarAssignmentFX);

    public abstract List<AvatarAssignmentFX> map(List<AvatarAssignment> avatarAssignmentList);
}
