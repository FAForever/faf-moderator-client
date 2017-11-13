package com.faforever.moderatorclient.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Type("avatar")
@Getter
@Setter
public class Avatar extends AbstractEntity {
    private String url;
    private String tooltip;
    @Relationship("assignments")
    @JsonIgnore
    private List<AvatarAssignment> assignments;
}
