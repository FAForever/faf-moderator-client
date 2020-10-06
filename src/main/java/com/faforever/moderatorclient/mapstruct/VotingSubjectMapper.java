package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.VotingSubject;
import com.faforever.moderatorclient.ui.domain.VotingSubjectFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, VotingQuestionMapper.class, CycleAvoidingMappingContext.class})
public abstract class VotingSubjectMapper {
    public abstract VotingSubjectFX map(VotingSubject dto);

    public abstract VotingSubject map(VotingSubjectFX fxBean);

    public abstract List<VotingSubjectFX> mapToFx(List<VotingSubject> dtoList);

    public abstract List<VotingSubject> mapToDto(List<VotingSubjectFX> fxList);
}
