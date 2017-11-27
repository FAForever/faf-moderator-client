package com.faforever.moderatorclient;

import com.faforever.moderatorclient.ui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {
        JmxAutoConfiguration.class,
        SecurityAutoConfiguration.class,
})
public class FafModeratorClientApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        SpringApplication app = new SpringApplication(FafModeratorClientApplication.class);
        app.setWebEnvironment(false);
        applicationContext = app.run();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageHolder.setStage(primaryStage);
        primaryStage.setTitle("FAF Moderator Client");

        UiService uiService = applicationContext.getBean(UiService.class);
        MainController mainController = uiService.loadFxml("ui/mainWindow.fxml");
        mainController.display();
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/media/favicon.png")));
        primaryStage.setScene(new Scene(mainController.getRoot()));
        primaryStage.show();
    }

    @Bean
    public PlatformService platformService() {
        return new PlatformServiceImpl(getHostServices());
    }
}
