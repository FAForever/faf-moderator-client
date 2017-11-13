package com.faforever.moderatorclient.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Type("avatarAssignment")
@Getter
@Setter
public class AvatarAssignment extends AbstractEntity {
    private boolean selected;
    private OffsetDateTime expiresAt;
    @Relationship("player")
    @JsonIgnore
    private Player player;
    @Relationship("avatar")
    @JsonIgnore
    private Avatar avatar;
}
