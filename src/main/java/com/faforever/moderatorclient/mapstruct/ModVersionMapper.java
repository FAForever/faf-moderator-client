package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.ModVersion;
import com.faforever.moderatorclient.ui.domain.ModVersionFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, ModMapper.class, CycleAvoidingMappingContext.class})
public abstract class ModVersionMapper {
    public abstract ModVersionFX map(ModVersion dto);

    public abstract ModVersion map(ModVersionFX fxBean);

    public abstract List<ModVersionFX> mapToFX(List<ModVersion> dtoList);

    public abstract List<ModVersion> mapToDTO(List<ModVersionFX> fxBeanList);
}
