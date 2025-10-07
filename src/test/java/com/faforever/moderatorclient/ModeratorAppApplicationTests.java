package com.faforever.moderatorclient;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest
@DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "CI does not have JavaFX installed")
public class ModeratorAppApplicationTests {

    private static final AtomicBoolean javafxInitialized = new AtomicBoolean(false);

    @BeforeAll
    static void initJavaFx() {
        if (!javafxInitialized.getAndSet(true)) {
            Thread javafxInitThread = new Thread(() -> Application.launch(NonRenderingApp.class));
            javafxInitThread.setDaemon(true);
            javafxInitThread.start();
            try {
                Thread.sleep(500); // wait for JavaFX to initialize
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class NonRenderingApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            // No UI shown
        }
    }

    @Test
    public void contextLoads() {
        // Your test here
    }
}
