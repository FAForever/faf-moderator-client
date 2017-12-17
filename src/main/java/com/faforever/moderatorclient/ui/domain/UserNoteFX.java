package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserNoteFX extends AbstractEntityFX {
    private final ObjectProperty<PlayerFX> player;
    private final ObjectProperty<PlayerFX> author;
    private final SimpleBooleanProperty watched;
    private final SimpleStringProperty note;

    public UserNoteFX() {
        player = new SimpleObjectProperty<>();
        author = new SimpleObjectProperty<>();
        watched = new SimpleBooleanProperty();
        note = new SimpleStringProperty();
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

    public PlayerFX getAuthor() {
        return author.get();
    }

    public void setAuthor(PlayerFX author) {
        this.author.set(author);
    }

    public ObjectProperty<PlayerFX> authorProperty() {
        return author;
    }

    public boolean isWatched() {
        return watched.get();
    }

    public void setWatched(boolean watched) {
        this.watched.set(watched);
    }

    public SimpleBooleanProperty watchedProperty() {
        return watched;
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }
}
