package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.Player;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.OffsetDateTime;

public class NameRecordFX {
    private final StringProperty id;
    private final ObjectProperty<OffsetDateTime> changeTime;
    private final ObjectProperty<Player> player;
    private final StringProperty name;

    public NameRecordFX() {
        id = new SimpleStringProperty();
        changeTime = new SimpleObjectProperty<>();
        player = new SimpleObjectProperty<>();
        name = new SimpleStringProperty();
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public OffsetDateTime getChangeTime() {
        return changeTime.get();
    }

    public void setChangeTime(OffsetDateTime changeTime) {
        this.changeTime.set(changeTime);
    }

    public ObjectProperty<OffsetDateTime> changeTimeProperty() {
        return changeTime;
    }

    public Player getPlayer() {
        return player.get();
    }

    public void setPlayer(Player player) {
        this.player.set(player);
    }

    public ObjectProperty<Player> playerProperty() {
        return player;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }
}
