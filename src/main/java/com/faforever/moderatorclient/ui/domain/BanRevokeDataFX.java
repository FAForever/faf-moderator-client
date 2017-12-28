package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BanRevokeDataFX extends AbstractEntityFX {
    private final ObjectProperty<BanInfoFX> ban;
    private final StringProperty reason;
    private final ObjectProperty<PlayerFX> author;

    public BanRevokeDataFX() {
        ban = new SimpleObjectProperty<>();
        reason = new SimpleStringProperty();
        author = new SimpleObjectProperty<>();
    }

    public BanInfoFX getBan() {
        return ban.get();
    }

    public BanRevokeDataFX setBan(BanInfoFX ban) {
        this.ban.set(ban);
        return this;
    }

    public ObjectProperty<BanInfoFX> banProperty() {
        return ban;
    }

    public String getReason() {
        return reason.get();
    }

    public BanRevokeDataFX setReason(String reason) {
        this.reason.set(reason);
        return this;
    }

    public StringProperty reasonProperty() {
        return reason;
    }

    public PlayerFX getAuthor() {
        return author.get();
    }

    public BanRevokeDataFX setAuthor(PlayerFX author) {
        this.author.set(author);
        return this;
    }

    public ObjectProperty<PlayerFX> authorProperty() {
        return author;
    }
}
