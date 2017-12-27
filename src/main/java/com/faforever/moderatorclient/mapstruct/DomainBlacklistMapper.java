package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.DomainBlacklist;
import com.faforever.moderatorclient.ui.domain.DomainBlacklistFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = JavaFXMapper.class)
public interface DomainBlacklistMapper {
    DomainBlacklistFX map(DomainBlacklist domainBlacklist);

    DomainBlacklist map(DomainBlacklistFX domainBlacklistFX);

    List<DomainBlacklistFX> map(List<DomainBlacklist> domainBlacklistList);
}
