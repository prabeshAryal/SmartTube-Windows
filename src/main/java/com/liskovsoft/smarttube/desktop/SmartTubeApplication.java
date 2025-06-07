package com.liskovsoft.smarttube.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.liskovsoft.smarttube.desktop.ui.MainController;
import com.liskovsoft.smarttube.desktop.service.YouTubeService;

import java.io.IOException;
import java.util.Objects;

/**
 * SmartTube Desktop Application
 * A modern Windows desktop YouTube player based on SmartTube Android TV
 */
public class SmartTubeApplication extends Application {
    
    private static final String APP_TITLE = "SmartTube Desktop";
    private static final String APP_VERSION = "1.0.0";
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Set up the primary stage
            setupPrimaryStage(primaryStage);
              // Load the main FXML layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Apply dark theme CSS
            scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/dark-theme.css")).toExternalForm()
            );
            
            // Get the controller and initialize it
            MainController controller = loader.getController();
            controller.setStage(primaryStage);
            
            // Set up keyboard event handling
            scene.setOnKeyPressed(controller::handleKeyPressed);
            
            // Set up the scene
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("SmartTube Desktop started successfully");
              } catch (IOException e) {
            System.err.println("Failed to start application: " + e.getMessage());
            Platform.exit();
        }
    }
    
    private void setupPrimaryStage(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE + " v" + APP_VERSION);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.setWidth(1600);
        primaryStage.setHeight(1000);
        
        // Center the stage on screen
        primaryStage.centerOnScreen();
          // Set application icon
        try {
            Image icon = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/app-icon.png")
            ));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        
        // Handle close request
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Application closing...");
            Platform.exit();
            System.exit(0);
        });
    }
      @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Application stopped");
    }
    
    public static void main(String[] args) {        // Set system properties for better performance
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
          // Set FFmpeg path if available
        String ffmpegPath = "C:\\Users\\prabe\\scoop\\shims\\ffmpeg.exe";
        if (new java.io.File(ffmpegPath).exists()) {
            System.setProperty("ffmpeg.path", ffmpegPath);
            System.out.println("FFmpeg found at: " + ffmpegPath);
        }
        
        System.out.println("Starting SmartTube Desktop v" + APP_VERSION);
        launch(args);
    }
}
