package com.faforever.moderatorclient.common;

import com.faforever.commons.api.dto.AbstractEntity;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("leaderboard")
public class Leaderboard extends AbstractEntity {

    private String descriptionKey;
    private String nameKey;
    private String technicalName;
}
