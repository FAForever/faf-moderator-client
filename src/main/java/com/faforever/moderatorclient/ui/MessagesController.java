package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.MessagesService;
import com.faforever.moderatorclient.ui.domain.MessageFx;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessagesController implements Controller<HBox> {
    private FilteredList<MessageFx> filteredItemList;
    private ObservableList<MessageFx> messageFxes;

    public HBox root;
    public ToggleGroup filterGroup;
    public TableView<MessageFx> messageTableView;
    private final MessagesService messagesService;
    private final UiService uiService;
    public TextField filter;
    public RadioButton valueRadioButton;
    public RadioButton keyRadioButton;
    public RadioButton regionRadioButton;
    public RadioButton languageRadioButton;
    public RadioButton noneRadioButton;

    public void initialize() {
        ViewHelper.buildMessagesTable(messageTableView, messagesService, log);
        messageFxes = FXCollections.observableArrayList();
        setUpFilter();
    }

    private void setUpFilter() {
        filteredItemList = new FilteredList<>(messageFxes);
        SortedList<MessageFx> sortedList = new SortedList<>(filteredItemList);
        sortedList.comparatorProperty().bind(messageTableView.comparatorProperty());
        messageTableView.setItems(sortedList);
        valueRadioButton.setUserData((Predicate<MessageFx>) o -> o.getValue().toLowerCase().contains(filter.getText().toLowerCase()));
        keyRadioButton.setUserData((Predicate<MessageFx>) o -> o.getKey().toLowerCase().contains(filter.getText().toLowerCase()));
        languageRadioButton.setUserData((Predicate<MessageFx>) o -> o.getLanguage().toLowerCase().contains(filter.getText().toLowerCase()));
        regionRadioButton.setUserData((Predicate<MessageFx>) o -> o.getRegion().toLowerCase().contains(filter.getText().toLowerCase()));
        noneRadioButton.setUserData((Predicate<MessageFx>) o -> true);
        filterGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> filteredItemList.setPredicate((Predicate<? super MessageFx>) newValue.getUserData()));
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            Predicate<? super MessageFx> predicate = (Predicate<? super MessageFx>) filterGroup.getSelectedToggle().getUserData();
            filteredItemList.setPredicate(predicate::test);
        });
    }

    public void onRefreshMessages() {
        messagesService.getAllMessages().thenAccept(tutorialFxes -> Platform.runLater(() -> {
            messageFxes.clear();
            messageFxes.setAll(tutorialFxes);
        })).exceptionally(throwable -> {
            log.error("error loading messages", throwable);
            return null;
        });
    }

    public void deleteMessage() {
        MessageFx selectedItem = messageTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            log.info("Could not delete message, there was no message selected");
            return;
        }
        messagesService.deleteMessage(selectedItem);
        onRefreshMessages();
    }

    public void addMessage() {
        MessageAddController messageAddController = uiService.loadFxml("ui/message_add.fxml");
        messageAddController.setOnSave(this::onRefreshMessages);
        Stage newCategoryDialog = new Stage();
        newCategoryDialog.setTitle("Add new message");
        newCategoryDialog.setScene(new Scene(messageAddController.getRoot()));
        newCategoryDialog.showAndWait();
    }

    @Override
    public HBox getRoot() {
        return root;
    }
}
