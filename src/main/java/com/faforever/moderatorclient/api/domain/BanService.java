package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.BanInfo;
import com.faforever.commons.api.dto.BanRevokeData;
import com.faforever.commons.api.dto.Player;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.commons.api.elide.ElideNavigatorOnId;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.mapstruct.BanInfoMapper;
import com.faforever.moderatorclient.mapstruct.BanRevokeDataMapper;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import com.faforever.moderatorclient.ui.domain.BanRevokeDataFX;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BanService {

    private final BanInfoMapper banInfoMapper;
    private final BanRevokeDataMapper banRevokeDataMapper;
    private final FafApiCommunicationService fafApi;

    public BanService(BanInfoMapper banInfoMapper, BanRevokeDataMapper banRevokeDataMapper, FafApiCommunicationService fafApi) {
        this.banInfoMapper = banInfoMapper;
        this.banRevokeDataMapper = banRevokeDataMapper;
        this.fafApi = fafApi;
    }

    public BanInfo patchBanInfo(@NotNull BanInfoFX banInfoFX) {
        BanInfo banInfo = banInfoMapper.map(banInfoFX);
        log.debug("Patching BanInfo of id: ", banInfo.getId());
        return fafApi.patch(ElideNavigator.of(BanInfo.class).id(banInfo.getId()), banInfo);
    }

    public BanRevokeData revokeBan(@NotNull BanRevokeDataFX banRevokeDataFX) {
        BanRevokeData banRevokeData = banRevokeDataMapper.map(banRevokeDataFX);
        log.debug("Revoking ban with id: ", banRevokeData.getBan().getId());
        banRevokeData.setAuthor(fafApi.getSelfPlayer());
        ElideNavigatorOnCollection<BanRevokeData> navigator = ElideNavigator.of(Player.class)
                .id(banRevokeData.getBan().getId())
                .navigateRelationship(BanRevokeData.class, "banRevokeData")
                .collection();

        return (BanRevokeData) fafApi.post(navigator, banRevokeData);
    }

    public String createBan(@NotNull BanInfoFX banInfoFX) {
        BanInfo banInfo = banInfoMapper.map(banInfoFX);
        log.debug("Creating ban");
        banInfo.setAuthor(fafApi.getSelfPlayer());
        return fafApi.post(ElideNavigator.of(BanInfo.class).collection(), banInfo).getId();
    }

    public CompletableFuture<List<BanInfoFX>> getAllBans() {
        return CompletableFuture.supplyAsync(() -> {
            List<BanInfo> banInfos = fafApi.getAll(ElideNavigator.of(BanInfo.class)
                    .collection()
                    .addIncludeOnCollection("player")
                    .addIncludeOnCollection("author")
                    .addIncludeOnCollection("banRevokeData")
                    .addIncludeOnCollection("banRevokeData.author")
            );
            return banInfos.stream().map(banInfoMapper::map).collect(Collectors.toList());
        });
    }

    public BanInfoFX getBanInfoById(String banInfoId) {
        log.debug("Search for ban id: " + banInfoId);
        ElideNavigatorOnId<BanInfo> navigator = ElideNavigator.of(BanInfo.class)
                .id(banInfoId)
                .addIncludeOnId("player")
                .addIncludeOnId("author");
        return banInfoMapper.map(fafApi.getOne(navigator));
    }
}
