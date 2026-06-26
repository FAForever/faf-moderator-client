package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.OffsetDateTime;

public class AvatarAssignmentFX extends AbstractEntityFX {
    private final ObjectProperty<OffsetDateTime> expiresAt;
    private final ObjectProperty<PlayerFX> player;
    private final ObjectProperty<AvatarFX> avatar;

    public AvatarAssignmentFX() {
        expiresAt = new SimpleObjectProperty<>();
        player = new SimpleObjectProperty<>();
        avatar = new SimpleObjectProperty<>();
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt.get();
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt.set(expiresAt);
    }

    public ObjectProperty<OffsetDateTime> expiresAtProperty() {
        return expiresAt;
    }

    public PlayerFX getPlayer() {
        return player.get();
    }

    public void setPlayer(PlayerFX player) {
        this.player.set(player);
    }

    public ObjectProperty<PlayerFX> playerProperty() {
        return player;
    }

    public AvatarFX getAvatar() {
        return avatar.get();
    }

    public void setAvatar(AvatarFX avatar) {
        this.avatar.set(avatar);
    }

    public ObjectProperty<AvatarFX> avatarProperty() {
        return avatar;
    }
}
