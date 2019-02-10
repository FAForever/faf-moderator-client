package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.FeaturedMod;
import com.faforever.moderatorclient.ui.domain.FeaturedModFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = JavaFXMapper.class)
public interface FeaturedModMapper {
    FeaturedModFX map(FeaturedMod dto);

    FeaturedMod map(FeaturedModFX fxBean);

    List<FeaturedModFX> map(List<FeaturedMod> dtoList);
}