package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.BanInfo;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.commons.api.elide.ElideNavigatorOnId;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.mapstruct.BanInfoMapper;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import com.google.common.collect.ImmutableMap;
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
    private final FafApiCommunicationService fafApi;

    public BanService(BanInfoMapper banInfoMapper, FafApiCommunicationService fafApi) {
        this.banInfoMapper = banInfoMapper;
        this.fafApi = fafApi;
    }

    public BanInfo patchBanInfo(@NotNull BanInfoFX banInfoFX) {
        BanInfo banInfo = banInfoMapper.map(banInfoFX);
        log.debug("Patching BanInfo of id: ", banInfo.getId());
        banInfo.setAuthor(null);
        banInfo.setPlayer(null);
        return fafApi.patch(ElideNavigator.of(BanInfo.class).id(banInfo.getId()), banInfo);
    }

    public String createBan(@NotNull BanInfoFX banInfoFX) {
        BanInfo banInfo = banInfoMapper.map(banInfoFX);
        log.debug("Creating ban");
        return fafApi.post(ElideNavigator.of(BanInfo.class).collection(), banInfo).getId();
    }

    public CompletableFuture<List<BanInfoFX>> getLatestBans() {
        return CompletableFuture.supplyAsync(() -> {
            List<BanInfo> banInfos = fafApi.getPage(ElideNavigator.of(BanInfo.class)
                    .collection()
                    .addIncludeOnCollection("player")
                    .addIncludeOnCollection("author")
                    .addIncludeOnCollection("revokeAuthor")
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
                .addIncludeOnId("player")
                .addIncludeOnId("author");
        return banInfoMapper.map(fafApi.getOne(navigator));
    }

    public void updateBan(BanInfo banInfoUpdate) {
        log.debug("Update for ban id: " + banInfoUpdate.getId());
        ElideNavigatorOnId<BanInfo> navigator = ElideNavigator.of(BanInfo.class)
                .id(banInfoUpdate.getId());

        fafApi.patch(navigator, banInfoUpdate);
    }

    public List<BanInfoFX> getBanInfoByBannedPlayerNameContains(String name) {
        ElideNavigatorOnCollection<BanInfo> navigator = ElideNavigator.of(BanInfo.class)
                .collection()
                .addFilter(ElideNavigator.qBuilder().string("player.login").eq("*" + name + "*"))
                .addIncludeOnCollection("player")
                .addIncludeOnCollection("author");
        return banInfoMapper.mapToFX(fafApi.getAll(navigator));
    }
}
