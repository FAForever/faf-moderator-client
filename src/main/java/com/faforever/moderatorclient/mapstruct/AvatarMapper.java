package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Avatar;
import com.faforever.moderatorclient.ui.domain.AvatarFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, AvatarAssignmentMapper.class, CycleAvoidingMappingContext.class})
public abstract class AvatarMapper {
    public abstract AvatarFX map(Avatar avatar);

    public abstract Avatar map(AvatarFX avatarFX);

    public abstract List<AvatarFX> map(List<Avatar> avatarList);
}
