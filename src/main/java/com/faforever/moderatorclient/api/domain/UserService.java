package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.*;
import com.faforever.moderatorclient.api.ElideRouteBuilder;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.mapstruct.FeaturedModMapper;
import com.faforever.moderatorclient.mapstruct.PlayerMapper;
import com.faforever.moderatorclient.mapstruct.TeamkillMapper;
import com.faforever.moderatorclient.mapstruct.UserNoteMapper;
import com.faforever.moderatorclient.ui.domain.FeaturedModFX;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import com.faforever.moderatorclient.ui.domain.TeamkillFX;
import com.faforever.moderatorclient.ui.domain.UserNoteFX;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final FafApiCommunicationService fafApi;
    private final PlayerMapper playerMapper;
    private final FeaturedModMapper featuredModMapper;
    private final UserNoteMapper userNoteMapper;
    private final TeamkillMapper teamkillMapper;


    public UserService(FafApiCommunicationService fafApi, PlayerMapper playerMapper, FeaturedModMapper featuredModMapper, UserNoteMapper userNoteMapper, TeamkillMapper teamkillMapper) {
        this.fafApi = fafApi;
        this.playerMapper = playerMapper;
        this.featuredModMapper = featuredModMapper;
        this.userNoteMapper = userNoteMapper;
        this.teamkillMapper = teamkillMapper;
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
                .addInclude(variablePrefix + "bans.author")
                .addInclude(variablePrefix + "bans.banRevokeData")
                .addInclude(variablePrefix + "bans.banRevokeData.author");
    }

    public List<PlayerFX> findLatestRegistrations() {
        log.debug("Searching for latest registrations");
        ElideRouteBuilder<Player> routeBuilder = ElideRouteBuilder.of(Player.class)
                .addInclude("bans")
                .sort("id", false)
                .pageSize(50);
        addModeratorIncludes(routeBuilder);

        List<Player> result = fafApi.getPage(routeBuilder, 100, 1, Collections.emptyMap());
        log.trace("found {} users", result.size());
        return playerMapper.mapToFx(result);
    }

    private List<PlayerFX> findUsersByAttribute(@NotNull String attribute, @NotNull String pattern) {
        log.debug("Searching for player by attribute '{}' with pattern: {}", attribute, pattern);
        ElideRouteBuilder<Player> routeBuilder = ElideRouteBuilder.of(Player.class)
                .filter(ElideRouteBuilder.qBuilder().string(attribute).eq(pattern));
        addModeratorIncludes(routeBuilder);

        List<Player> result = fafApi.getAll(routeBuilder);
        log.trace("found {} users", result.size());
        return playerMapper.mapToFx(result);
    }

    public List<PlayerFX> findUserById(@NotNull String pattern) {
        return findUsersByAttribute("id", pattern);
    }

    public List<PlayerFX> findUserByName(@NotNull String pattern) {
        return findUsersByAttribute("login", pattern);
    }

    public List<PlayerFX> findUserByEmail(@NotNull String pattern) {
        return findUsersByAttribute("email", pattern);
    }

    public List<PlayerFX> findUserBySteamId(@NotNull String pattern) {
        return findUsersByAttribute("steamId", pattern);
    }

    public List<PlayerFX> findUserByIP(@NotNull String pattern) {
        return findUsersByAttribute("recentIpAddress", pattern);
    }

    public List<PlayerFX> findUsersByPreviousName(@NotNull String pattern) {
        log.debug("Searching for player by previous name with pattern: {}", pattern);
        ElideRouteBuilder<NameRecord> routeBuilder = ElideRouteBuilder.of(NameRecord.class)
                .addInclude("player")
                .filter(ElideRouteBuilder.qBuilder().string("name").eq(pattern));
        addModeratorIncludes(routeBuilder, "player");

        List<NameRecord> result = fafApi.getAll(routeBuilder);
        log.trace("found {} name records", result.size());
        return result.stream()
                .map(NameRecord::getPlayer)
                .distinct()
                .map(playerMapper::map)
                .collect(Collectors.toList());
    }

    public List<TeamkillFX> findLatestTeamkills() {
        log.debug("Searching for latest teamkills ");
        ElideRouteBuilder<Teamkill> routeBuilder = ElideRouteBuilder.of(Teamkill.class)
                .addInclude("teamkiller")
                .addInclude("teamkiller.bans")
                .addInclude("victim")
                .sort("id", false);

        List<Teamkill> result = fafApi.getPage(routeBuilder, 100, 1, Collections.emptyMap());
        log.trace("found {} teamkills", result.size());
        return teamkillMapper.map(result);
    }

    public List<TeamkillFX> findTeamkillsByUserId(@NotNull String userId) {
        log.debug("Searching for teamkills invoked by player id: {}", userId);
        ElideRouteBuilder<Teamkill> routeBuilder = ElideRouteBuilder.of(Teamkill.class)
                .addInclude("teamkiller")
                .addInclude("victim")
                .filter(ElideRouteBuilder.qBuilder().string("teamkiller.id").eq(userId));

        List<Teamkill> result = fafApi.getAll(routeBuilder);
        log.trace("found {} teamkills", result.size());
        return teamkillMapper.map(result);
    }

    public List<GamePlayerStats> getLastHundredPlayedGamesByFeaturedMod(@NotNull String userId, int page, FeaturedModFX featuredModFX) {
        log.debug("Searching for games played by player id: {}", userId);
        ElideRouteBuilder<GamePlayerStats> routeBuilder = ElideRouteBuilder.of(GamePlayerStats.class)
                .addInclude("game")
                .addInclude("player")
                .addInclude("game.host")
                .addInclude("game.featuredMod")
                .addInclude("game.mapVersion")
                .addInclude("game.mapVersion.map")
                .sort("scoreTime", false);
        if (featuredModFX != null) {
            routeBuilder.filter(ElideRouteBuilder.qBuilder().string("game.featuredMod.technicalName").eq(featuredModFX.getTechnicalName())
                    .and().string("player.id").eq(userId));
        } else {
            routeBuilder.filter(ElideRouteBuilder.qBuilder().string("player.id").eq(userId));
        }
        return fafApi.getPage(routeBuilder, 100, page, Collections.emptyMap());
    }

    public List<GamePlayerStats> getLastHundredPlayedGames(@NotNull String userId, int page) {
        return getLastHundredPlayedGamesByFeaturedMod(userId, page, null);
    }

    public List<FeaturedModFX> getFeaturedMods() {
        ElideRouteBuilder<FeaturedMod> routeBuilder = ElideRouteBuilder.of(FeaturedMod.class);
        return featuredModMapper.map(fafApi.getAll(routeBuilder));
    }

    public UserNoteFX getUserNoteById(@NotNull String userNoteId) {
        log.debug("Search for player note id: " + userNoteId);
        ElideRouteBuilder<UserNote> routeBuilder = ElideRouteBuilder.of(UserNote.class)
                .id(userNoteId)
                .addInclude("player")
                .addInclude("author");
        return userNoteMapper.map(fafApi.getOne(routeBuilder));
    }

    public List<UserNoteFX> getUserNotes(@NotNull String userId) {
        log.debug("Search for all note of player id: " + userId);
        ElideRouteBuilder<UserNote> routeBuilder = ElideRouteBuilder.of(UserNote.class)
                .filter(ElideRouteBuilder.qBuilder().string("player.id").eq(userId))
                .addInclude("player")
                .addInclude("author");
        return userNoteMapper.map(fafApi.getAll(routeBuilder));
    }

    public String createUserNote(UserNote userNote) {
        log.debug("Creating userNote");
        userNote.setAuthor(fafApi.getSelfPlayer());
        return fafApi.post(ElideRouteBuilder.of(UserNote.class), userNote).getId();
    }

    public UserNoteFX patchUserNote(UserNote userNote) {
        log.debug("Patching UserNote of id: " + userNote.getId());
        return userNoteMapper.map(fafApi.patch(ElideRouteBuilder.of(UserNote.class).id(userNote.getId()), userNote));
    }
}
