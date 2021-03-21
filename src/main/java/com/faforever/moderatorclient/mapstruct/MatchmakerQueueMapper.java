package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.MatchmakerQueue;
import com.faforever.moderatorclient.ui.domain.MatchmakerQueueFX;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, MatchmakerQueue.class, MapPoolMapper.class})
public abstract class MatchmakerQueueMapper {
    public abstract MatchmakerQueueFX map(MatchmakerQueue dto);
    public abstract MatchmakerQueue map(MatchmakerQueueFX fxBean);
}
