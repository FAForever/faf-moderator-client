package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class GroupPermissionFX extends AbstractEntityFX{
    @ToString.Include
    StringProperty technicalName = new SimpleStringProperty();
    StringProperty nameKey = new SimpleStringProperty();
    ObservableSet<UserGroupFX> userGroups = FXCollections.observableSet();

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

    public ObservableSet<UserGroupFX> getUserGroups() {
        return userGroups;
    }
}
