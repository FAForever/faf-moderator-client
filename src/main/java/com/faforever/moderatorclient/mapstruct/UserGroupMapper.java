package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.UserGroup;
import com.faforever.moderatorclient.ui.domain.UserGroupFX;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, PlayerMapper.class, GroupPermissionMapper.class, CycleAvoidingMappingContext.class})
public abstract class UserGroupMapper {
    public abstract UserGroupFX map(UserGroup dto);

    public abstract UserGroup map(UserGroupFX fxBean);

    public abstract Set<UserGroupFX> mapToFx(Set<UserGroup> dtos);

    public abstract Set<UserGroup> mapToDto(Set<UserGroupFX> fxBeans);
}
