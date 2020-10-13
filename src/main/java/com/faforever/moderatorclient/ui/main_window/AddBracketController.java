package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.ui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddBracketController implements Controller<GridPane> {

    @FXML GridPane root;
    @FXML Label bracketRatingLabel;
    @FXML public Button addToBracketButton;
    @FXML public Button removeFromBracketButton;

    @Override
    public GridPane getRoot() {
        return root;
    }

    public void setRatingLabelText(String text) {
        bracketRatingLabel.setText(text);
    }
}
