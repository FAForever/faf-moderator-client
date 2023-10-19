package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.ui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BracketRatingController implements Controller<HBox> {

    @FXML HBox root;
    @FXML Label ratingLabel;
    public Button removeMaps;

    @Override
    public HBox getRoot() {
        return root;
    }

    public void setRatingLabelText(String text) {
        ratingLabel.setText(text);
    }
}
