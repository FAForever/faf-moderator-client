package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.caches.SmallThumbnailCache;
import com.faforever.moderatorclient.ui.data_cells.ListViewMapCell;
import com.faforever.moderatorclient.ui.domain.MapPoolAssignmentFX;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import com.faforever.moderatorclient.ui.domain.MatchmakerQueueMapPoolFX;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BracketListViewController implements Controller<VBox> {

    public TextField vetoTokensPerPlayerInput;
    public TextField maxTokensPerMapInput;
    public TextField minimalMapsAllowedInput;
    @FXML VBox root;
    @FXML ListView<MapPoolAssignmentFX> mapListView;

    private final SmallThumbnailCache smallThumbnailCache;

    @Override
    public VBox getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        mapListView.setCellFactory(mapListView -> new ListViewMapCell(smallThumbnailCache));
    }

    private void bindVetoTokensPerPlayer(IntegerProperty property) {
        vetoTokensPerPlayerInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int value = Integer.parseInt(newValue);
                if (value < 0 || value > 255)
                    throw new NumberFormatException("veto tokens per player must be between 0 and 255");
                if (!Objects.equals(property.get(), value)) {
                    property.set(value);
                }
                vetoTokensPerPlayerInput.setStyle("");
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
                vetoTokensPerPlayerInput.setStyle("-fx-background-color: rgb(255,100,100)");
            }
        });

        ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
            String stringValue = newValue.toString();
            if (!Objects.equals(vetoTokensPerPlayerInput.getText(), stringValue)) {
                vetoTokensPerPlayerInput.setText(stringValue);
                vetoTokensPerPlayerInput.setStyle("");
            }
        };

        property.addListener(listener);
        listener.changed(property, property.get(), property.get());

    }

    private void bindMaxTokensPerMap(IntegerProperty property) {
        maxTokensPerMapInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (Objects.equals(newValue, "D")) {
                    property.set(0);
                    maxTokensPerMapInput.setStyle("");
                    return;
                }
                int value = Integer.parseInt(newValue);
                if (value < 1 || value > 255)
                    throw new NumberFormatException("max tokens per map must be between 1 and 255, or D (dynamic)");
                if (!Objects.equals(property.get(), value)) {
                    property.set(value);
                }
                maxTokensPerMapInput.setStyle("");
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
                maxTokensPerMapInput.setStyle("-fx-background-color: rgb(255,100,100)");
            }
        });

        ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
            if (Objects.equals(newValue, 0)) {
                if (!Objects.equals(maxTokensPerMapInput.getText(), "D")) {
                    maxTokensPerMapInput.setStyle("");
                    maxTokensPerMapInput.setText("D");
                }
                return;
            }

            String stringValue = newValue.toString();
            if (!Objects.equals(maxTokensPerMapInput.getText(), stringValue)) {
                maxTokensPerMapInput.setText(stringValue);
                maxTokensPerMapInput.setStyle("");
            }
        };
        property.addListener(listener);
        listener.changed(property, property.get(), property.get());
    }

    private void bindMinimalMapsAllowed(FloatProperty property) {
        minimalMapsAllowedInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float value = Float.parseFloat(newValue);

                if (value < 1.0f) {
                    throw new NumberFormatException("Minimal maps allowed must be not lower than 1.0");
                }
                String formattedValue = String.format(Locale.US, "%.2f", value);
                if (!Objects.equals(minimalMapsAllowedInput.getText(), formattedValue)) {
                    minimalMapsAllowedInput.setText(formattedValue);
                }
                if (!Objects.equals(property.get(), value)) {
                    property.set(value);
                }
                minimalMapsAllowedInput.setStyle("");
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
                minimalMapsAllowedInput.setStyle("-fx-background-color: rgb(255,100,100)");
            }
        });

        ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
            float value = newValue.floatValue();
            String formattedValue = String.format(Locale.US, "%.2f", value);
            if (!Objects.equals(minimalMapsAllowedInput.getText(), formattedValue)) {
                minimalMapsAllowedInput.setText(formattedValue);
                minimalMapsAllowedInput.setStyle("");
            }
        };
        property.addListener(listener);
        listener.changed(property, property.get(), property.get());
    }

    public void setMaps(ObservableList<MapPoolAssignmentFX> maps) {
        mapListView.prefHeightProperty().bind(Bindings.size(maps).multiply(70));
        mapListView.setItems(maps);
//        mapListView.setItems(maps.sorted(Comparator.comparing(MapPoolAssignmentFX::getId)));
    }

    public void bindVetoParams(MatchmakerQueueMapPoolFX bracket) {
        bindVetoTokensPerPlayer(bracket.vetoTokensPerPlayerProperty());
        bindMaxTokensPerMap(bracket.maxTokensPerMapProperty());
        bindMinimalMapsAllowed(bracket.minimalMapsAllowedProperty());
    }
}
