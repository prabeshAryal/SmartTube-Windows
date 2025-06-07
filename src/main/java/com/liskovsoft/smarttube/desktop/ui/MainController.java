package com.liskovsoft.smarttube.desktop.ui;

import com.liskovsoft.smarttube.desktop.model.Video;
import com.liskovsoft.smarttube.desktop.model.VideoFormat;
import com.liskovsoft.smarttube.desktop.player.VideoPlayer;
import com.liskovsoft.smarttube.desktop.service.YouTubeService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * Main controller for the SmartTube Desktop application UI
 */
public class MainController implements Initializable {
    
    // FXML Components
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button trendingButton;
    @FXML private StackPane videoContainer;
    @FXML private MediaView mediaView;
    @FXML private VBox controlsOverlay;
    @FXML private Slider progressSlider;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Button playPauseButton;
    @FXML private Button stopButton;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button muteButton;
    @FXML private Slider volumeSlider;
    @FXML private ComboBox<String> qualityComboBox;
    @FXML private Button fullscreenButton;
    @FXML private Label videoTitleLabel;
    @FXML private Label channelLabel;
    @FXML private Label viewCountLabel;
    @FXML private Label uploadDateLabel;
    @FXML private Label videoDescriptionLabel;
    @FXML private ListView<Video> searchResultsList;
    @FXML private ListView<Video> playlistView;
    @FXML private ProgressIndicator searchProgressIndicator;
    @FXML private Button clearPlaylistButton;
    @FXML private Button shufflePlaylistButton;
    @FXML private Label statusLabel;
    @FXML private Label connectionStatusLabel;
    @FXML private CheckMenuItem fullscreenMenuItem;
    @FXML private CheckMenuItem alwaysOnTopMenuItem;
    
    // Services and Components
    private Stage primaryStage;
    private VideoPlayer videoPlayer;
    private YouTubeService youTubeService;
    
