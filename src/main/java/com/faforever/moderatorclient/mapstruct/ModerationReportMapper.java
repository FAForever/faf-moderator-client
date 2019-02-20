package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.ModerationReport;
import com.faforever.moderatorclient.ui.domain.ModerationReportFX;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, PlayerMapper.class, FeaturedModMapper.class, BanInfoMapper.class, CycleAvoidingMappingContext.class})
public abstract class ModerationReportMapper {
	public abstract ModerationReportFX map(ModerationReport dto);

	public abstract ModerationReport map(ModerationReportFX fxBean);

	public abstract List<ModerationReportFX> mapToFx(List<ModerationReport> dtoList);

	public abstract List<ModerationReport> mapToDto(List<ModerationReportFX> fxBeanList);

	public abstract Set<ModerationReportFX> mapToFx(Set<ModerationReport> dtoList);

	public abstract Set<ModerationReport> mapToDto(Set<ModerationReportFX> fxBeanList);
}