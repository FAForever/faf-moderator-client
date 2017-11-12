package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;

import java.time.OffsetDateTime;

@Type("banInfo")
@RestrictedVisibility("HasBanRead")
@Getter
public class BanInfo extends AbstractEntity {
    @Relationship("player")
    private Player player;
    @Relationship("author")
    private Player author;
    private String reason;
    private OffsetDateTime expiresAt;
    private BanLevel level;
    @Relationship("banRevokeData")
    private BanRevokeData banRevokeData;

    public BanDurationType getDuration() {
        return expiresAt == null ? BanDurationType.PERMANENT : BanDurationType.TEMPORARY;
    }

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
