package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.UserService;
import com.faforever.moderatorclient.mapstruct.UserNoteMapper;
import com.faforever.moderatorclient.ui.domain.UserNoteFX;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class UserNoteController implements Controller<Pane> {
    private final UserService userService;
    private final UserNoteMapper userNoteMapper;

    public GridPane root;
    public TextField affectedUserTextField;
    public TextField authorTextField;
    public TextArea noteTextArea;
    public CheckBox watchedCheckBox;

    @Getter
    private UserNoteFX userNoteFX;
    private Consumer<UserNoteFX> postedListener;

    public void addPostedListener(Consumer<UserNoteFX> listener) {
        this.postedListener = listener;
    }

    @Override
    public Pane getRoot() {
        return root;
    }

    @FXML
    public void initialize() {
    }

    public void setUserNoteFX(UserNoteFX userNoteFX) {
        this.userNoteFX = userNoteFX;

        if (userNoteFX != null) {
            Optional.ofNullable(userNoteFX.getPlayer()).ifPresent(author -> affectedUserTextField.textProperty().bind(author.representationProperty()));
            Optional.ofNullable(userNoteFX.getAuthor()).ifPresent(author -> authorTextField.textProperty().bind(author.representationProperty()));
            watchedCheckBox.setSelected(userNoteFX.isWatched());

            noteTextArea.setText(userNoteFX.getNote());
        }
    }

    public void onSave() {
        Assert.notNull(userNoteFX, "You can't save if userNoteFX is null.");

        if (!validate()) {
            return;
        }

        userNoteFX.setNote(noteTextArea.getText());
        userNoteFX.setWatched(watchedCheckBox.isSelected());

        if (userNoteFX.getId() == null) {
            log.debug("Creating userNote for player '{}'", userNoteFX.getPlayer().representationProperty().get());
            String newNoteId = userService.createUserNote(userNoteMapper.map(userNoteFX));
            UserNoteFX loadedUserNote = userService.getUserNoteById(newNoteId);

            if (postedListener != null) {
                postedListener.accept(loadedUserNote);
            }
        } else {
            log.debug("Updating userNote id '{}'", userNoteFX.getId());
            userService.patchUserNote(userNoteMapper.map(userNoteFX));
        }
        close();
    }

    private boolean validate() {
        List<String> validationErrors = new ArrayList<>();

        if (StringUtils.isBlank(noteTextArea.getText())) {
            validationErrors.add("No text is given.");
        }

        if (validationErrors.size() > 0) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("UserNote validation failed");
            errorAlert.setContentText(
                    validationErrors.stream()
                            .collect(Collectors.joining("\n"))
            );
            errorAlert.showAndWait();

            return false;
        }

        return true;
    }

    public void onAbort() {
        close();
    }

    private void close() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
