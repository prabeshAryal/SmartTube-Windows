package com.liskovsoft.smarttube.desktop.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {

    @FXML private Button menuButton;
    @FXML private StackPane contentArea;
    
    // fx:id="sidebarView" in MainView.fxml for the <fx:include> tag
    @FXML private VBox sidebarView; 

    // Injected controller for the included SidebarView.fxml (fx:id of include + "Controller")
    @FXML private SidebarController sidebarViewController; 

    private boolean sidebarCurrentlyVisible = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sidebarViewController != null) {
            sidebarViewController.setMainController(this);
        } else {
            System.err.println("SidebarViewController is null. Check fx:id='sidebarView' on the <fx:include> tag in MainView.fxml and ensure SidebarView.fxml has a controller.");
        }

        if (sidebarView != null) {
            sidebarCurrentlyVisible = sidebarView.isVisible();
        } else {
             System.err.println("sidebarView (VBox root of Sidebar) is null. Check fx:id='sidebarView' on the <fx:include> tag in MainView.fxml.");
             sidebarCurrentlyVisible = true; // Default if not injected
        }

        if (menuButton != null) {
            menuButton.setOnAction(event -> toggleSidebar());
        }
        
        loadContent("HomeView.fxml"); 
    }

    private void toggleSidebar() {
        if (sidebarView != null) {
            sidebarCurrentlyVisible = !sidebarCurrentlyVisible;
            sidebarView.setVisible(sidebarCurrentlyVisible);
            sidebarView.setManaged(sidebarCurrentlyVisible);
        } else {
            System.err.println("Cannot toggle sidebar: sidebarView VBox is not injected.");
        }
    }

    public void loadContent(String fxmlFile) {
        try {
            URL fxmlUrl = getClass().getResource("/com/liskovsoft/smarttube/desktop/ui/" + fxmlFile);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile + " (resolved path: /com/liskovsoft/smarttube/desktop/ui/" + fxmlFile + ")");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().clear();
            Label errorLabel = new Label("Error loading view: " + fxmlFile + "\\n" + e.getMessage());
            contentArea.getChildren().add(errorLabel);
        }
    }
}
