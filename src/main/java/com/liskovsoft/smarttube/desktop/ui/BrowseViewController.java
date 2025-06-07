package com.liskovsoft.smarttube.desktop.ui;

import com.liskovsoft.smarttube.desktop.model.Video;
import com.liskovsoft.smarttube.desktop.model.VideoGroup;
import com.liskovsoft.smarttube.desktop.service.YouTubeService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the main browse view - equivalent to Android's BrowseFragment
 */
public class BrowseViewController implements Initializable {
    
    // Header components
    @FXML private Button menuButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button profileButton;
    
    // Sidebar components
    @FXML private Button homeButton;
    @FXML private Button trendingButton;
    @FXML private Button subscriptionsButton;
    @FXML private Button libraryButton;
    @FXML private Button historyButton;
    @FXML private Button watchLaterButton;
    @FXML private Button likedVideosButton;
    @FXML private Button settingsButton;
    
    // Content area
    @FXML private StackPane contentArea;
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox videoSectionsContainer;
    
    // Status bar
    @FXML private Label statusLabel;
    @FXML private Label connectionStatus;
    
    // Services and data
    private YouTubeService youTubeService;
    private List<VideoGroup> videoSections;
    private String currentSection = "home";
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupServices();
        setupUI();
        loadDefaultContent();
    }
    
    private void setupServices() {
        try {
            youTubeService = new YouTubeService();
            videoSections = new ArrayList<>();
        } catch (Exception e) {
            updateStatus("Error initializing services: " + e.getMessage());
        }
    }
    
    private void setupUI() {
        // Setup search field
        searchField.setOnAction(e -> onSearch());
        
        // Mark home as selected initially
        updateSelectedButton(homeButton);
        
        // Setup status
        updateStatus("Loading...");
        updateConnectionStatus("Connecting...");
    }
    
    private void loadDefaultContent() {
        onHomeSelected();
    }
    
    // Navigation methods
    @FXML
    private void onHomeSelected() {
        updateSelectedButton(homeButton);
        currentSection = "home";
        loadVideoSections("home");
    }
    
    @FXML
    private void onTrendingSelected() {
        updateSelectedButton(trendingButton);
        currentSection = "trending";
        loadVideoSections("trending");
    }
    
    @FXML
    private void onSubscriptionsSelected() {
        updateSelectedButton(subscriptionsButton);
        currentSection = "subscriptions";
        loadVideoSections("subscriptions");
    }
    
    @FXML
    private void onLibrarySelected() {
        updateSelectedButton(libraryButton);
        currentSection = "library";
        loadVideoSections("library");
    }
    
    @FXML
    private void onHistorySelected() {
        updateSelectedButton(historyButton);
        currentSection = "history";
        loadVideoSections("history");
    }
    
    @FXML
    private void onWatchLaterSelected() {
        updateSelectedButton(watchLaterButton);
        currentSection = "watch_later";
        loadVideoSections("watch_later");
    }
    
    @FXML
    private void onLikedVideosSelected() {
        updateSelectedButton(likedVideosButton);
        currentSection = "liked_videos";
        loadVideoSections("liked_videos");
    }
    
    @FXML
    private void onSettingsSelected() {
        updateSelectedButton(settingsButton);
        currentSection = "settings";
        loadSettingsView();
    }
    
    @FXML
    private void onSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            updateSelectedButton(null); // Deselect all
            currentSection = "search";
            performSearch(query);
        }
    }
    
    private void updateSelectedButton(Button selectedButton) {
        // Remove selected style from all buttons
        Button[] sidebarButtons = {
            homeButton, trendingButton, subscriptionsButton, 
            libraryButton, historyButton, watchLaterButton, 
            likedVideosButton, settingsButton
        };
        
        for (Button button : sidebarButtons) {
            button.getStyleClass().remove("sidebar-button-selected");
        }
        
        // Add selected style to the current button
        if (selectedButton != null) {
            selectedButton.getStyleClass().add("sidebar-button-selected");
        }
    }
    
    private void loadVideoSections(String section) {
        updateStatus("Loading " + section + "...");
        
        CompletableFuture.runAsync(() -> {
            try {
                // Load video sections based on the selected category
                List<VideoGroup> sections = loadVideoSectionsForCategory(section);
                
                Platform.runLater(() -> {
                    displayVideoSections(sections);
                    updateStatus("Ready");
                    updateConnectionStatus("Connected");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    updateStatus("Error loading content: " + e.getMessage());
                    updateConnectionStatus("Error");
                });
            }
        });
    }
    
    private List<VideoGroup> loadVideoSectionsForCategory(String category) {
        List<VideoGroup> sections = new ArrayList<>();
        
        try {
            switch (category) {
                case "home":
                    sections.add(createSampleVideoGroup("Recommended", 20));
                    sections.add(createSampleVideoGroup("Recently Uploaded", 15));
                    sections.add(createSampleVideoGroup("Popular Today", 12));
                    break;
                case "trending":
                    sections.add(createSampleVideoGroup("Trending Now", 25));
                    sections.add(createSampleVideoGroup("Music", 15));
                    sections.add(createSampleVideoGroup("Gaming", 15));
                    break;
                case "subscriptions":
                    sections.add(createSampleVideoGroup("Latest Uploads", 20));
                    sections.add(createSampleVideoGroup("Recommended from Subscriptions", 15));
                    break;
                case "library":
                    sections.add(createSampleVideoGroup("Watch Later", 10));
                    sections.add(createSampleVideoGroup("Liked Videos", 8));
                    sections.add(createSampleVideoGroup("My Playlists", 5));
                    break;
                case "history":
                    sections.add(createSampleVideoGroup("Recently Watched", 20));
                    sections.add(createSampleVideoGroup("Watch Again", 15));
                    break;
                default:
                    sections.add(createSampleVideoGroup("Content", 10));
                    break;
            }
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error loading video sections: " + e.getMessage());
        }
        
        return sections;
    }
    
    private VideoGroup createSampleVideoGroup(String title, int videoCount) {
        VideoGroup group = new VideoGroup();
        group.setTitle(title);
        
        List<Video> videos = new ArrayList<>();
        for (int i = 1; i <= videoCount; i++) {            Video video = new Video();
            video.setTitle(title + " Video " + i);
            video.setChannelName("Sample Channel " + (i % 5 + 1));
            video.setDurationText((5 + i % 10) + ":" + String.format("%02d", i % 60));
            video.setViewCount(1000000L + i * 50000);
            video.setThumbnailUrl("https://img.youtube.com/vi/sample" + i + "/maxresdefault.jpg");
            videos.add(video);
        }
        
        group.setVideos(videos);
        return group;
    }
    
    private void displayVideoSections(List<VideoGroup> sections) {
        videoSectionsContainer.getChildren().clear();
        
        for (VideoGroup section : sections) {
            try {
                // Create section header
                Label sectionTitle = new Label(section.getTitle());
                sectionTitle.getStyleClass().add("section-title");
                videoSectionsContainer.getChildren().add(sectionTitle);
                
                // Create horizontal video grid for this section
                HBox videoGrid = createVideoGrid(section.getVideos());
                videoSectionsContainer.getChildren().add(videoGrid);
                
            } catch (Exception e) {
                System.err.println("Error displaying section " + section.getTitle() + ": " + e.getMessage());
            }
        }
    }
    
    private HBox createVideoGrid(List<Video> videos) {
        HBox grid = new HBox(10.0);
        grid.getStyleClass().add("video-grid");
        
        for (Video video : videos) {
            try {
                VBox videoCard = createVideoCard(video);
                grid.getChildren().add(videoCard);
            } catch (Exception e) {
                System.err.println("Error creating video card: " + e.getMessage());
            }
        }
        
        return grid;
    }
    
    private VBox createVideoCard(Video video) {
        VBox card = new VBox(5.0);
        card.getStyleClass().add("video-card");
        card.setMaxWidth(280.0);
        card.setMinWidth(280.0);
        
        // Thumbnail placeholder
        Region thumbnail = new Region();
        thumbnail.getStyleClass().add("video-thumbnail");
        thumbnail.setPrefSize(280.0, 157.0); // 16:9 aspect ratio
        
        // Video title
        Label title = new Label(video.getTitle());
        title.getStyleClass().add("video-title");
        title.setWrapText(true);
        title.setMaxHeight(40.0);
        
        // Channel name
        Label channel = new Label(video.getChannelName());
        channel.getStyleClass().add("video-channel");
        
        // Video info (views, duration, etc.)
        Label info = new Label(formatVideoInfo(video));
        info.getStyleClass().add("video-info");
        
        card.getChildren().addAll(thumbnail, title, channel, info);
        
        // Add click handler
        card.setOnMouseClicked(e -> onVideoSelected(video));
        
        return card;
    }
    
    private String formatVideoInfo(Video video) {
        StringBuilder info = new StringBuilder();
        
        if (video.getViewCount() > 0) {
            info.append(formatViewCount(video.getViewCount())).append(" views");
        }
          if (video.getDurationText() != null && !video.getDurationText().isEmpty()) {
            if (info.length() > 0) info.append(" • ");
            info.append(video.getDurationText());
        }
        
        return info.toString();
    }
    
    private String formatViewCount(long views) {
        if (views >= 1000000) {
            return String.format("%.1fM", views / 1000000.0);
        } else if (views >= 1000) {
            return String.format("%.1fK", views / 1000.0);
        } else {
            return String.valueOf(views);
        }
    }
      private void onVideoSelected(Video video) {
        updateStatus("Loading video: " + video.getTitle());
        // TODO: Open video player or navigate to video details
        System.out.println("Video selected: " + video.getTitle());
    }
    
    private void performSearch(String query) {
        updateStatus("Searching for: " + query);
        
        CompletableFuture.runAsync(() -> {
            try {
                // TODO: Implement actual search
                VideoGroup searchResults = createSampleVideoGroup("Search Results for \"" + query + "\"", 15);
                List<VideoGroup> sections = List.of(searchResults);
                
                Platform.runLater(() -> {
                    displayVideoSections(sections);
                    updateStatus("Search completed");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    updateStatus("Search error: " + e.getMessage());
                });
            }
        });
    }
    
    private void loadSettingsView() {
        try {
            // Load settings view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsView.fxml"));
            Node settingsNode = loader.load();
            
            videoSectionsContainer.getChildren().clear();
            videoSectionsContainer.getChildren().add(settingsNode);
            
            updateStatus("Settings loaded");
        } catch (IOException e) {
            updateStatus("Error loading settings: " + e.getMessage());
        }
    }
    
    private void updateStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }
    
    private void updateConnectionStatus(String status) {
        if (connectionStatus != null) {
            connectionStatus.setText(status);
        }
    }
    
    // Public methods for external access
    public void refreshCurrentSection() {
        loadVideoSections(currentSection);
    }
    
    public void searchFor(String query) {
        searchField.setText(query);
        onSearch();
    }
}
