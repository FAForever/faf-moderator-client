package com.faforever.moderatorclient.ui;

import com.faforever.commons.api.dto.BanStatus;
import com.faforever.moderatorclient.api.domain.BanService;
import com.faforever.moderatorclient.ui.domain.BanInfoFX;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Component
@Slf4j
public class BansController implements Controller<HBox> {
    private final UiService uiService;
    private final BanService banService;
    public HBox root;
    public ToggleGroup filterGroup;
    public TextField filter;
    public RadioButton playerRadioButton;
    public RadioButton banIdRadioButton;
    public TableView<BanInfoFX> banTableView;
    public CheckBox onlyActiveCheckBox;
    public Button editBanButton;
    private FilteredList<BanInfoFX> filteredList;
    private ObservableList<BanInfoFX> itemList;
    private boolean inSearchMode = false;

    @Inject
    public BansController(UiService uiService, BanService banService) {
        this.uiService = uiService;
        this.banService = banService;
    }

    @Override
    public HBox getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        itemList = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(itemList);
        SortedList<BanInfoFX> sortedItemList = new SortedList<>(filteredList);
        sortedItemList.comparatorProperty().bind(banTableView.comparatorProperty());
        ViewHelper.buildBanTableView(banTableView, sortedItemList, true);
        onRefreshLatestBans();
        playerRadioButton.setUserData((Supplier<List<BanInfoFX>>) () -> banService.getBanInfoByBannedPlayerNameContains(filter.getText()));
        banIdRadioButton.setUserData((Supplier<List<BanInfoFX>>) () -> Collections.singletonList(banService.getBanInfoById(filter.getText())));
        editBanButton.disableProperty().bind(banTableView.getSelectionModel().selectedItemProperty().isNull());
        InvalidationListener onlyActiveBansChangeListener = (observable) ->
                filteredList.setPredicate(banInfoFX -> !onlyActiveCheckBox.isSelected() || banInfoFX.getBanStatus() == BanStatus.BANNED);
        onlyActiveCheckBox.selectedProperty().addListener(onlyActiveBansChangeListener);
        onlyActiveBansChangeListener.invalidated(onlyActiveCheckBox.selectedProperty());
    }

    public void onRefreshLatestBans() {
        banService.getLatestBans().thenAccept(banInfoFXES -> Platform.runLater(() -> itemList.setAll(banInfoFXES))).exceptionally(throwable -> {
            log.error("error loading bans", throwable);
            return null;
        });
        filter.clear();
        inSearchMode = false;
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
        banInfoController.addPostedListener(banInfoFX1 -> {
            if (inSearchMode) {
                onSearch();
                return;
            }
            onRefreshLatestBans();
        });

        Stage banInfoDialog = new Stage();
        banInfoDialog.setTitle(isNew ? "Apply new ban" : "Edit ban");
        banInfoDialog.setScene(new Scene(banInfoController.getRoot()));
        banInfoDialog.showAndWait();
    }

    public void addBan() {
        openBanDialog(new BanInfoFX(), true);
    }

    public void onSearch() {
        List<BanInfoFX> banInfoFXES = ((Supplier<List<BanInfoFX>>) filterGroup.getSelectedToggle().getUserData()).get();
        itemList.setAll(banInfoFXES);
        inSearchMode = true;
    }
}
