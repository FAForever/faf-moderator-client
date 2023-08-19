package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Mod;
import com.faforever.moderatorclient.ui.domain.ModFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, ModVersionMapper.class, PlayerMapper.class, CycleAvoidingMappingContext.class})
public abstract class ModMapper {
    public abstract ModFX map(Mod dto);

    public abstract Mod map(ModFX fxBean);

    public abstract List<ModFX> map(List<Mod> dtoList);
}
