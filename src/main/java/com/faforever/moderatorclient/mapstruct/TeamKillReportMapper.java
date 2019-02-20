package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.api.domain.TeamKillReportFx;
import com.faforever.moderatorclient.api.dto.TeamkillReport;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, PlayerMapper.class, GameMapper.class})
public abstract class TeamKillReportMapper {
	public abstract TeamKillReportFx map(TeamkillReport dto);

	public abstract TeamkillReport map(TeamKillReportFx fxBean);

	public abstract List<TeamKillReportFx> map(List<TeamkillReport> dtoList);
}
