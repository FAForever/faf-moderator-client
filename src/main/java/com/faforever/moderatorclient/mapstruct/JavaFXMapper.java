package com.faforever.moderatorclient.mapstruct;

import javafx.beans.property.*;
import org.springframework.stereotype.Component;

@Component
public class JavaFXMapper {
    public BooleanProperty map(Boolean value) {
        return new SimpleBooleanProperty(value);
    }

    public Boolean map(BooleanProperty value) {
        return value.get();
    }

    public StringProperty map(String value) {
        return new SimpleStringProperty(value);
    }

    public String map(StringProperty value) {
        return value.get();
    }

    public IntegerProperty map(Integer value) {
        return new SimpleIntegerProperty(value);
    }

    public Integer map(IntegerProperty value) {
        return value.get();
    }

    public <T> ObjectProperty<T> map(T value) {
        return new SimpleObjectProperty<>(value);
    }

    public <T> T map(ObjectProperty<T> value) {
        return value.get();
    }

//    public <T> ListProperty<T> mapListToListProperty (List<T> value) {
//        return new SimpleListProperty<>(FXCollections.observableList(value));
//    }
//
//    public <T> List<T> map (ListProperty<T> value) {
//        return value.get();
//    }
}
