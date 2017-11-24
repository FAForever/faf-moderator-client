package com.faforever.moderatorclient.api.rest.domain;

import com.faforever.moderatorclient.api.dto.*;
import com.faforever.moderatorclient.api.rest.ElideRouteBuilder;
import com.faforever.moderatorclient.api.rest.FafApiCommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final FafApiCommunicationService fafApi;

    public UserService(FafApiCommunicationService fafApi) {
        this.fafApi = fafApi;
    }

    private ElideRouteBuilder addModeratorIncludes(@NotNull ElideRouteBuilder builder) {
        return addModeratorIncludes(builder, null);
    }

    private ElideRouteBuilder addModeratorIncludes(@NotNull ElideRouteBuilder builder, String prefix) {
        String variablePrefix = "";

        if (prefix != null) {
            variablePrefix = prefix + ".";
        }

        return builder
                .addInclude(variablePrefix + "names")
                .addInclude(variablePrefix + "globalRating")
                .addInclude(variablePrefix + "ladder1v1Rating")
                .addInclude(variablePrefix + "lobbyGroup")
                .addInclude(variablePrefix + "avatarAssignments")
                .addInclude(variablePrefix + "avatarAssignments.avatar")
                .addInclude(variablePrefix + "bans")
                .addInclude(variablePrefix + "bans.banRevokeData");
    }

    public List<Player> findLatestRegistrations() {
        log.debug("Searching for latest registrations");
        ElideRouteBuilder<Player> routeBuilder = ElideRouteBuilder.of(Player.class)
                .sort("id", false)
                .pageSize(50);
        addModeratorIncludes(routeBuilder);

        List<Player> result = fafApi.getPage(routeBuilder, 100, 1, Collections.emptyMap());
        log.trace("found {} users", result.size());
        return result;
    }

    private List<Player> findUsersByAttribute(@NotNull String attribute, @NotNull String pattern) {
        log.debug("Searching for user by attribute '{}' with pattern: {}", attribute, pattern);
        ElideRouteBuilder<Player> routeBuilder = ElideRouteBuilder.of(Player.class)
                .filter(ElideRouteBuilder.qBuilder().string(attribute).eq(pattern));
        addModeratorIncludes(routeBuilder);

        List<Player> result = fafApi.getAll(routeBuilder);
        log.trace("found {} users", result.size());
        return result;
    }

    public List<Player> findUserById(@NotNull String pattern) {
        return findUsersByAttribute("id", pattern);
    }

    public List<Player> findUserByName(@NotNull String pattern) {
        return findUsersByAttribute("login", pattern);
    }

    public List<Player> findUserByEmail(@NotNull String pattern) {
        return findUsersByAttribute("email", pattern);
    }

    public List<Player> findUserBySteamId(@NotNull String pattern) {
        return findUsersByAttribute("steamId", pattern);
    }

    public List<Player> findUserByIP(@NotNull String pattern) {
        return findUsersByAttribute("recentIpAddress", pattern);
    }

    public Collection<Player> findUsersByPreviousName(@NotNull String pattern) {
        log.debug("Searching for user by previous name with pattern: {}", pattern);
        ElideRouteBuilder<NameRecord> routeBuilder = ElideRouteBuilder.of(NameRecord.class)
                .addInclude("player")
                .filter(ElideRouteBuilder.qBuilder().string("name").eq(pattern));
        addModeratorIncludes(routeBuilder, "player");

        List<NameRecord> result = fafApi.getAll(routeBuilder);
        log.trace("found {} name records", result.size());
        return result.stream()
                .map(NameRecord::getPlayer)
                .collect(Collectors.toSet());
    }

    public List<Teamkill> findLatestTeamkills() {
        log.debug("Searching for latest teamkills ");
        ElideRouteBuilder<Teamkill> routeBuilder = ElideRouteBuilder.of(Teamkill.class)
                .addInclude("teamkiller")
                .addInclude("victim")
                .sort("id", false);

        List<Teamkill> result = fafApi.getPage(routeBuilder, 100, 1, Collections.emptyMap());
        log.trace("found {} teamkills", result.size());
        return result;
    }

    public List<Teamkill> findTeamkillsByUserId(@NotNull String userId) {
        log.debug("Searching for teamkills invoked by user id: {}", userId);
        ElideRouteBuilder<Teamkill> routeBuilder = ElideRouteBuilder.of(Teamkill.class)
                .addInclude("teamkiller")
                .addInclude("victim")
                .filter(ElideRouteBuilder.qBuilder().string("teamkiller.id").eq(userId));

        List<Teamkill> result = fafApi.getAll(routeBuilder);
        log.trace("found {} teamkills", result.size());
        return result;
    }

    public BanInfo patchBanInfo(@NotNull BanInfo banInfo) {
        log.debug("Patching BanInfo of id: ", banInfo.getId());
        return fafApi.patch(ElideRouteBuilder.of(BanInfo.class).id(banInfo.getId()), banInfo);
    }

    public BanRevokeData revokeBan(@NotNull BanRevokeData banRevokeData) {
        log.debug("Revoking ban with id: ", banRevokeData.getBan().getId());
        banRevokeData.setAuthor(fafApi.getSelfPlayer());
        ElideRouteBuilder<Player> routeBuilder = ElideRouteBuilder.of(Player.class)
                .id(banRevokeData.getBan().getId())
                .relationship("banRevokeData");

        return (BanRevokeData) fafApi.postRelationship(routeBuilder, banRevokeData);
    }

    public BanInfo createBan(@NotNull BanInfo banInfo) {
        log.debug("Creating ban");
        banInfo.setAuthor(fafApi.getSelfPlayer());
        return fafApi.post(ElideRouteBuilder.of(BanInfo.class), banInfo);
    }
}
