package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.caches.SmallThumbnailCache;
import com.faforever.moderatorclient.ui.data_cells.ListViewMapCell;
import com.faforever.moderatorclient.ui.domain.MapPoolAssignmentFX;
import com.faforever.moderatorclient.ui.domain.MapVersionFX;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BracketListViewController implements Controller<VBox> {

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

    public void setMaps(ObservableList<MapPoolAssignmentFX> maps) {
        mapListView.prefHeightProperty().bind(Bindings.size(maps).multiply(70));
        mapListView.setItems(maps);
//        mapListView.setItems(maps.sorted(Comparator.comparing(MapPoolAssignmentFX::getId)));
    }
}
