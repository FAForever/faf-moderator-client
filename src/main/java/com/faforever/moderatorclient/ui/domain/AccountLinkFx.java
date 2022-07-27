package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.LinkedServiceType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AccountLinkFx {
    private final StringProperty id = new SimpleStringProperty();
    private final ObjectProperty<LinkedServiceType> serviceType = new SimpleObjectProperty<>();
    private final StringProperty serviceId = new SimpleStringProperty();
    private final BooleanProperty public_ = new SimpleBooleanProperty();
    private final BooleanProperty ownership = new SimpleBooleanProperty();

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public LinkedServiceType getServiceType() {
        return serviceType.get();
    }

    public ObjectProperty<LinkedServiceType> serviceTypeProperty() {
        return serviceType;
    }

    public void setServiceType(LinkedServiceType serviceType) {
        this.serviceType.set(serviceType);
    }

    public String getServiceId() {
        return serviceId.get();
    }

    public StringProperty serviceIdProperty() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId.set(serviceId);
    }

    public boolean isPublic() {
        return public_.get();
    }

    public BooleanProperty publicProperty() {
        return public_;
    }

    public void setPublic(boolean public_) {
        this.public_.set(public_);
    }

    public boolean isOwnership() {
        return ownership.get();
    }

    public BooleanProperty ownershipProperty() {
        return ownership;
    }

    public void setOwnership(boolean ownership) {
        this.ownership.set(ownership);
    }
}
