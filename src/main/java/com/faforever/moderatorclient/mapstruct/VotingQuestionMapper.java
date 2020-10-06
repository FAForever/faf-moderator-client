package com.faforever.moderatorclient.mapstruct;


import com.faforever.commons.api.dto.VotingQuestion;
import com.faforever.moderatorclient.ui.domain.VotingQuestionFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, VotingChoiceMapper.class, VotingSubjectMapper.class, CycleAvoidingMappingContext.class})
public abstract class VotingQuestionMapper {
    public abstract VotingQuestionFX map(VotingQuestion dto);

    public abstract VotingQuestion map(VotingQuestionFX fxBean);

    public abstract List<VotingQuestionFX> mapToFx(List<VotingQuestion> dtoList);

    public abstract List<VotingQuestion> mapToDTo(List<VotingQuestionFX> dtoList);
}