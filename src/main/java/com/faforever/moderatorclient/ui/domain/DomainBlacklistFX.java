package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DomainBlacklistFX {
    private final StringProperty domain;

    public DomainBlacklistFX() {
        domain = new SimpleStringProperty();
    }

    public String getDomain() {
        return domain.get();
    }

    public void setDomain(String domain) {
        this.domain.set(domain);
    }

    public StringProperty domainProperty() {
        return domain;
    }
}
