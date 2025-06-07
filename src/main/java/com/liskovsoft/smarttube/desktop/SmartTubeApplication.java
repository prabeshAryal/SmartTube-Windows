package com.liskovsoft.smarttube.desktop;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SmartTube Desktop Application
 * A modern Windows desktop YouTube player based on SmartTube Android TV
 */
public class SmartTubeApplication extends Application {
    
    private static final String APP_TITLE = "SmartTube Desktop";
    private static final String APP_VERSION = "1.0.0";
    
    @Override
    public void start(Stage primaryStage) throws IOException {        // Load the new BrowseView.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/liskovsoft/smarttube/desktop/ui/BrowseView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);
        
        // Apply the new stylesheet
        URL cssUrl = getClass().getResource("/com/liskovsoft/smarttube/desktop/css/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Stylesheet not found: styles.css");
        }

        primaryStage.setTitle("SmartTube Desktop - New UI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
