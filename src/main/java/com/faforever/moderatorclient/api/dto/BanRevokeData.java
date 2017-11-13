package com.faforever.moderatorclient.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Type("banRevokeData")
@Getter
@Setter
@RestrictedVisibility("HasBanRead")
public class BanRevokeData extends AbstractEntity {
    @Relationship("ban")
    @JsonIgnore
    private BanInfo ban;
    private String reason;
    @Relationship("author")
    @JsonIgnore
    private Player author;
}
