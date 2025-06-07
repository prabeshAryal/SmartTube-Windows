package com.liskovsoft.smarttube.desktop.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML private Button homeButton;
    @FXML private Button trendingButton;
    @FXML private Button subscriptionsButton;
    @FXML private Button historyButton;
    @FXML private Button libraryButton;
    @FXML private Button settingsButton;

    private MainViewController mainViewController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Actions for sidebar buttons will be set up here
        // They will typically call mainViewController.loadContent(...)
        homeButton.setOnAction(event -> {
            if (mainViewController != null) mainViewController.loadContent("HomeView.fxml");
        });
        trendingButton.setOnAction(event -> {
            if (mainViewController != null) mainViewController.loadContent("TrendingView.fxml");
        });
        subscriptionsButton.setOnAction(event -> {
            if (mainViewController != null) mainViewController.loadContent("SubscriptionsView.fxml");
        });
        historyButton.setOnAction(event -> {
            if (mainViewController != null) mainViewController.loadContent("HistoryView.fxml");
        });
        libraryButton.setOnAction(event -> {
            if (mainViewController != null) mainViewController.loadContent("LibraryView.fxml");
        });
        settingsButton.setOnAction(event -> {
            if (mainViewController != null) mainViewController.loadContent("SettingsView.fxml");
        });
    }

    // Method to set the MainViewController, called after Sidebar.fxml is loaded
    public void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
