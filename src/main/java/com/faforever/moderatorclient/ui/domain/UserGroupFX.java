package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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
public class UserGroupFX extends AbstractEntityFX{
    @ToString.Include
    StringProperty technicalName = new SimpleStringProperty();
    StringProperty nameKey = new SimpleStringProperty();
    BooleanProperty public_ = new SimpleBooleanProperty();
    ObservableSet<PlayerFX> members = FXCollections.observableSet();
    ObservableSet<GroupPermissionFX> permissions = FXCollections.observableSet();
    ObservableSet<UserGroupFX> children = FXCollections.observableSet();
    ObjectProperty<UserGroupFX> parent = new SimpleObjectProperty<>();

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

    public boolean isPublic_() {
        return public_.get();
    }

    public BooleanProperty public_Property() {
        return public_;
    }

    public void setPublic_(boolean public_) {
        this.public_.set(public_);
    }

    public ObservableSet<PlayerFX> getMembers() {
        return members;
    }

    public ObservableSet<GroupPermissionFX> getPermissions() {
        return permissions;
    }

    public ObservableSet<UserGroupFX> getChildren() {
        return children;
    }

    public UserGroupFX getParent() {
        return parent.get();
    }

    public ObjectProperty<UserGroupFX> parentProperty() {
        return parent;
    }

    public void setParent(UserGroupFX parent) {
        this.parent.set(parent);
    }
}
