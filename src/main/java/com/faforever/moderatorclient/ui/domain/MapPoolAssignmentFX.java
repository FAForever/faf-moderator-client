package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.MapParams;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MapPoolAssignmentFX extends AbstractEntityFX {

    private final ObjectProperty<MapPoolFX> mapPool;
    private final ObjectProperty<MapVersionFX> mapVersion;
    private final IntegerProperty weight;
    private final ObjectProperty<MapParams> mapParams;

    public MapPoolAssignmentFX() {
        mapPool = new SimpleObjectProperty<>();
        mapVersion = new SimpleObjectProperty<>();
        weight = new SimpleIntegerProperty();
        mapParams = new SimpleObjectProperty<>();
    }

    public MapParams getMapParams() {
        return mapParams.get();
    }

    public ObjectProperty<MapParams> mapParamsProperty() {
        return mapParams;
    }

    public MapPoolAssignmentFX setMapParams(MapParams mapParams) {
        this.mapParams.set(mapParams);
        return this;
    }

    public Integer getWeight() {
        return weight.get();
    }

    public IntegerProperty weightProperty() {
        return weight;
    }

    public MapPoolAssignmentFX setWeight(Integer weight) {
        this.weight.set(weight);
        return this;
    }

    public MapVersionFX getMapVersion() {
        return mapVersion.get();
    }

    public ObjectProperty<MapVersionFX> mapVersionProperty() {
        return mapVersion;
    }

    public MapPoolAssignmentFX setMapVersion(MapVersionFX mapVersion) {
        this.mapVersion.set(mapVersion);
        return this;
    }
    
    public MapPoolFX getMapPool() {
        return mapPool.get();
    }

    public ObjectProperty<MapPoolFX> mapPoolProperty() {
        return mapPool;
    }

    public MapPoolAssignmentFX setMapPool(MapPoolFX mapPool) {
        this.mapPool.set(mapPool);
        return this;
    }
}
