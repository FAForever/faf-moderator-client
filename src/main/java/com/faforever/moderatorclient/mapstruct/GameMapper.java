package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.api.dto.Game;
import com.faforever.moderatorclient.ui.domain.GameFX;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, GamePlayerStatsMapper.class, PlayerMapper.class, FeaturedModMapper.class, MapVersionMapper.class})
public abstract class GameMapper {
    @Mapping(target = "reviews", ignore = true)
    public abstract GameFX map(Game game);

    public abstract Game map(GameFX gameFX);

    public abstract List<GameFX> map(List<Game> games);
}
