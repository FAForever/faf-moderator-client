package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.GroupPermission;
import com.faforever.moderatorclient.ui.domain.GroupPermissionFX;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(uses = {JavaFXMapper.class, UserGroupMapper.class, PlayerMapper.class, CycleAvoidingMappingContext.class})
public abstract class GroupPermissionMapper {
    public abstract GroupPermissionFX map(GroupPermission dto);

    public abstract GroupPermission map(GroupPermissionFX fxBean);

    public abstract Set<GroupPermissionFX> mapToFx(Set<GroupPermission> dtos);

    public abstract Set<GroupPermission> mapToDto(Set<GroupPermissionFX> fxBeans);
}
