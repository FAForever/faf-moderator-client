package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public class MapPoolFX extends AbstractEntityFX {

    private final StringProperty name;
    private final ObservableList<MapPoolAssignmentFX> mapPoolAssignments;

    public MapPoolFX() {
        name = new SimpleStringProperty();
        mapPoolAssignments = FXCollections.observableArrayList();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public MapPoolFX setName(String name) {
        this.name.set(name);
        return this;
    }

    public ObservableList<MapPoolAssignmentFX> getMapPoolAssignments() {
        return mapPoolAssignments;
    }

    public MapPoolFX setMapPoolAssignments(List<MapPoolAssignmentFX> mapPoolAssignmentsFX) {
        if (mapPoolAssignmentsFX != null) {
            this.mapPoolAssignments.setAll(mapPoolAssignmentsFX);
        }
        return this;
    }
}
