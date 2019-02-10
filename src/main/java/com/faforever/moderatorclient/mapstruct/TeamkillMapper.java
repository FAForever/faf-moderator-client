package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Teamkill;
import com.faforever.moderatorclient.ui.domain.TeamkillFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, PlayerMapper.class, GameMapper.class})
public abstract class TeamkillMapper {
    public abstract TeamkillFX map(Teamkill dto);

    public abstract Teamkill map(TeamkillFX fxBean);

    public abstract List<TeamkillFX> map(List<Teamkill> dtoList);
}
