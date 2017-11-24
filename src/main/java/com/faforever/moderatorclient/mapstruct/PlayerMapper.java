package com.faforever.moderatorclient.mapstruct;

import com.faforever.moderatorclient.api.dto.Player;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, CycleAvoidingMappingContext.class})
public interface PlayerMapper {
    PlayerFX map(Player player);

    Player map(PlayerFX playerFX);

    List<PlayerFX> map(List<Player> playerList);
}
