package com.faforever.moderatorclient.common;

import com.faforever.commons.api.dto.AbstractEntity;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("leaderboard")
public class Leaderboard extends AbstractEntity {

    private String technical_name;
    private String name_key;
    private String description_key;

}
