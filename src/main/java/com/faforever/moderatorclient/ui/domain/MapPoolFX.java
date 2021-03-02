package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MapPoolFX extends AbstractEntityFX {

    private final StringProperty name;
    private final ObservableList<MapVersionFX> mapVersions;

    public MapPoolFX() {
        name = new SimpleStringProperty();
        mapVersions = FXCollections.observableArrayList();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public String toString() {
        return "MapPoolFX{" +
                "name=" + name +
                ", maps=" + mapVersions +
                '}';
    }
}
