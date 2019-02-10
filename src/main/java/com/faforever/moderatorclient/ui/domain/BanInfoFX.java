package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.BanDurationType;
import com.faforever.commons.api.dto.BanLevel;
import com.faforever.commons.api.dto.BanStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.OffsetDateTime;

public class BanInfoFX extends AbstractEntityFX {
    private final ObjectProperty<PlayerFX> player;
    private final ObjectProperty<PlayerFX> author;
    private final StringProperty reason;
    private final ObjectProperty<OffsetDateTime> expiresAt;
    private final ObjectProperty<BanLevel> level;
    private final ObjectProperty<BanDurationType> duration;
    private final ObjectProperty<BanStatus> banStatus;
    private final StringProperty revokeReason;
    private final ObjectProperty<PlayerFX> revokeAuthor;
    private final ObjectProperty<OffsetDateTime> revokeTime;
    private final ObjectProperty<ModerationReportFX> moderationReport;

    public BanInfoFX() {
        player = new SimpleObjectProperty<>();
        author = new SimpleObjectProperty<>();
        reason = new SimpleStringProperty();
        expiresAt = new SimpleObjectProperty<>();
        level = new SimpleObjectProperty<>();

        revokeAuthor = new SimpleObjectProperty<>();
        revokeReason = new SimpleStringProperty();
        revokeTime = new SimpleObjectProperty<>();

        duration = new SimpleObjectProperty<>();
        duration.bind(Bindings.createObjectBinding(() -> expiresAt.get() == null ? BanDurationType.PERMANENT : BanDurationType.TEMPORARY, expiresAt));

        banStatus = new SimpleObjectProperty<>();
        banStatus.bind(
                Bindings.createObjectBinding(() -> {
                            if (getRevokeTime() != null && getRevokeTime().isBefore(OffsetDateTime.now())) {
                                return BanStatus.DISABLED;
                            }
                            if (getDuration() == BanDurationType.PERMANENT) {
                                return BanStatus.BANNED;
                            }
                            return getExpiresAt().isAfter(OffsetDateTime.now())
                                    ? BanStatus.BANNED
                                    : BanStatus.EXPIRED;
                        },
                        revokeTime, duration, expiresAt)
        );
        moderationReport = new SimpleObjectProperty<>();
    }

    public PlayerFX getPlayer() {
        return player.get();
    }

    public BanInfoFX setPlayer(PlayerFX player) {
        this.player.set(player);
        return this;
    }

    public ObjectProperty<PlayerFX> playerProperty() {
        return player;
    }

    public PlayerFX getAuthor() {
        return author.get();
    }

    public BanInfoFX setAuthor(PlayerFX author) {
        this.author.set(author);
        return this;
    }

    public ObjectProperty<PlayerFX> authorProperty() {
        return author;
    }

    public String getReason() {
        return reason.get();
    }

    public BanInfoFX setReason(String reason) {
        this.reason.set(reason);
        return this;
    }

    public StringProperty reasonProperty() {
        return reason;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt.get();
    }

    public BanInfoFX setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt.set(expiresAt);
        return this;
    }

    public ObjectProperty<OffsetDateTime> expiresAtProperty() {
        return expiresAt;
    }

    public BanLevel getLevel() {
        return level.get();
    }

    public BanInfoFX setLevel(BanLevel level) {
        this.level.set(level);
        return this;
    }

    public ObjectProperty<BanLevel> levelProperty() {
        return level;
    }

    public BanDurationType getDuration() {
        return duration.get();
    }

    public ObjectProperty<BanDurationType> durationProperty() {
        return duration;
    }

    public BanStatus getBanStatus() {
        return banStatus.get();
    }

    public ObjectProperty<BanStatus> banStatusProperty() {
        return banStatus;
    }

    public String getRevokeReason() {
        return revokeReason.get();
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason.set(revokeReason);
    }

    public StringProperty revokeReasonProperty() {
        return revokeReason;
    }

    public PlayerFX getRevokeAuthor() {
        return revokeAuthor.get();
    }

    public void setRevokeAuthor(PlayerFX revokeAuthor) {
        this.revokeAuthor.set(revokeAuthor);
    }

    public ObjectProperty<PlayerFX> revokeAuthorProperty() {
        return revokeAuthor;
    }

    public OffsetDateTime getRevokeTime() {
        return revokeTime.get();
    }

    public void setRevokeTime(OffsetDateTime revokeTime) {
        this.revokeTime.set(revokeTime);
    }

    public ObjectProperty<OffsetDateTime> revokeTimeProperty() {
        return revokeTime;
    }

    public ModerationReportFX getModerationReport() {
        return moderationReport.get();
    }

    public void setModerationReport(ModerationReportFX moderationReport) {
        this.moderationReport.set(moderationReport);
    }

    public ObjectProperty<ModerationReportFX> moderationReportProperty() {
        return moderationReport;
    }
}
