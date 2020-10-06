package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.VotingChoice;
import com.faforever.moderatorclient.ui.domain.VotingChoiceFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, VotingQuestionMapper.class, CycleAvoidingMappingContext.class})
public abstract class VotingChoiceMapper {
    public abstract VotingChoiceFX map(VotingChoice dto);

    public abstract VotingChoice map(VotingChoiceFX fxBean);

    public abstract List<VotingChoiceFX> mapToFX(List<VotingChoice> dtoList);

    public abstract List<VotingChoice> mapToDto(List<VotingChoiceFX> dtoList);
}
