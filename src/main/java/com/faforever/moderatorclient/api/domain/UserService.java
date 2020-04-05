package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.FeaturedMod;
import com.faforever.commons.api.dto.GamePlayerStats;
import com.faforever.commons.api.dto.NameRecord;
import com.faforever.commons.api.dto.User;
import com.faforever.commons.api.dto.Teamkill;
import com.faforever.commons.api.dto.UserNote;
import com.faforever.commons.api.elide.ElideEntity;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.commons.api.elide.ElideNavigatorOnId;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.mapstruct.FeaturedModMapper;
import com.faforever.moderatorclient.mapstruct.UserMapper;
import com.faforever.moderatorclient.mapstruct.TeamkillMapper;
import com.faforever.moderatorclient.mapstruct.UserNoteMapper;
import com.faforever.moderatorclient.ui.domain.FeaturedModFX;
import com.faforever.moderatorclient.ui.domain.UserFX;
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
    private final UserMapper userMapper;
    private final FeaturedModMapper featuredModMapper;
    private final UserNoteMapper userNoteMapper;
    private final TeamkillMapper teamkillMapper;


    public UserService(FafApiCommunicationService fafApi, UserMapper userMapper, FeaturedModMapper featuredModMapper, UserNoteMapper userNoteMapper, TeamkillMapper teamkillMapper) {
        this.fafApi = fafApi;
        this.userMapper = userMapper;
        this.featuredModMapper = featuredModMapper;
        this.userNoteMapper = userNoteMapper;
        this.teamkillMapper = teamkillMapper;
    }

    private <T extends ElideEntity> ElideNavigatorOnCollection<T> addModeratorIncludes(@NotNull ElideNavigatorOnCollection<T> builder) {
        return addModeratorIncludes(builder, null);
    }

    private <T extends ElideEntity> ElideNavigatorOnCollection<T> addModeratorIncludes(@NotNull ElideNavigatorOnCollection<T> builder, String prefix) {
        String variablePrefix = "";

        if (prefix != null) {
            variablePrefix = prefix + ".";
        }

        return builder
                .addIncludeOnCollection(variablePrefix + "names")
                .addIncludeOnCollection(variablePrefix + "globalRating")
                .addIncludeOnCollection(variablePrefix + "ladder1v1Rating")
                .addIncludeOnCollection(variablePrefix + "avatarAssignments")
                .addIncludeOnCollection(variablePrefix + "avatarAssignments.avatar")
                .addIncludeOnCollection(variablePrefix + "bans")
                .addIncludeOnCollection(variablePrefix + "bans.author")
                .addIncludeOnCollection(variablePrefix + "bans.revokeAuthor");
    }

    public List<UserFX> findLatestRegistrations() {
        log.debug("Searching for latest registrations");
        ElideNavigatorOnCollection<User> navigator = ElideNavigator.of(User.class)
                .collection()
                .addIncludeOnCollection("bans")
                .addSortingRule("id", false)
                .pageSize(50);
        addModeratorIncludes(navigator);

        List<User> result = fafApi.getPage(User.class, navigator, 100, 1, Collections.emptyMap());
        log.trace("found {} users", result.size());
        return userMapper.mapToFx(result);
    }

    private List<UserFX> findUsersByAttribute(@NotNull String attribute, @NotNull String pattern) {
        log.debug("Searching for player by attribute '{}' with pattern: {}", attribute, pattern);
        ElideNavigatorOnCollection<User> navigator = ElideNavigator.of(User.class)
                .collection()
                .addFilter(ElideNavigator.qBuilder().string(attribute).eq(pattern));
        addModeratorIncludes(navigator);

        List<User> result = fafApi.getAll(User.class, navigator);
        log.trace("found {} users", result.size());
        return userMapper.mapToFx(result);
    }

    public List<UserFX> findUserById(@NotNull String pattern) {
        return findUsersByAttribute("id", pattern);
    }

    public List<UserFX> findUserByName(@NotNull String pattern) {
        return findUsersByAttribute("login", pattern);
    }

    public List<UserFX> findUserByEmail(@NotNull String pattern) {
        return findUsersByAttribute("email", pattern);
    }

    public List<UserFX> findUserBySteamId(@NotNull String pattern) {
        return findUsersByAttribute("steamId", pattern);
    }

    public List<UserFX> findUserByIP(@NotNull String pattern) {
        return findUsersByAttribute("recentIpAddress", pattern);
    }

    public List<UserFX> findUsersByPreviousName(@NotNull String pattern) {
        log.debug("Searching for player by previous name with pattern: {}", pattern);
        ElideNavigatorOnCollection<NameRecord> navigator = ElideNavigator.of(NameRecord.class)
                .collection()
                .addIncludeOnCollection("player")
                .addFilter(ElideNavigator.qBuilder().string("name").eq(pattern));
        addModeratorIncludes(navigator, "player");

        List<NameRecord> result = fafApi.getAll(NameRecord.class, navigator);
        log.trace("found {} name records", result.size());
        return List.of();
//        return result.stream()
//                .map(NameRecord::getPlayer)
//                .distinct()
//                .map(userMapper::map)
//                .collect(Collectors.toList());
    }

    public List<TeamkillFX> findLatestTeamkills() {
        log.debug("Searching for latest teamkills ");
        ElideNavigatorOnCollection<Teamkill> navigator = ElideNavigator.of(Teamkill.class)
                .collection()
                .addIncludeOnCollection("teamkiller")
                .addIncludeOnCollection("teamkiller.bans")
                .addIncludeOnCollection("victim")
                .addSortingRule("id", false);

        List<Teamkill> result = fafApi.getPage(Teamkill.class, navigator, 100, 1, Collections.emptyMap());
        log.trace("found {} teamkills", result.size());
        return teamkillMapper.map(result);
    }

    public List<TeamkillFX> findTeamkillsByUserId(@NotNull String userId) {
        log.debug("Searching for teamkills invoked by player id: {}", userId);
        ElideNavigatorOnCollection<Teamkill> navigator = ElideNavigator.of(Teamkill.class)
                .collection()
                .addIncludeOnCollection("teamkiller")
                .addIncludeOnCollection("victim")
                .addFilter(ElideNavigator.qBuilder().string("teamkiller.id").eq(userId));

        List<Teamkill> result = fafApi.getAll(Teamkill.class, navigator);
        log.trace("found {} teamkills", result.size());
        return teamkillMapper.map(result);
    }

    public List<GamePlayerStats> getLastHundredPlayedGamesByFeaturedMod(@NotNull String userId, int page, FeaturedModFX featuredModFX) {
        log.debug("Searching for games played by player id: {}", userId);
        ElideNavigatorOnCollection<GamePlayerStats> navigator = ElideNavigator.of(GamePlayerStats.class)
                .collection()
                .addIncludeOnCollection("game")
                .addIncludeOnCollection("player")
                .addIncludeOnCollection("game.host")
                .addIncludeOnCollection("game.featuredMod")
                .addIncludeOnCollection("game.mapVersion")
                .addIncludeOnCollection("game.mapVersion.map")
                .addSortingRule("scoreTime", false);
        if (featuredModFX != null) {
            navigator.addFilter(ElideNavigator.qBuilder().string("game.featuredMod.technicalName").eq(featuredModFX.getTechnicalName())
                    .and().string("player.id").eq(userId));
        } else {
            navigator.addFilter(ElideNavigator.qBuilder().string("player.id").eq(userId));
        }
        return fafApi.getPage(GamePlayerStats.class, navigator, 100, page, Collections.emptyMap());
    }

    public List<GamePlayerStats> getLastHundredPlayedGames(@NotNull String userId, int page) {
        return getLastHundredPlayedGamesByFeaturedMod(userId, page, null);
    }

    public List<FeaturedModFX> getFeaturedMods() {
        ElideNavigatorOnCollection<FeaturedMod> navigator = ElideNavigator.of(FeaturedMod.class)
                .collection();
        return featuredModMapper.map(fafApi.getAll(FeaturedMod.class, navigator));
    }

    public UserNoteFX getUserNoteById(@NotNull String userNoteId) {
        log.debug("Search for player note id: " + userNoteId);
        ElideNavigatorOnId<UserNote> navigator = ElideNavigator.of(UserNote.class)
                .id(userNoteId)
                .addIncludeOnId("player")
                .addIncludeOnId("author");
        return userNoteMapper.map(fafApi.getOne(navigator));
    }

    public List<UserNoteFX> getUserNotes(@NotNull String userId) {
        log.debug("Search for all note of player id: " + userId);
        ElideNavigatorOnCollection<UserNote> navigator = ElideNavigator.of(UserNote.class)
                .collection()
                .addFilter(ElideNavigator.qBuilder().string("player.id").eq(userId))
                .addIncludeOnCollection("player")
                .addIncludeOnCollection("author");
        return userNoteMapper.map(fafApi.getAll(UserNote.class, navigator));
    }

    public String createUserNote(UserNote userNote) {
        log.debug("Creating userNote");
        return fafApi.post(ElideNavigator.of(UserNote.class).collection(), userNote).getId();
    }

    public UserNoteFX patchUserNote(UserNote userNote) {
        log.debug("Patching UserNote of id: " + userNote.getId());
        return userNoteMapper.map(fafApi.patch(ElideNavigator.of(UserNote.class).id(userNote.getId()), userNote));
    }
}
