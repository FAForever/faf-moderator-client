package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.*;

public class FeaturedModFX {
    private final StringProperty id;
    private final StringProperty description;
    private final StringProperty displayName;
    private final IntegerProperty order;
    private final StringProperty gitBranch;
    private final StringProperty gitUrl;
    private final StringProperty bireusUrl;
    private final StringProperty technicalName;
    private final BooleanProperty visible;

    public FeaturedModFX() {
        this.id = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.displayName = new SimpleStringProperty();
        this.order = new SimpleIntegerProperty();
        this.gitBranch = new SimpleStringProperty();
        this.gitUrl = new SimpleStringProperty();
        this.bireusUrl = new SimpleStringProperty();
        this.technicalName = new SimpleStringProperty();
        this.visible = new SimpleBooleanProperty();
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    public int getOrder() {
        return order.get();
    }

    public void setOrder(int order) {
        this.order.set(order);
    }

    public IntegerProperty orderProperty() {
        return order;
    }

    public String getGitBranch() {
        return gitBranch.get();
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch.set(gitBranch);
    }

    public StringProperty gitBranchProperty() {
        return gitBranch;
    }

    public String getGitUrl() {
        return gitUrl.get();
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl.set(gitUrl);
    }

    public StringProperty gitUrlProperty() {
        return gitUrl;
    }

    public String getBireusUrl() {
        return bireusUrl.get();
    }

    public void setBireusUrl(String bireusUrl) {
        this.bireusUrl.set(bireusUrl);
    }

    public StringProperty bireusUrlProperty() {
        return bireusUrl;
    }

    public String getTechnicalName() {
        return technicalName.get();
    }

    public void setTechnicalName(String technicalName) {
        this.technicalName.set(technicalName);
    }

    public StringProperty technicalNameProperty() {
        return technicalName;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }
}
