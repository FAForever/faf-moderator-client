package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.BanInfo;
import com.faforever.commons.api.dto.UniqueId;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import com.faforever.moderatorclient.ui.domain.UniqueIdFx;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, CycleAvoidingMappingContext.class})
public abstract class UniqueIdMapper {
    public abstract UniqueIdFx map(UniqueId dto);

    public abstract UniqueId map(UniqueIdFx fxBean);

    public abstract List<UniqueIdFx> mapToFX(List<UniqueId> dtoList);

    public abstract List<UniqueId> mapToDto(List<UniqueIdFx> fxBeanList);

	public abstract Set<UniqueIdFx> mapToFX(Set<UniqueId> dtoList);

	public abstract Set<UniqueId> mapToDto(Set<UniqueIdFx> fxBeanList);
}
