package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.Tutorial;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TutorialCategoryFX {

    private final IntegerProperty id;
    private final StringProperty categoryKey;
    private final StringProperty category;
    private final ObservableList<Tutorial> tutorials;

    public TutorialCategoryFX() {
        tutorials = FXCollections.observableArrayList();
        id = new SimpleIntegerProperty();
        category = new SimpleStringProperty();
        categoryKey = new SimpleStringProperty();
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

    public String getCategoryKey() {
        return categoryKey.get();
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey.set(categoryKey);
    }

    public StringProperty categoryKeyProperty() {
        return categoryKey;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public ObservableList<Tutorial> getTutorials() {
        return tutorials;
    }
}
