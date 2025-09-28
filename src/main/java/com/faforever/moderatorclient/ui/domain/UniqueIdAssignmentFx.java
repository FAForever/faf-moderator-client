package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UniqueIdAssignmentFx extends AbstractEntityFX {
    private final ObjectProperty<UniqueIdFx> uniqueId = new SimpleObjectProperty<>();

    public UniqueIdFx getUniqueId() {
        return uniqueId.get();
    }

    public ObjectProperty<UniqueIdFx> uniqueIdProperty() {
        return uniqueId;
    }

    public void setUniqueId(UniqueIdFx uniqueId) {
        this.uniqueId.set(uniqueId);
    }
}
