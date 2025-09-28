package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.UniqueId;
import com.faforever.moderatorclient.ui.domain.UniqueIdFx;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, CycleAvoidingMappingContext.class})
public abstract class UniqueIdMapper {
    public abstract UniqueIdFx map(UniqueId dto);

    public abstract UniqueId map(UniqueIdFx fxBean);
}
