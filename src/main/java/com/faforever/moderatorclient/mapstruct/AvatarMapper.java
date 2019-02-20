package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Avatar;
import com.faforever.moderatorclient.ui.domain.AvatarFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, AvatarAssignmentMapper.class, CycleAvoidingMappingContext.class})
public abstract class AvatarMapper {
    public abstract AvatarFX map(Avatar dto);

    public abstract Avatar map(AvatarFX fxBean);

    public abstract List<AvatarFX> map(List<Avatar> dtoList);
}
