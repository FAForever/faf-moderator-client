package com.faforever.moderatorclient.ui.main_window;

import com.faforever.commons.api.dto.DomainBlacklist;
import com.faforever.moderatorclient.api.domain.DomainBlacklistService;
import com.faforever.moderatorclient.mapstruct.DomainBlacklistMapper;
import com.faforever.moderatorclient.ui.Controller;
import com.faforever.moderatorclient.ui.domain.DomainBlacklistFX;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainBlacklistController implements Controller<SplitPane> {
    private final DomainBlacklistService domainBlacklistService;
    private final DomainBlacklistMapper domainBlacklistMapper;
    private ObservableList<DomainBlacklistFX> domainBlacklist = FXCollections.observableArrayList();

    public SplitPane root;
    public ListView<DomainBlacklistFX> currentDomainBlacklistListView;
    public TextArea addDomainBlacklistTextArea;

    @FXML
    public void initialize() {
        currentDomainBlacklistListView.setItems(domainBlacklist);
        currentDomainBlacklistListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentDomainBlacklistListView.setCellFactory(new Callback<ListView<DomainBlacklistFX>, ListCell<DomainBlacklistFX>>() {
            @Override
            public ListCell<DomainBlacklistFX> call(ListView<DomainBlacklistFX> lv) {
                return new ListCell<DomainBlacklistFX>() {
                    @Override
                    public void updateItem(DomainBlacklistFX item, boolean empty) {
                        super.updateItem(item, empty);

                        textProperty().unbind();
                        if (item == null) {
                            setText(null);
                        } else {
                            textProperty().bind(item.domainProperty());
                        }
                    }
                };
            }
        });
    }

    @Override
    public SplitPane getRoot() {
        return root;
    }

    public void refresh() {
        domainBlacklist.clear();
        domainBlacklist.addAll(
                domainBlacklistMapper.map(domainBlacklistService.getAll())
        );
    }

    public void removeSelected() {
        for (DomainBlacklistFX selected : currentDomainBlacklistListView.getSelectionModel().getSelectedItems()) {
            domainBlacklistService.remove(selected.getDomain());
        }

        refresh();
    }

    public void addList() {
        List<String> newDomains = Lists.newArrayList(addDomainBlacklistTextArea.getText().split("\\n"));
        List<String> currentDomains = domainBlacklist.stream()
                .map(DomainBlacklistFX::getDomain)
                .collect(Collectors.toList());

        newDomains.stream()
                .filter(newDomain -> !currentDomains.contains(newDomain))
                .forEach(newDomain -> domainBlacklistService.add(new DomainBlacklist().setDomain(newDomain)));

        refresh();
    }
}
