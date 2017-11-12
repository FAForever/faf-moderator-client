package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Type("teamkill")
@RestrictedVisibility("IsModerator")
public class Teamkill {
    @Id
    private String id;
    @Relationship("teamkiller")
    private Player teamkiller;
    @Relationship("victim")
    private Player victim;
    @Relationship("game")
    private Game game;
    private long gameTime;
    private OffsetDateTime reportedAt;
}
