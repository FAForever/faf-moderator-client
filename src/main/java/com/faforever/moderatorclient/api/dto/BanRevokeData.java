package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;

@Type("banRevokeData")
@Getter
@RestrictedVisibility("HasBanRead")
public class BanRevokeData extends AbstractEntity {
    @Relationship("ban")
    private BanInfo ban;
    private String reason;
    @Relationship("author")
    private Player author;
}
