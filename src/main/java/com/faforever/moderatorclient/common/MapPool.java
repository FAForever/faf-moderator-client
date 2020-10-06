package com.faforever.moderatorclient.common;

import com.faforever.commons.api.dto.AbstractEntity;
import com.faforever.commons.api.dto.MapVersion;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Type("mapPool")
public class MapPool extends AbstractEntity {

    private String name;

    @Relationship("mapVersions")
    private List<MapVersion> mapVersions;
}
