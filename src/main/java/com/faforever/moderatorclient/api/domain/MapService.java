package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.Ladder1v1Map;
import com.faforever.commons.api.dto.Map;
import com.faforever.commons.api.dto.MapVersion;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.commons.api.elide.ElideNavigatorOnCollection;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MapService {
    private final FafApiCommunicationService fafApi;

    public MapService(FafApiCommunicationService fafApi) {
        this.fafApi = fafApi;
    }


    private List<Map> findMapsByAttribute(@NotNull String attribute, @NotNull String pattern, boolean excludeHidden) {
        log.debug("Searching for maps by attribute '{}' with pattern: {}", attribute, pattern);
        ElideNavigatorOnCollection<MapVersion> routeBuilder = ElideNavigator.of(MapVersion.class)
                .collection()
                .addIncludeOnCollection("map")
                .addIncludeOnCollection("map.author");

        if (excludeHidden) {
            routeBuilder.addFilter(ElideNavigator.qBuilder().string("map." + attribute).eq(pattern)
                    .and().bool("hidden").isFalse());
        } else {
            routeBuilder.addFilter(ElideNavigator.qBuilder().string("map." + attribute).eq(pattern));
        }

        List<Map> result = fafApi.getAll(routeBuilder).stream()
                .map(MapVersion::getMap)
                .distinct()
                .collect(Collectors.toList());

        // filter empty mapVersions that were created by map relationships
        for (Map map : result) {
            for (MapVersion mapVersion : new ArrayList<>(map.getVersions())) {
                if (mapVersion.getMap() == null) {
                    map.getVersions().remove(mapVersion);
                }
            }
        }

        log.trace("found {} maps", result.size());
        return result;
    }

    public List<Map> findMapsByName(@NotNull String pattern, boolean excludeHidden) {
        return findMapsByAttribute("displayName", pattern, excludeHidden);
    }

    public List<Map> findMapsByAuthorId(@NotNull String pattern, boolean excludeHidden) {
        return findMapsByAttribute("author.id", pattern, excludeHidden);
    }

    public List<Map> findMapsByAuthorName(@NotNull String pattern, boolean excludeHidden) {
        return findMapsByAttribute("author.login", pattern, excludeHidden);
    }

    public List<Map> findMaps(String mapNamePattern) {
        log.debug("Searching for maps with pattern: {}", mapNamePattern);
        ElideNavigatorOnCollection<Map> routeBuilder = ElideNavigator.of(Map.class)
                .collection()
                .addIncludeOnCollection("versions");

        if (mapNamePattern != null && mapNamePattern.length() > 0) {
            routeBuilder.addFilter(ElideNavigator.qBuilder().string("displayName").eq(mapNamePattern));
        }

        List<Map> result = fafApi.getAll(routeBuilder);
        log.trace("found {} maps", result.size());
        return result;
    }

    public List<Map> findMapsInLadder1v1Pool() {
        log.debug("Searching for all ladder1v1 maps");
        List<Ladder1v1Map> ladder1v1Maps = fafApi.getAll(
                ElideNavigator.of(Ladder1v1Map.class)
                        .collection()
                        .addIncludeOnCollection("mapVersion")
                        .addIncludeOnCollection("mapVersion.map"));

        ladder1v1Maps.forEach(ladder1v1Map -> ladder1v1Map.getMapVersion().setLadder1v1Map(ladder1v1Map));

        List<Map> result = ladder1v1Maps.stream()
                .map(Ladder1v1Map::getMapVersion)
                .map(MapVersion::getMap)
                .collect(Collectors.toList());
        log.trace("found {} maps", result.size());
        return result;
    }

    public void removeMapVersionFromLadderPool(MapVersion mapVersion) {
        log.debug("Deleting mapVersion from ladder pool: {}", mapVersion.getId());
        fafApi.delete(ElideNavigator.of(Ladder1v1Map.class).id(mapVersion.getId()));
    }

    public void addMapVersionToLadderPool(MapVersion mapVersion) {
        log.debug("Adding mapVersion to ladder pool: {}", mapVersion.getId());
        fafApi.post(ElideNavigator.of(Ladder1v1Map.class).collection(),
                new Ladder1v1Map().setMapVersion(mapVersion));
    }

    public void patchMapVersion(MapVersion mapVersion) {
        log.debug("Updating mapVersion id: {}", mapVersion.getId());
        fafApi.patch(ElideNavigator.of(mapVersion),
                (MapVersion) new MapVersion()
                        .setHidden(mapVersion.isHidden())
                        .setRanked(mapVersion.isRanked())
                        .setId(mapVersion.getId()
                        ));
    }

    public boolean doesMapVersionExist(int id) {
        log.debug("Requesting Mapversion with id: {}", id);
        return !fafApi.getAll(ElideRouteBuilder.of(MapVersion.class)
                .filter(ElideRouteBuilder.qBuilder().string("id").eq(String.valueOf(id))))
                .isEmpty();
    }
}
