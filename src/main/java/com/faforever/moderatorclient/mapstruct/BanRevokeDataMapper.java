package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.BanRevokeData;
import com.faforever.moderatorclient.ui.domain.BanRevokeDataFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {JavaFXMapper.class, BanInfoMapper.class, PlayerMapper.class})
public interface BanRevokeDataMapper {
    BanRevokeDataFX map(BanRevokeData dto);

    BanRevokeData map(BanRevokeDataFX fxBean);

    List<BanRevokeDataFX> map(List<BanRevokeData> dtoList);
}
