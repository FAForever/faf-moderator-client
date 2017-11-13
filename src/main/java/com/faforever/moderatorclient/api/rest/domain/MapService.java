package com.faforever.moderatorclient.api.rest.domain;

import com.faforever.moderatorclient.api.dto.Ladder1v1Map;
import com.faforever.moderatorclient.api.dto.Map;
import com.faforever.moderatorclient.api.dto.MapVersion;
import com.faforever.moderatorclient.api.rest.ElideRouteBuilder;
import com.faforever.moderatorclient.api.rest.FafApiCommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MapService {
    private final FafApiCommunicationService fafApi;

    public MapService(FafApiCommunicationService fafApi) {
        this.fafApi = fafApi;
    }

    public List<Map> findMaps(String mapNamePattern) {
        log.debug("Searching for maps with pattern: {}", mapNamePattern);
        ElideRouteBuilder<Map> routeBuilder = ElideRouteBuilder.of(Map.class);

        if (mapNamePattern != null && mapNamePattern.length() > 0) {
            routeBuilder.filter(ElideRouteBuilder.qBuilder().string("displayName").eq(mapNamePattern));
        }

        List<Map> result = fafApi.getAll(routeBuilder);
        log.trace("found {} maps", result.size());
        return result;
    }

    public List<Map> findMapsInLadder1v1Pool() {
        log.debug("Searching for all ladder1v1 maps");
        List<Ladder1v1Map> ladder1v1Maps = fafApi.getAll(
                ElideRouteBuilder.of(Ladder1v1Map.class)
                        .addInclude("mapVersion")
                        .addInclude("mapVersion.map"));

        ladder1v1Maps.forEach(ladder1v1Map -> ladder1v1Map.getMapVersion().setLadder1v1Map(ladder1v1Map));

        List<Map> result = ladder1v1Maps.stream()
                .map(Ladder1v1Map::getMapVersion)
                .map(MapVersion::getMap)
                .collect(Collectors.toList());
        log.trace("found {} maps", result.size());
        return result;
    }

    public void removeMapVersionFromLadderPool(String mapVersionId) {
        log.debug("Deleting mapVersion from ladder pool: {}", mapVersionId);
        fafApi.delete(ElideRouteBuilder.of(Ladder1v1Map.class).id(mapVersionId));
    }

    public void addMapVersionToLadderPool(String mapVersionID) {
        log.debug("Adding mapVersion to ladder pool: {}", mapVersionID);
        fafApi.post(ElideRouteBuilder.of(Ladder1v1Map.class), new Ladder1v1Map().setMapVersion(new MapVersion().setId(mapVersionID)));
    }
}
