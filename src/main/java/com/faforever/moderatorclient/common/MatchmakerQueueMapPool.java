package com.faforever.moderatorclient.common;

import com.faforever.commons.api.dto.AbstractEntity;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Type("matchmakerQueueMapPool")
public class MatchmakerQueueMapPool extends AbstractEntity {
    private Double minRating;
    private Double maxRating;

    @Relationship("matchmakerQueue")
    private MatchmakerQueue matchmakerQueue;

    @Relationship("mapPool")
    private MapPool mapPool;
}
