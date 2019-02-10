package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Tutorial;
import com.faforever.moderatorclient.ui.domain.TutorialFx;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, MapVersionMapper.class, TutorialCategoryMapper.class, CycleAvoidingMappingContext.class})
public abstract class TutorialMapper {
    public abstract TutorialFx map(Tutorial dto);

    public abstract Tutorial map(TutorialFx fxBean);

    public abstract List<TutorialFx> map(List<Tutorial> dtoList);
}
