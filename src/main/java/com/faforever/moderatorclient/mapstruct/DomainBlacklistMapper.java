package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.DomainBlacklist;
import com.faforever.moderatorclient.ui.domain.DomainBlacklistFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = JavaFXMapper.class)
public interface DomainBlacklistMapper {
    DomainBlacklistFX map(DomainBlacklist dto);

    DomainBlacklist map(DomainBlacklistFX fxBean);

    List<DomainBlacklistFX> map(List<DomainBlacklist> dtoList);
}
