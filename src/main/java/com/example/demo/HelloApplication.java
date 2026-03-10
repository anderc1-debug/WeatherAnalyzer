package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ## HelloApplication
 *
 * Entry point for the JavaFX Weather Analyzer application.
 *
 * ```java
 * // Launch via Launcher.java
 * Application.launch(HelloApplication.class, args);
 * ```
 */
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 650);
        scene.getStylesheets().add(
                HelloApplication.class.getResource("styles.css").toExternalForm());
        stage.setTitle("🌤 Weather Data Analyzer");
        stage.setScene(scene);
        stage.show();
    }
}