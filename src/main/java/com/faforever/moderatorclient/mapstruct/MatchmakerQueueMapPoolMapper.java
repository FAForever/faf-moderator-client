package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.MatchmakerQueueMapPool;
import com.faforever.moderatorclient.ui.domain.MatchmakerQueueMapPoolFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, MatchmakerQueueMapper.class, MapPoolMapper.class})
public abstract class MatchmakerQueueMapPoolMapper {

    public abstract MatchmakerQueueMapPoolFX mapToFx(MatchmakerQueueMapPool dto);
    public abstract MatchmakerQueueMapPool mapToDto(MatchmakerQueueMapPoolFX fxBean);
    public abstract List<MatchmakerQueueMapPoolFX> mapToFx(List<MatchmakerQueueMapPool> dtoList);
    public abstract List<MatchmakerQueueMapPool> mapToDto(List<MatchmakerQueueMapPoolFX> fxBeanList);
}
