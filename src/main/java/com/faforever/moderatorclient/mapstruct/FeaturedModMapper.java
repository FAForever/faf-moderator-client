package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.api.dto.FeaturedMod;
import com.faforever.moderatorclient.ui.domain.FeaturedModFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = JavaFXMapper.class)
public abstract class FeaturedModMapper {
    public abstract FeaturedModFX map(FeaturedMod featuredMod);

    public abstract FeaturedMod map(FeaturedModFX featuredModFX);

    public abstract List<FeaturedModFX> map(List<FeaturedMod> featuredMods);
}