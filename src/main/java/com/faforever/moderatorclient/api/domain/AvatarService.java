package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.Avatar;
import com.faforever.moderatorclient.api.ElideRouteBuilder;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AvatarService {
    private final FafApiCommunicationService fafApi;

    public AvatarService(FafApiCommunicationService fafApi) {
        this.fafApi = fafApi;
    }

    public List<Avatar> getAll() {
        log.debug("Retrieving all avatars");
        List<Avatar> result = fafApi.getAll(ElideRouteBuilder.of(Avatar.class)
                .addInclude("assignments")
                .addInclude("assignments.player"));
        log.trace("found {} avatars", result.size());
        return result;
    }

    private List<Avatar> findAvatarsByAttribute(@NotNull String attribute, @NotNull String pattern) {
        log.debug("Searching for avatars by attribute '{}' with pattern: {}", attribute, pattern);
        ElideRouteBuilder<Avatar> routeBuilder = ElideRouteBuilder.of(Avatar.class)
                .addInclude("assignments")
                .addInclude("assignments.player")
                .filter(ElideRouteBuilder.qBuilder().string(attribute).eq(pattern));

        List<Avatar> result = fafApi.getAll(routeBuilder);
        log.trace("found {} avatars", result.size());
        return result;
    }

    public List<Avatar> findAvatarsById(@NotNull String pattern) {
        return findAvatarsByAttribute("id", pattern);
    }

    public List<Avatar> findAvatarsByTooltip(@NotNull String pattern) {
        return findAvatarsByAttribute("tooltip", pattern);
    }

    public List<Avatar> findAvatarsByAssignedUser(@NotNull String pattern) {
        log.debug("Searching for avatars by assigned player with pattern: {}", pattern);
        ElideRouteBuilder<Avatar> routeBuilder = ElideRouteBuilder.of(Avatar.class)
                .addInclude("assignments")
                .addInclude("assignments.player")
                .filter(ElideRouteBuilder.qBuilder().string("assignments.player.id").eq(pattern)
                        .or().string("assignments.player.login").eq(pattern));

        List<Avatar> result = fafApi.getAll(routeBuilder);
        log.trace("found {} avatars", result.size());
        return result;
    }
}
