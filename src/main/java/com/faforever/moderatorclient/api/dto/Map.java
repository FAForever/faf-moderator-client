package com.faforever.moderatorclient.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Type("map")
public class Map {

    @Id
    private String id;
    private String battleType;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private String displayName;
    private String mapType;

    @Relationship("author")
    private Player author;

    @Relationship("statistics")
    private MapStatistics statistics;

    @Relationship("latestVersion")
    @JsonIgnore
    private MapVersion latestVersion;

    @Relationship("versions")
    @JsonIgnore
    private List<MapVersion> versions;
}
