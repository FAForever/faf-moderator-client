package com.faforever.moderatorclient.search;

import com.faforever.moderatorclient.api.ElideRouteBuilder;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.api.dto.Ladder1v1Map;
import com.faforever.moderatorclient.api.dto.Map;
import com.faforever.moderatorclient.api.dto.MapVersion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapService {
    private final FafApiCommunicationService fafApi;

    public MapService(FafApiCommunicationService fafApi) {
        this.fafApi = fafApi;
    }

    public List<Map> findMaps(String mapNamePattern) {
        ElideRouteBuilder routeBuilder = new ElideRouteBuilder(Map.class);

        if (mapNamePattern != null && mapNamePattern.length() > 0) {
            routeBuilder.filter(ElideRouteBuilder.qBuilder().string("displayName").eq(mapNamePattern));
        }

        return fafApi.getAll(routeBuilder);
    }

    public List<Map> findMapsInLadder1v1Pool() {
        List<Ladder1v1Map> ladder1v1Maps = fafApi.getAll(
                new ElideRouteBuilder(Ladder1v1Map.class)
                        .addInclude("mapVersion")
                        .addInclude("mapVersion.map"));

        ladder1v1Maps.forEach(ladder1v1Map -> ladder1v1Map.getMapVersion().setLadder1v1Map(ladder1v1Map));

        return ladder1v1Maps.stream()
                .map(Ladder1v1Map::getMapVersion)
                .map(MapVersion::getMap)
                .collect(Collectors.toList());
    }

    public void removeMapVersionFromLadderPool(String mapVersionId) {
        fafApi.delete(new ElideRouteBuilder(Ladder1v1Map.class).id(mapVersionId));
    }

    public void addMapVersionToLadderPool(String mapVersionID) {
        fafApi.post(new ElideRouteBuilder(Ladder1v1Map.class), new Ladder1v1Map().setMapVersion(new MapVersion().setId(mapVersionID)), Ladder1v1Map.class);
    }
}
