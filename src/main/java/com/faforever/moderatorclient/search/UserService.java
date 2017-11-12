package com.faforever.moderatorclient.search;

import com.faforever.moderatorclient.api.ElideRouteBuilder;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.dto.NameRecord;
import com.faforever.moderatorclient.api.dto.Player;
import com.faforever.moderatorclient.api.dto.Teamkill;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
                .addInclude(variablePrefix + "bans")
                .addInclude(variablePrefix + "bans.banRevokeData");
    }

    private List<Player> findUsersByAttribute(@NotNull String attribute, @NotNull String pattern) {
        log.debug("Searching for user by attribute '{}' with pattern: {}", attribute, pattern);
        ElideRouteBuilder routeBuilder = new ElideRouteBuilder(Player.class)
                .filter(ElideRouteBuilder.qBuilder().string(attribute).eq(pattern));
        addModeratorIncludes(routeBuilder);

        List<Player> result = fafApi.getAll(routeBuilder);
        log.trace("found {} users", result.size());
        return result;
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

    public Collection<Player> findUsersByPreviousName(@NotNull String pattern) {
        log.debug("Searching for user by previous name with pattern: {}", pattern);
        ElideRouteBuilder routeBuilder = new ElideRouteBuilder(NameRecord.class)
                .addInclude("player")
                .filter(ElideRouteBuilder.qBuilder().string("name").eq(pattern));
        addModeratorIncludes(routeBuilder, "player");

        List<NameRecord> result = fafApi.getAll(routeBuilder);
        log.trace("found {} name records", result.size());
        return result.stream()
                .map(NameRecord::getPlayer)
                .collect(Collectors.toSet());
    }

    public List<Teamkill> findTeamkillsByUserId(@NotNull String userId) {
        log.debug("Searching for teamkills invoked by user id: {}", userId);
        ElideRouteBuilder routeBuilder = new ElideRouteBuilder(Teamkill.class)
                .addInclude("teamkiller")
                .addInclude("victim")
                .filter(ElideRouteBuilder.qBuilder().string("teamkiller.id").eq(userId));

        List<Teamkill> result = fafApi.getAll(routeBuilder);
        log.trace("found {} teamkills", result.size());
        return result;
    }
}
