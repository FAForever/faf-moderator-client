package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.GamePlayerStats;
import com.faforever.moderatorclient.ui.domain.GamePlayerStatsFX;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, GameMapper.class, PlayerMapper.class, LeaderboardRatingJournalMapper.class, CycleAvoidingMappingContext.class})
public abstract class GamePlayerStatsMapper {

    @Mapping(target = "beforeRating", ignore = true)
    @Mapping(target = "afterRating", ignore = true)
    @Mapping(target = "ratingChange", ignore = true)
    public abstract GamePlayerStatsFX map(GamePlayerStats dto);

    public abstract GamePlayerStats map(GamePlayerStatsFX fxBean);

    public abstract List<GamePlayerStatsFX> map(List<GamePlayerStats> dtoList);
}