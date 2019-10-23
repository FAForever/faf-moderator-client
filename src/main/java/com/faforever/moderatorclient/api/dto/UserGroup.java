package com.faforever.moderatorclient.api.dto;

import com.faforever.commons.api.dto.AbstractEntity;
import com.faforever.commons.api.dto.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Type(UserGroup.TYPE_NAME)
public class UserGroup extends AbstractEntity {

    public static final String TYPE_NAME = "userGroup";

    private String technicalName;
    private String nameKey;
    private boolean public_;
    @Relationship("members")
    @JsonIgnore
    private Set<Player> members;
    @Relationship("permissions")
    private Set<GroupPermission> permissions;
}
