package com.liskovsoft.smarttube.desktop;

import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

/**
 * Preloader for SmartTube Desktop to show loading screen
 */
public class SmartTubePreloader extends Preloader {
    
    private Stage preloaderStage;
    private ProgressBar progressBar;
    private Label statusLabel;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;
        
        // Create splash screen content
        VBox splashLayout = createSplashLayout();
        
        Scene splashScene = new Scene(splashLayout, 400, 300);
        splashScene.setFill(Color.TRANSPARENT);
        
        // Style the stage
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(splashScene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    
    private VBox createSplashLayout() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a1a, #2d2d2d); " +
                       "-fx-background-radius: 10; " +
                       "-fx-border-radius: 10; " +
                       "-fx-border-color: #555; " +
                       "-fx-border-width: 1;");
        layout.setPrefSize(400, 300);
        
        // App logo/icon
        try {
            ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/app-icon.png"))
            ));
            logo.setFitWidth(80);
            logo.setFitHeight(80);
            logo.setPreserveRatio(true);
            layout.getChildren().add(logo);
        } catch (Exception e) {
            // Fallback to text if image not found
            Label logoText = new Label("SmartTube");
            logoText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            layout.getChildren().add(logoText);
        }
        
        // App title
        Label titleLabel = new Label("SmartTube Desktop");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Version label
        Label versionLabel = new Label("Version 1.0.0");
        versionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaa;");
        
        // Progress bar
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #4CAF50;");
        
        // Status label
        statusLabel = new Label("Loading...");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ccc;");
        
        layout.getChildren().addAll(titleLabel, versionLabel, progressBar, statusLabel);
        
        return layout;
    }
    
    @Override
    public void handleProgressNotification(ProgressNotification info) {
        if (progressBar != null) {
            progressBar.setProgress(info.getProgress());
        }
    }
    
    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            if (statusLabel != null) {
                statusLabel.setText("Starting application...");
            }
            if (preloaderStage != null) {
                preloaderStage.hide();
            }
        }
    }
}
