package com.faforever.moderatorclient.common;

import com.faforever.commons.api.dto.AbstractEntity;
import com.faforever.commons.api.dto.FeaturedMod;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("matchmakerQueue")
public class MatchmakerQueue extends AbstractEntity {

    private String technicalName;
    private String nameKey;

    @Relationship("featuredMod")
    private FeaturedMod featuredMod;

    @Relationship("leaderboard")
    private Leaderboard leaderboard;

}
