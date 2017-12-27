package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.Player;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, CycleAvoidingMappingContext.class})
public abstract class PlayerMapper {
    public abstract PlayerFX map(Player player);

    public abstract Player map(PlayerFX playerFX);

    public abstract List<PlayerFX> map(List<Player> playerList);
}