    // State
    private Video currentVideo;
    private ObservableList<Video> playlist;
    private int currentPlaylistIndex = -1;
    private boolean isControlsVisible = true;
    private boolean isMuted = false;
    private double lastVolume = 0.5;
    
    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        setupUI();
        setupEventHandlers();
        updateStatus("Ready");
    }
      private void initializeServices() {
        youTubeService = new YouTubeService();
        videoPlayer = new VideoPlayer(mediaView);
        playlist = FXCollections.observableArrayList();
        
        // Bind playlist to playlist view
        playlistView.setItems(playlist);
    }
    
    private void setupUI() {
        // Setup list views with custom cell factories
        searchResultsList.setCellFactory(listView -> new VideoListCell());
        playlistView.setCellFactory(listView -> new VideoListCell());
        
        // Setup quality combo box
        qualityComboBox.getItems().addAll("Auto", "1080p", "720p", "480p", "360p", "240p");
        qualityComboBox.setValue("Auto");
        
        // Setup volume slider
        volumeSlider.setValue(50);
        lastVolume = 0.5;
        
        // Setup progress slider
        progressSlider.setMin(0);
        progressSlider.setValue(0);
        
        // Initial button states
        playPauseButton.setText("▶");
        muteButton.setText("🔊");
        
        // Hide controls initially
        controlsOverlay.setVisible(false);
        
        // Setup mouse event for showing/hiding controls
        videoContainer.setOnMouseMoved(e -> showControls());
        videoContainer.setOnMouseExited(e -> hideControlsDelayed());
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchField.setOnAction(e -> searchVideos());
        searchButton.setOnAction(e -> searchVideos());
        trendingButton.setOnAction(e -> loadTrending());
        
        // Video selection
        searchResultsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Video selected = searchResultsList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    playVideo(selected);
                }
            }
        });
        
        // Playlist functionality
        playlistView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Video selected = playlistView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    currentPlaylistIndex = playlist.indexOf(selected);
                    playVideo(selected);
                }
            }
        });
        
        // Player controls
        playPauseButton.setOnAction(e -> playPause());
        stopButton.setOnAction(e -> stop());
        previousButton.setOnAction(e -> previousVideo());
        nextButton.setOnAction(e -> nextVideo());
        muteButton.setOnAction(e -> toggleMute());
        fullscreenButton.setOnAction(e -> toggleFullscreen());
        
        // Volume and progress sliders
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isMuted) {
                videoPlayer.setVolume(newVal.doubleValue() / 100.0);
                lastVolume = newVal.doubleValue() / 100.0;
            }
        });
        
        progressSlider.setOnMousePressed(e -> {
            if (currentVideo != null) {
                double progress = progressSlider.getValue() / progressSlider.getMax();
                videoPlayer.seek(progress);
            }
        });
        
        // Quality selection
        qualityComboBox.setOnAction(e -> changeQuality());
        
        // Playlist controls
        clearPlaylistButton.setOnAction(e -> clearPlaylist());
        shufflePlaylistButton.setOnAction(e -> shufflePlaylist());
        
        // Context menu for search results
        searchResultsList.setContextMenu(createVideoContextMenu());
        
        // Video player event handlers
        setupVideoPlayerHandlers();
    }
      private void setupVideoPlayerHandlers() {
        // Note: VideoPlayer doesn't have built-in event handlers like setOnTimeUpdate
        // We'll need to implement a timer-based solution for progress updates
        // For now, we'll skip the automatic progress updates
        
        // TODO: Implement periodic progress updates using Timeline or Timer
        // This would require checking VideoPlayer methods for current/total time
    }
    
    // Menu Actions
    @FXML
    private void openUrl() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Open URL");
        dialog.setHeaderText("Enter YouTube URL");
        dialog.setContentText("URL:");
        
        dialog.showAndWait().ifPresent(url -> {
            if (url.contains("youtube.com") || url.contains("youtu.be")) {
                loadVideoFromUrl(url);
            } else {
                showAlert("Invalid URL", "Please enter a valid YouTube URL");
            }
        });
    }
    
    @FXML
    private void exit() {
        Platform.exit();
    }
    
    @FXML
    private void toggleFullscreen() {
        if (primaryStage != null) {
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
            fullscreenMenuItem.setSelected(primaryStage.isFullScreen());
        }
    }
    
    @FXML
    private void toggleAlwaysOnTop() {
        if (primaryStage != null) {
            primaryStage.setAlwaysOnTop(!primaryStage.isAlwaysOnTop());
            alwaysOnTopMenuItem.setSelected(primaryStage.isAlwaysOnTop());
        }
    }
    
    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About SmartTube Desktop");
        alert.setHeaderText("SmartTube Desktop v1.0.0");
        alert.setContentText("A desktop YouTube player built with JavaFX.\n\n" +
                            "Features:\n" +
                            "• Search and play YouTube videos\n" +
                            "• Playlist management\n" +
                            "• Quality selection\n" +
                            "• Keyboard shortcuts\n" +
                            "• Fullscreen support");
        alert.showAndWait();
    }
    
    // Player Controls
    @FXML
    private void playPause() {
        if (videoPlayer.isPlaying()) {
            videoPlayer.pause();
            playPauseButton.setText("▶");
            updateStatus("Paused");
        } else {
            videoPlayer.play();
            playPauseButton.setText("⏸");
            updateStatus("Playing");
        }
    }
    
    @FXML
    private void stop() {
        videoPlayer.stop();
        playPauseButton.setText("▶");
        progressSlider.setValue(0);
        currentTimeLabel.setText("00:00");
        updateStatus("Stopped");
    }
    
    @FXML
    private void previousVideo() {
        if (currentPlaylistIndex > 0) {
            currentPlaylistIndex--;
            Video video = playlist.get(currentPlaylistIndex);
            playVideo(video);
            playlistView.getSelectionModel().select(currentPlaylistIndex);
        }
    }
    
    @FXML
    private void nextVideo() {
        if (currentPlaylistIndex >= 0 && currentPlaylistIndex < playlist.size() - 1) {
            currentPlaylistIndex++;
            Video video = playlist.get(currentPlaylistIndex);
            playVideo(video);
            playlistView.getSelectionModel().select(currentPlaylistIndex);
        }
    }
    
    @FXML
    private void toggleMute() {
        if (isMuted) {
            videoPlayer.setVolume(lastVolume);
            volumeSlider.setValue(lastVolume * 100);
            muteButton.setText("🔊");
            isMuted = false;
        } else {
            lastVolume = volumeSlider.getValue() / 100.0;
            videoPlayer.setVolume(0);
            muteButton.setText("🔇");
            isMuted = true;
        }
    }
    
    @FXML
    private void changeQuality() {
        if (currentVideo != null) {
            String selectedQuality = qualityComboBox.getValue();
            // Reload video with new quality
            loadVideoDetails(currentVideo.getId()).thenAccept(video -> {
                if (video != null) {
                    Platform.runLater(() -> {
                        VideoFormat format = selectBestFormat(video.getFormats(), selectedQuality);
                        if (format != null) {
                            videoPlayer.loadVideo(format.getUrl());
                            videoPlayer.play();
                        }
                    });
                }
            });
        }
    }
    
    // Speed controls
    @FXML private void setSpeed0_5() { videoPlayer.setPlaybackRate(0.5); }
    @FXML private void setSpeed0_75() { videoPlayer.setPlaybackRate(0.75); }
    @FXML private void setSpeed1_0() { videoPlayer.setPlaybackRate(1.0); }
    @FXML private void setSpeed1_25() { videoPlayer.setPlaybackRate(1.25); }
    @FXML private void setSpeed1_5() { videoPlayer.setPlaybackRate(1.5); }
    @FXML private void setSpeed2_0() { videoPlayer.setPlaybackRate(2.0); }
    
    // Search and Content Loading
    @FXML
    private void searchVideos() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            searchProgressIndicator.setVisible(true);
            updateStatus("Searching...");
            
            youTubeService.searchVideos(query, 20).thenAccept(videos -> {
                Platform.runLater(() -> {
                    searchResultsList.getItems().clear();
                    searchResultsList.getItems().addAll(videos);
                    searchProgressIndicator.setVisible(false);
                    updateStatus("Found " + videos.size() + " videos");
                });
            }).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    searchProgressIndicator.setVisible(false);
                    showAlert("Search Error", "Error searching videos: " + throwable.getMessage());
                    updateStatus("Search failed");
                });
                return null;
            });
        }
    }
    
    @FXML
    private void loadTrending() {
        searchProgressIndicator.setVisible(true);
        updateStatus("Loading trending videos...");
        
        youTubeService.getTrendingVideos(20).thenAccept(videos -> {
            Platform.runLater(() -> {
                searchResultsList.getItems().clear();
                searchResultsList.getItems().addAll(videos);
                searchProgressIndicator.setVisible(false);
                updateStatus("Loaded trending videos");
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                searchProgressIndicator.setVisible(false);
                showAlert("Error", "Error loading trending videos: " + throwable.getMessage());
                updateStatus("Failed to load trending");
            });
            return null;
        });
    }
    
    // Playlist Management
    @FXML
    private void clearPlaylist() {
        playlist.clear();
        currentPlaylistIndex = -1;
        updateStatus("Playlist cleared");
    }
    
    @FXML
    private void shufflePlaylist() {
        if (!playlist.isEmpty()) {
            Collections.shuffle(playlist);
            updateStatus("Playlist shuffled");
        }
    }
    
    // Video Loading and Playback
    private void playVideo(Video video) {
        currentVideo = video;
        updateVideoInfo(video);
        updateStatus("Loading video...");
        
        loadVideoDetails(video.getId()).thenAccept(detailedVideo -> {
            if (detailedVideo != null) {
                Platform.runLater(() -> {
                    String quality = qualityComboBox.getValue();
                    VideoFormat format = selectBestFormat(detailedVideo.getFormats(), quality);
                    
                    if (format != null) {
                        videoPlayer.loadVideo(format.getUrl());
                        videoPlayer.play();
                        playPauseButton.setText("⏸");
                        updateStatus("Playing: " + video.getTitle());
                        
                        // Add to playlist if not already there
                        if (!playlist.contains(video)) {
                            playlist.add(video);
                            currentPlaylistIndex = playlist.size() - 1;
                        } else {
                            currentPlaylistIndex = playlist.indexOf(video);
                        }
                        
                        playlistView.getSelectionModel().select(currentPlaylistIndex);
                    } else {
                        showAlert("Playback Error", "No suitable video format found");
                        updateStatus("No playable format found");
                    }
                });
            }
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showAlert("Video Error", "Error loading video: " + throwable.getMessage());
                updateStatus("Video load failed");
            });
            return null;
        });
    }
    
    private void loadVideoFromUrl(String url) {
        updateStatus("Loading video from URL...");
        
        youTubeService.getVideoDetails(url).thenAccept(video -> {
            if (video != null) {
                Platform.runLater(() -> {
                    playVideo(video);
                });
            } else {
                Platform.runLater(() -> {
                    showAlert("Error", "Could not load video from URL");
                    updateStatus("Failed to load video");
                });
            }
        });
    }
    
    private CompletableFuture<Video> loadVideoDetails(String videoId) {
        String url = "https://www.youtube.com/watch?v=" + videoId;
        return youTubeService.getVideoDetails(url);
    }
    
    private VideoFormat selectBestFormat(List<VideoFormat> formats, String preferredQuality) {
        if (formats == null || formats.isEmpty()) {
            return null;
        }
          // Filter formats that have both video and audio
        List<VideoFormat> videoAudioFormats = formats.stream()
            .filter(f -> f.hasVideo() && f.hasAudio())
            .toList();
        
        if (!videoAudioFormats.isEmpty()) {
            if ("Auto".equals(preferredQuality)) {
                return videoAudioFormats.get(0); // First (usually best) format
            }
            
            // Find format matching preferred quality
            for (VideoFormat format : videoAudioFormats) {
                if (format.getQuality() != null && format.getQuality().contains(preferredQuality.replace("p", ""))) {
                    return format;
                }
            }
            
            return videoAudioFormats.get(0); // Fallback to first format
        }
        
        // Fallback to any format
        return formats.get(0);
    }
    
    // UI Helper Methods
    private void updateVideoInfo(Video video) {
        videoTitleLabel.setText(video.getTitle() != null ? video.getTitle() : "Unknown Title");
        channelLabel.setText(video.getChannelName() != null ? video.getChannelName() : "Unknown Channel");
        
        if (video.getViewCount() > 0) {
            viewCountLabel.setText(formatViewCount(video.getViewCount()) + " views");
        } else {
            viewCountLabel.setText("");
        }
          if (video.getUploadDate() != null) {
            uploadDateLabel.setText(video.getUploadDate());
        } else {
            uploadDateLabel.setText("");
        }
        
        if (video.getDescription() != null && !video.getDescription().isEmpty()) {
            String description = video.getDescription();
            if (description.length() > 500) {
                description = description.substring(0, 500) + "...";
            }
            videoDescriptionLabel.setText(description);
        } else {
            videoDescriptionLabel.setText("");
        }
    }
    
    private void showControls() {
        controlsOverlay.setVisible(true);
        isControlsVisible = true;
    }
    
    private void hideControlsDelayed() {
        // Hide controls after a delay when not in fullscreen
        if (!primaryStage.isFullScreen()) {
            return;
        }
        
        Platform.runLater(() -> {
            try {
                Thread.sleep(3000);
                if (isControlsVisible) {
                    controlsOverlay.setVisible(false);
                    isControlsVisible = false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    private ContextMenu createVideoContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem addToPlaylist = new MenuItem("Add to Playlist");
        addToPlaylist.setOnAction(e -> {
            Video selected = searchResultsList.getSelectionModel().getSelectedItem();
            if (selected != null && !playlist.contains(selected)) {
                playlist.add(selected);
                updateStatus("Added to playlist");
            }
        });
        
        MenuItem playNow = new MenuItem("Play Now");
        playNow.setOnAction(e -> {
            Video selected = searchResultsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                playVideo(selected);
            }
        });
        
        contextMenu.getItems().addAll(playNow, addToPlaylist);
        return contextMenu;
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
      private String formatDuration(double seconds) {
        if (seconds <= 0) {
            return "00:00";
        }
        
        long totalSeconds = (long) seconds;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long secs = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }
    
    private String formatViewCount(long viewCount) {
        if (viewCount >= 1_000_000_000) {
            return String.format("%.1fB", viewCount / 1_000_000_000.0);
        } else if (viewCount >= 1_000_000) {
            return String.format("%.1fM", viewCount / 1_000_000.0);
        } else if (viewCount >= 1_000) {
            return String.format("%.1fK", viewCount / 1_000.0);
        } else {
            return String.valueOf(viewCount);
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Keyboard Event Handler
    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
                playPause();
                event.consume();
                break;
            case F11:
                toggleFullscreen();
                event.consume();
                break;
            case ESCAPE:
                if (primaryStage.isFullScreen()) {
                    primaryStage.setFullScreen(false);
                }
                event.consume();
                break;
            case UP:
                if (event.isControlDown()) {
                    double newVolume = Math.min(100, volumeSlider.getValue() + 5);
                    volumeSlider.setValue(newVolume);
                    event.consume();
                }
                break;
            case DOWN:
                if (event.isControlDown()) {
                    double newVolume = Math.max(0, volumeSlider.getValue() - 5);
                    volumeSlider.setValue(newVolume);
                    event.consume();
                }
                break;            case LEFT:
                if (event.isControlDown() && currentVideo != null) {
                    double currentTime = videoPlayer.getCurrentTime();
                    videoPlayer.seek(Math.max(0, currentTime - 10));
                    event.consume();
                }
                break;
            case RIGHT:
                if (event.isControlDown() && currentVideo != null) {
                    double currentTime = videoPlayer.getCurrentTime();
                    videoPlayer.seek(currentTime + 10);
                    event.consume();
                }
                break;
            case M:
                toggleMute();
                event.consume();
                break;
        }
    }
}
