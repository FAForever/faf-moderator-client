package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("gameReview")
public class GameReview extends Review {

    @Relationship("game")
    private Game game;
}
