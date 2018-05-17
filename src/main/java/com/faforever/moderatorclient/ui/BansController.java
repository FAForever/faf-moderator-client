package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.BanStatus;
import com.faforever.moderatorclient.api.domain.BanService;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Predicate;

@Component
@Slf4j
public class BansController {
    private final UiService uiService;
    private final BanService banService;
    public HBox root;
    public ToggleGroup filterGroup;
    public TextField filter;
    public RadioButton playerRadioButton;
    public RadioButton banIdRadioButton;
    public TableView<BanInfoFX> banTableView;
    public CheckBox onlyActiveCheckBox;
    private FilteredList<BanInfoFX> filteredItemList;
    private ObservableList<BanInfoFX> itemList;

    @Inject
    public BansController(UiService uiService, BanService banService) {
        this.uiService = uiService;
        this.banService = banService;
    }

    @FXML
    public void initialize() {
        itemList = FXCollections.observableArrayList();
        filteredItemList = new FilteredList<>(itemList);
        SortedList<BanInfoFX> sortedItemList = new SortedList<>(filteredItemList);
        sortedItemList.comparatorProperty().bind(banTableView.comparatorProperty());
        ViewHelper.buildBanTableView(banTableView, sortedItemList, true);
        onRefreshBans();
        setUpFilter();
    }

    private void setUpFilter() {
        banIdRadioButton.setUserData((Predicate<BanInfoFX>) o -> activeFilter(o) && o.getId().toLowerCase().contains(filter.getText().toLowerCase()));
        playerRadioButton.setUserData((Predicate<BanInfoFX>) o -> activeFilter(o) && o.getPlayer().getLogin().toLowerCase().contains(filter.getText().toLowerCase()));
        filterGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> renewFilter());
        filter.textProperty().addListener((observable, oldValue, newValue) -> renewFilter());
        onlyActiveCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> renewFilter());
        renewFilter();
    }

    private void renewFilter() {
        Predicate<? super BanInfoFX> predicate = (Predicate<? super BanInfoFX>) filterGroup.getSelectedToggle().getUserData();
        filteredItemList.setPredicate(predicate::test);
    }

    public void onRefreshBans() {
        banService.getAllBans().thenAccept(banInfoFXES -> Platform.runLater(() -> itemList.setAll(banInfoFXES))).exceptionally(throwable -> {
            log.error("error loading bans", throwable);
            return null;
        });
    }

    public void editBan() {
        BanInfoFX selectedItem = banTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            log.info("Could not delete ban, there was no message selected");
            return;
        }

        openBanDialog(selectedItem, false);
    }

    private void openBanDialog(BanInfoFX banInfoFX, boolean isNew) {
        BanInfoController banInfoController = uiService.loadFxml("ui/banInfo.fxml");
        banInfoController.setBanInfo(banInfoFX);
        banInfoController.addPostedListener(banInfoFX1 -> onRefreshBans());

        Stage banInfoDialog = new Stage();
        banInfoDialog.setTitle(isNew ? "Apply new ban" : "Edit ban");
        banInfoDialog.setScene(new Scene(banInfoController.getRoot()));
        banInfoDialog.showAndWait();
    }

    public void addBan() {
        openBanDialog(new BanInfoFX(), true);
    }

    private boolean activeFilter(BanInfoFX banInfoFX) {
        return !onlyActiveCheckBox.isSelected() || banInfoFX.getBanStatus() == BanStatus.BANNED;
    }
}
