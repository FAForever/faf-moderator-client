package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.NameRecord;
import com.faforever.moderatorclient.ui.domain.NameRecordFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, PlayerMapper.class})
public abstract class NameRecordMapper {
    public abstract NameRecordFX map(NameRecord dto);

    public abstract NameRecord map(NameRecordFX fxBean);

    public abstract List<NameRecordFX> mapToFX(List<NameRecord> dtoList);

    public abstract List<NameRecord> mapToDto(List<NameRecordFX> fxBeanList);
}
