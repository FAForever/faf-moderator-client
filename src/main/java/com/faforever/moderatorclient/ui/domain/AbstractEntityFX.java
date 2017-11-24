package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.OffsetDateTime;

public abstract class AbstractEntityFX {
    private boolean dirty;
    private final StringProperty id;
    private final ObjectProperty<OffsetDateTime> createTime;
    private final ObjectProperty<OffsetDateTime> updateTime;

    protected AbstractEntityFX() {
        id = new SimpleStringProperty();
        createTime = new SimpleObjectProperty<>();
        updateTime = new SimpleObjectProperty<>();
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        dirty = true;
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public OffsetDateTime getCreateTime() {
        return createTime.get();
    }

    public void setCreateTime(OffsetDateTime createTime) {
        dirty = true;
        this.createTime.set(createTime);
    }

    public ObjectProperty<OffsetDateTime> createTimeProperty() {
        return createTime;
    }

    public OffsetDateTime getUpdateTime() {
        return updateTime.get();
    }

    public void setUpdateTime(OffsetDateTime updateTime) {
        dirty = true;
        this.updateTime.set(updateTime);
    }

    public ObjectProperty<OffsetDateTime> updateTimeProperty() {
        return updateTime;
    }
}
