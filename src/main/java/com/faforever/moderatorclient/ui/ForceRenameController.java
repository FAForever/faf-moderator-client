package com.faforever.moderatorclient.ui;

import com.faforever.moderatorclient.api.domain.UserService;
import com.faforever.moderatorclient.ui.domain.PlayerFX;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForceRenameController implements Controller<Parent> {

    private final UserService userService;

    private PlayerFX player;

    public GridPane root;
    public TextField newNameTextField;
    public Label oldNameLabel;

    @Override
    public Parent getRoot() {
        return root;
    }

    public void close() {
        ((Stage) root.getScene().getWindow()).close();
    }

    public void submit() {
        userService.updatePlayer(player.getId(), newNameTextField.getText());
        close();
    }

    public void setPlayer(PlayerFX player) {
        this.player = player;
        oldNameLabel.setText(player.getLogin());
    }
}
