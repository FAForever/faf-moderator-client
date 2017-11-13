package com.faforever.moderatorclient.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Type("banInfo")
@RestrictedVisibility("HasBanRead")
@Getter
@Setter
public class BanInfo extends AbstractEntity {
    @Relationship("player")
    @JsonIgnore
    private Player player;
    @Relationship("author")
    @JsonIgnore
    private Player author;
    private String reason;
    private OffsetDateTime expiresAt;
    private BanLevel level;
    @Relationship("banRevokeData")
    @JsonIgnore
    private BanRevokeData banRevokeData;

    @JsonIgnore
    public BanDurationType getDuration() {
        return expiresAt == null ? BanDurationType.PERMANENT : BanDurationType.TEMPORARY;
    }

    @JsonIgnore
    public BanStatus getBanStatus() {
        if (banRevokeData != null) {
            return BanStatus.DISABLED;
        }
        if (getDuration() == BanDurationType.PERMANENT) {
            return BanStatus.BANNED;
        }
        return expiresAt.isAfter(OffsetDateTime.now())
                ? BanStatus.BANNED
                : BanStatus.EXPIRED;
    }
}
