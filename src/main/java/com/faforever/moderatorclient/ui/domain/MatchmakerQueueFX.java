package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class MatchmakerQueueFX extends AbstractEntityFX {
    private final StringProperty technicalName;
    private final StringProperty nameKey;
    private final IntegerProperty teamSize;

    public MatchmakerQueueFX() {
        technicalName = new SimpleStringProperty();
        nameKey = new SimpleStringProperty();
        teamSize = new SimpleIntegerProperty();
    }

    public String getTechnicalName() {
        return technicalName.get();
    }

    public StringProperty technicalNameProperty() {
        return technicalName;
    }

    public void setTechnicalName(String technicalName) {
        this.technicalName.set(technicalName);
    }

    public String getNameKey() {
        return nameKey.get();
    }

    public StringProperty nameKeyProperty() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey.set(nameKey);
    }

    public int getTeamSize() {
        return teamSize.get();
    }

    public IntegerProperty teamSizeProperty() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize.set(teamSize);
    }
}
