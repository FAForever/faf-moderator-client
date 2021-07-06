package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Player;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, UniqueIdMapper.class, NameRecordMapper.class, BanInfoMapper.class, AvatarAssignmentMapper.class, CycleAvoidingMappingContext.class})
public abstract class PlayerMapper {
    public abstract PlayerFX map(Player dto);

    public abstract Player map(PlayerFX fxBean);

    public abstract List<PlayerFX> mapToFx(List<Player> dtoList);

    public abstract List<Player> mapToDto(List<PlayerFX> fxBeanList);

    public abstract Set<PlayerFX> mapToFx(Set<Player> dtoList);

    public abstract Set<Player> mapToDto(Set<PlayerFX> fxBeanList);
}
