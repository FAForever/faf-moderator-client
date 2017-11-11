package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Type("mapVersion")
public class MapVersion {

    @Id
    private String id;
    private String description;
    private int maxPlayers;
    private int width;
    private int height;
    private ComparableVersion version;
    private String folderName;
    // TODO name consistently with folderName
    private String filename;
    private boolean ranked;
    private boolean hidden;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private URL thumbnailUrlSmall;
    private URL thumbnailUrlLarge;
    private URL downloadUrl;

    @Relationship("map")
    private Map map;

    @Relationship("statistics")
    private MapVersionStatistics statistics;

    @Nullable
    @Relationship("ladder1v1Map")
    private Ladder1v1Map ladder1v1Map;

    @Relationship("reviews")
    private List<MapVersionReview> reviews;
}
