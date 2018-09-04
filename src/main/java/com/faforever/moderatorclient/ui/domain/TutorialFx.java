package com.faforever.moderatorclient.ui.domain;

import javafx.beans.property.*;

public class TutorialFx {
    private final IntegerProperty id;
    private final StringProperty description;
    private final StringProperty descriptionKey;
    private final StringProperty title;
    private final StringProperty titleKey;
    private final ObjectProperty<TutorialCategoryFX> category;
    private final StringProperty image;
    private final StringProperty imageUrl;
    private final IntegerProperty ordinal;
    private final BooleanProperty launchable;
    private final ObjectProperty<MapVersionFX> mapVersion;
    private final StringProperty technicalName;


    public TutorialFx() {
        id = new SimpleIntegerProperty();
        description = new SimpleStringProperty();
        title = new SimpleStringProperty();
        category = new SimpleObjectProperty<>();
        image = new SimpleStringProperty();
        ordinal = new SimpleIntegerProperty();
        launchable = new SimpleBooleanProperty();
        mapVersion = new SimpleObjectProperty<>();
        descriptionKey = new SimpleStringProperty();
        titleKey = new SimpleStringProperty();
        technicalName = new SimpleStringProperty();
        imageUrl = new SimpleStringProperty();
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

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public int getOrdinal() {
        return ordinal.get();
    }

    public void setOrdinal(int ordinal) {
        this.ordinal.set(ordinal);
    }

    public IntegerProperty ordinalProperty() {
        return ordinal;
    }

    public boolean isLaunchable() {
        return launchable.get();
    }

    public void setLaunchable(boolean launchable) {
        this.launchable.set(launchable);
    }

    public BooleanProperty launchableProperty() {
        return launchable;
    }

    public MapVersionFX getMapVersion() {
        return mapVersion.get();
    }

    public void setMapVersion(MapVersionFX mapVersion) {
        this.mapVersion.set(mapVersion);
    }

    public ObjectProperty<MapVersionFX> mapVersionProperty() {
        return mapVersion;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getDescriptionKey() {
        return descriptionKey.get();
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey.set(descriptionKey);
    }

    public StringProperty descriptionKeyProperty() {
        return descriptionKey;
    }

    public String getTitleKey() {
        return titleKey.get();
    }

    public void setTitleKey(String titleKey) {
        this.titleKey.set(titleKey);
    }

    public StringProperty titleKeyProperty() {
        return titleKey;
    }

    public TutorialCategoryFX getCategory() {
        return category.get();
    }

    public void setCategory(TutorialCategoryFX category) {
        this.category.set(category);
    }

    public ObjectProperty<TutorialCategoryFX> categoryProperty() {
        return category;
    }

    public String getImage() {
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public StringProperty imageProperty() {
        return image;
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl);
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
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
}
