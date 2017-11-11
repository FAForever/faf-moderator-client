package com.faforever.moderatorclient.ui;

import javafx.fxml.FXMLLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class UiService {
    private final ApplicationContext applicationContext;

    @Inject
    public UiService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SneakyThrows
    public <T extends Controller<?>> T loadFxml(String relativePath) {
        log.trace("Loading fxml from relative path: {}", relativePath);
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(applicationContext::getBean);
        loader.setLocation(getClass().getResource("/" + relativePath));
        loader.load();
        return loader.getController();
    }
}
