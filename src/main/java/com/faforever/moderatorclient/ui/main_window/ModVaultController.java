package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.Mod;
import com.faforever.moderatorclient.api.domain.ModService;
import com.faforever.moderatorclient.mapstruct.ModMapper;
import com.faforever.moderatorclient.mapstruct.ModVersionMapper;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.domain.ModFX;
import com.faforever.moderatorclient.ui.domain.ModVersionFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ModVaultController implements Controller<SplitPane> {
    private final ModService modService;
    private final ModMapper modMapper;
    private final ModVersionMapper modVersionMapper;
    private final ObservableList<ModFX> mods = FXCollections.observableArrayList();
    private final ObservableList<ModVersionFX> modVersions = FXCollections.observableArrayList();

    public SplitPane root;
    public RadioButton searchModByModNameRadioButton;
    public RadioButton searchModByModVersionUidRadioButton;
    public RadioButton searchModByAuthorIdRadioButton;
    public RadioButton searchModByAuthorNameRadioButton;
    public TextField modSearchTextField;
    public CheckBox excludeHiddenModVersionsCheckbox;
    public ImageView modVersionPreviewImageView;
    public TableView<ModFX> modSearchTableView;
    public TableView<ModVersionFX> modVersionTableView;
    public Button toggleModVersionHidingButton;
    public Button toggleModVersionRatingButton;

    @Override
    public SplitPane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
        ViewHelper.buildModTableView(modSearchTableView, mods);
        ViewHelper.buildModVersionTableView(modVersionTableView, modVersions);

        modSearchTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            modVersions.clear();
            Optional.ofNullable(newValue).ifPresent(mod -> modVersions.addAll(
                    mod.getVersions()));
        });

        modVersionTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                modVersionPreviewImageView.setImage(null);
                toggleModVersionHidingButton.setDisable(true);
                toggleModVersionRatingButton.setDisable(true);
            } else {
                modVersionPreviewImageView.setImage(new Image(newValue.getThumbnailUrl().toString()));
                toggleModVersionHidingButton.setDisable(false);
                toggleModVersionRatingButton.setDisable(false);
            }
        });
    }

    public void onSearchMods() {
        mods.clear();
        modSearchTableView.getSortOrder().clear();

        List<Mod> modsFound = Collections.emptyList();
        String searchPattern = modSearchTextField.getText();
        if (searchModByModNameRadioButton.isSelected()) {
            modsFound = modService.findModsByName(searchPattern, excludeHiddenModVersionsCheckbox.isSelected());
        } else if (searchModByModVersionUidRadioButton.isSelected()) {
            modsFound = modService.findModsByModVersionUid(searchPattern, excludeHiddenModVersionsCheckbox.isSelected());
        } else if (searchModByAuthorIdRadioButton.isSelected()) {
            modsFound = modService.findModsByAuthorId(searchPattern, excludeHiddenModVersionsCheckbox.isSelected());
        } else if (searchModByAuthorNameRadioButton.isSelected()) {
            modsFound = modService.findModsByAuthorName(searchPattern, excludeHiddenModVersionsCheckbox.isSelected());
        }

        mods.addAll(modMapper.map(modsFound));
    }

    public void onToggleModVersionHiding() {
        ModVersionFX modVersion = modVersionTableView.getSelectionModel().getSelectedItem();

        Assert.notNull(modVersion, "You can only edit a selected ModVersion");

        modVersion.setHidden(!modVersion.isHidden());
        modService.patchModVersion(modVersionMapper.map(modVersion));
    }

    public void onToggleModVersionRanking() {
        ModVersionFX modVersion = modVersionTableView.getSelectionModel().getSelectedItem();

        Assert.notNull(modVersion, "You can only edit a selected ModVersion");

        modVersion.setRanked(!modVersion.isRanked());
        modService.patchModVersion(modVersionMapper.map(modVersion));
    }
}
