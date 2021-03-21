package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class MatchmakerQueueFX extends AbstractEntityFX {
    private final StringProperty technicalName;
    private final StringProperty nameKey;

    public MatchmakerQueueFX() {
        technicalName = new SimpleStringProperty();
        nameKey = new SimpleStringProperty();
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
}
