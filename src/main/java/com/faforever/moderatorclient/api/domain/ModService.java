package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.Mod;
import com.faforever.commons.api.dto.ModVersion;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.mapstruct.ModMapper;
import com.faforever.moderatorclient.mapstruct.ModVersionMapper;
import com.faforever.moderatorclient.ui.domain.ModFX;
import com.faforever.moderatorclient.ui.domain.ModVersionFX;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModService {
    private final FafApiCommunicationService communicationService;
    private final ModMapper modMapper;
    private final ModVersionMapper modVersionMapper;


    private List<Mod> findModsByAttribute(@NotNull String attribute, @NotNull String pattern, boolean excludeHidden) {
        log.debug("Searching for mods by attribute '{}' with pattern: {}", attribute, pattern);
        ElideNavigatorOnCollection<ModVersion> routeBuilder = ElideNavigator.of(ModVersion.class)
                .collection()
                .addInclude("mod")
                .addInclude("mod.uploader");

        if (excludeHidden) {
            routeBuilder.setFilter(ElideNavigator.qBuilder().string("mod." + attribute).eq(pattern)
                    .and().bool("hidden").isFalse());
        } else {
            routeBuilder.setFilter(ElideNavigator.qBuilder().string("mod." + attribute).eq(pattern));
        }

        List<Mod> result = communicationService.getAll(ModVersion.class, routeBuilder).stream()
                .map(ModVersion::getMod)
                .distinct()
                .collect(Collectors.toList());

        // filter empty mapVersions that were created by map relationships
        for (Mod mod : result) {
            for (ModVersion modVersion : new ArrayList<>(mod.getVersions())) {
                if (modVersion.getMod() == null) {
                    mod.getVersions().remove(modVersion);
                }
            }
        }

        log.trace("found {} mods", result.size());
        return result;
    }

    public List<Mod> findModsByName(@NotNull String pattern, boolean excludeHidden) {
        return findModsByAttribute("displayName", pattern, excludeHidden);
    }

    public List<Mod> findModsByModVersionUid(@NotNull String pattern, boolean excludeHidden) {
        return findModsByAttribute("versions.uid", pattern, excludeHidden);
    }

    public List<Mod> findModsByAuthorId(@NotNull String pattern, boolean excludeHidden) {
        return findModsByAttribute("uploader.id", pattern, excludeHidden);
    }

    public List<Mod> findModsByAuthorName(@NotNull String pattern, boolean excludeHidden) {
        return findModsByAttribute("uploader.login", pattern, excludeHidden);
    }

    public List<Mod> findMods(String modNamePattern) {
        log.debug("Searching for mods with pattern: {}", modNamePattern);
        ElideNavigatorOnCollection<Mod> routeBuilder = ElideNavigator.of(Mod.class)
                .collection()
                .addInclude("versions");

        if (modNamePattern != null && modNamePattern.length() > 0) {
            routeBuilder.setFilter(ElideNavigator.qBuilder().string("displayName").eq(modNamePattern));
        }

        List<Mod> result = communicationService.getAll(Mod.class, routeBuilder);
        log.trace("found {} mods", result.size());
        return result;
    }

    public void patchMod(ModFX modFX) {
        patchMod(modMapper.map(modFX));
    }

    public void patchMod(Mod mod) {
        log.debug("Updating mod id: {}", mod.getId());
        communicationService.patch(ElideNavigator.of(mod),
                new Mod()
                        .setRecommended(mod.getRecommended())
                        .setId(mod.getId()
                        ));
    }

    public void patchModVersion(ModVersionFX modVersionFX) {
        patchModVersion(modVersionMapper.map(modVersionFX));
    }

    public void patchModVersion(ModVersion modVersion) {
        log.debug("Updating modVersion id: {}", modVersion.getId());
        communicationService.patch(ElideNavigator.of(modVersion),
                new ModVersion()
                        .setHidden(modVersion.getHidden())
                        .setRanked(modVersion.getRanked())
                        .setId(modVersion.getId()
                        ));
    }

    public boolean doesModVersionExist(int id) {
        log.debug("Requesting Modversion with id: {}", id);
        return !communicationService.getAll(ModVersion.class, ElideNavigator.of(ModVersion.class)
                .collection()
                .setFilter(ElideNavigator.qBuilder().string("id").eq(String.valueOf(id))))
                .isEmpty();
    }

    public List<ModVersionFX> findLatestModVersions() {
        log.debug("Searching for latest modVersions ");
        ElideNavigatorOnCollection<ModVersion> navigator = ElideNavigator.of(ModVersion.class)
                .collection()
                .addInclude("mod")
                .addInclude("mod.uploader")
                .addSortingRule("id", false);

        List<ModVersion> result = communicationService.getPage(ModVersion.class, navigator, 50, 1, Collections.emptyMap());
        log.trace("found {} modVersions", result.size());
        return modVersionMapper.mapToFX(result);
    }
}
