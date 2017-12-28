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
    private final ObjectProperty<BanRevokeDataFX> banRevokeData;

    public BanInfoFX() {
        player = new SimpleObjectProperty<>();
        author = new SimpleObjectProperty<>();
        reason = new SimpleStringProperty();
        expiresAt = new SimpleObjectProperty<>();
        level = new SimpleObjectProperty<>();
        banRevokeData = new SimpleObjectProperty<>();

        duration = new SimpleObjectProperty<>();
        duration.bind(Bindings.createObjectBinding(() -> expiresAt.get() == null ? BanDurationType.PERMANENT : BanDurationType.TEMPORARY, expiresAt));

        banStatus = new SimpleObjectProperty<>();
        banStatus.bind(
                Bindings.createObjectBinding(() -> {
                            if (banRevokeData.get() != null) {
                                return BanStatus.DISABLED;
                            } else if (duration.get() == BanDurationType.PERMANENT) {
                                return BanStatus.BANNED;
                            } else {
                                return expiresAt.get().isAfter(OffsetDateTime.now()) ? BanStatus.BANNED : BanStatus.EXPIRED;
                            }
                        },
                        banRevokeData, duration, expiresAt)
        );
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

    public BanRevokeDataFX getBanRevokeData() {
        return banRevokeData.get();
    }

    public BanInfoFX setBanRevokeData(BanRevokeDataFX banRevokeData) {
        this.banRevokeData.set(banRevokeData);
        return this;
    }

    public ObjectProperty<BanRevokeDataFX> banRevokeDataProperty() {
        return banRevokeData;
    }
}
