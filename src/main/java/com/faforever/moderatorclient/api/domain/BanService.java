package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.BanInfo;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.commons.api.elide.ElideNavigatorOnId;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.FafUserCommunicationService;
import com.faforever.moderatorclient.api.dto.hydra.RevokeRefreshTokenRequest;
import com.faforever.moderatorclient.mapstruct.BanInfoMapper;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BanService {
    private static final String REVOKE_ENDPOINT = "/oauth2/revokeTokens";

    private final BanInfoMapper banInfoMapper;
    private final FafApiCommunicationService fafApi;
    private final FafUserCommunicationService fafUser;

    public BanInfo patchBanInfo(@NotNull BanInfoFX banInfoFX) {
        BanInfo banInfo = banInfoMapper.map(banInfoFX);
        log.debug("Patching BanInfo of id: {}", banInfo.getId());
        fafUser.post(REVOKE_ENDPOINT, RevokeRefreshTokenRequest.allClientsOf(banInfo.getPlayer().getId()));
        banInfo.setAuthor(null);
        banInfo.setPlayer(null);
        return fafApi.patch(ElideNavigator.of(BanInfo.class).id(banInfo.getId()), banInfo);
    }

    public String createBan(@NotNull BanInfoFX banInfoFX) {
        BanInfo banInfo = banInfoMapper.map(banInfoFX);
        log.debug("Creating ban");
        fafUser.post(REVOKE_ENDPOINT, RevokeRefreshTokenRequest.allClientsOf(banInfo.getPlayer().getId()));
        return fafApi.post(ElideNavigator.of(BanInfo.class).collection(), banInfo).getId();
    }

    public void updateBan(BanInfo banInfoUpdate) {
        log.debug("Update for ban id: " + banInfoUpdate.getId());
        ElideNavigatorOnId<BanInfo> navigator = ElideNavigator.of(BanInfo.class)
                .id(banInfoUpdate.getId());

        fafUser.post(REVOKE_ENDPOINT, RevokeRefreshTokenRequest.allClientsOf(banInfoUpdate.getPlayer().getId()));
        fafApi.patch(navigator, banInfoUpdate);
    }

    public CompletableFuture<List<BanInfoFX>> getLatestBans() {
        return CompletableFuture.supplyAsync(() -> {
            List<BanInfo> banInfos = fafApi.getPage(BanInfo.class, ElideNavigator.of(BanInfo.class)
                    .collection()
                    .addInclude("player")
                    .addInclude("author")
                    .addInclude("revokeAuthor")
                            .addSortingRule("createTime", false),
                    100,
                    1,
                    ImmutableMap.of()
            );
            return banInfos.stream().map(banInfoMapper::map).collect(Collectors.toList());
        });
    }

    public BanInfoFX getBanInfoById(String banInfoId) {
        log.debug("Search for ban id: " + banInfoId);
        ElideNavigatorOnId<BanInfo> navigator = ElideNavigator.of(BanInfo.class)
                .id(banInfoId)
                .addInclude("player")
                .addInclude("author");
        return banInfoMapper.map(fafApi.getOne(navigator));
    }

    public List<BanInfoFX> getBanInfoByBannedPlayerNameContains(String name) {
        ElideNavigatorOnCollection<BanInfo> navigator = ElideNavigator.of(BanInfo.class)
                .collection()
                .setFilter(ElideNavigator.qBuilder().string("player.login").eq("*" + name + "*"))
                .addInclude("player")
                .addInclude("author");
        return banInfoMapper.mapToFX(fafApi.getAll(BanInfo.class, navigator));
    }
}
