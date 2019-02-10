package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.TutorialCategory;
import com.faforever.moderatorclient.ui.domain.TutorialCategoryFX;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, TutorialMapper.class, CycleAvoidingMappingContext.class})
public abstract class TutorialCategoryMapper {
    public abstract TutorialCategoryFX map(TutorialCategory dto);

    public abstract TutorialCategory map(TutorialCategoryFX fxBean);

    public abstract List<TutorialCategoryFX> map(List<TutorialCategory> dtoList);
}
