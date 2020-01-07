package com.faforever.moderatorclient;

import com.faforever.moderatorclient.ui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@Slf4j
public class FafModeratorClientApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    public static void applicationMain(String[] args) {
        Application.launch(FafModeratorClientApplication.class, args);
    }

    @Override
    public void init() {
        SpringApplication app = new SpringApplication(FafModeratorClientApplication.class);
        applicationContext = app.run();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(getClass().getResource("/style/NotoEmoji-Regular.ttf").toExternalForm(), 12);

        StageHolder.setStage(primaryStage);
        primaryStage.setTitle("FAF Moderator Client");

        UiService uiService = applicationContext.getBean(UiService.class);
        MainController mainController = uiService.loadFxml("ui/mainWindow.fxml");
        mainController.display();
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/media/favicon.png")));
        Scene scene = new Scene(mainController.getRoot());
        scene.getStylesheets().add(getClass().getResource("/style/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Bean
    public PlatformService platformService() {
        return new PlatformServiceImpl(getHostServices());
    }
}
