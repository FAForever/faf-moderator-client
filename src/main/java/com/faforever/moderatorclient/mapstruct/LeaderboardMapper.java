package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Leaderboard;
import com.faforever.moderatorclient.ui.domain.LeaderboardFX;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, CycleAvoidingMappingContext.class})
public abstract class LeaderboardMapper {

    public abstract LeaderboardFX map(Leaderboard dto);

    public abstract Leaderboard map(LeaderboardFX fxBean);
}