package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.AccountLink;
import com.faforever.moderatorclient.ui.domain.AccountLinkFx;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, PlayerMapper.class, CycleAvoidingMappingContext.class})
public abstract class AccountLinkMapper {
    public abstract AccountLinkFx map(AccountLink dto);

    public abstract AccountLink map(AccountLinkFx fxBean);

    public abstract List<AccountLinkFx> mapToFX(List<AccountLink> dtoList);

    public abstract List<AccountLink> mapToDto(List<AccountLinkFx> fxBeanList);

	public abstract Set<AccountLinkFx> mapToFX(Set<AccountLink> dtoList);

	public abstract Set<AccountLink> mapToDto(Set<AccountLinkFx> fxBeanList);
}
