package com.liskovsoft.smarttube.desktop.ui;

import com.liskovsoft.smarttube.desktop.model.Video;
import com.liskovsoft.smarttube.desktop.service.YouTubeService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main controller for the SmartTube Desktop application
 */
public class MainController implements Initializable {

    // FXML components
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<Video> searchResults;
    @FXML private MediaView mediaView;
    @FXML private VBox playerControls;
    @FXML private Button playPauseButton;
    @FXML private Slider progressSlider;
    @FXML private Slider volumeSlider;
    @FXML private Label timeLabel;
    @FXML private ComboBox<String> qualityComboBox;
    @FXML private ListView<Video> playlistView;
    @FXML private Label statusLabel;
    @FXML private VBox sidebar;

    // Application state
    private Stage stage;
    private MediaPlayer mediaPlayer;
    private YouTubeService youTubeService;
    private ObservableList<Video> searchResultsList;
    private ObservableList<Video> playlistItems;
    private Video currentVideo;    @Override
    public void initialize(URL location, ResourceBundle resources) {
        youTubeService = new YouTubeService();
        
        // Initialize collections
        searchResultsList = FXCollections.observableArrayList();
        playlistItems = FXCollections.observableArrayList();
        
        // Set up list views
        searchResults.setItems(searchResultsList);
        playlistView.setItems(playlistItems);
        
        // Set up custom cell factories
        searchResults.setCellFactory(listView -> new VideoListCell());
        playlistView.setCellFactory(listView -> new VideoListCell());
        
        // Set up event handlers
        setupEventHandlers();
        
        // Initialize UI state
        setupUI();
        
        updateStatus("SmartTube Desktop ready");
    }

    private void setupEventHandlers() {
        // Search functionality
        searchButton.setOnAction(e -> performSearch());
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performSearch();
            }
        });

        // Video selection
        searchResults.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Video selected = searchResults.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    playVideo(selected);
                }
            }
        });

        // Player controls
        playPauseButton.setOnAction(e -> togglePlayPause());
        
        // Volume control
        if (volumeSlider != null) {
            volumeSlider.setValue(50);
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(newVal.doubleValue() / 100.0);
                }
            });
        }

        // Quality selection
        if (qualityComboBox != null) {
            qualityComboBox.getItems().addAll("Auto", "1080p", "720p", "480p", "360p");
            qualityComboBox.setValue("Auto");
        }
    }

    private void setupUI() {
        // Set initial button text
        if (playPauseButton != null) {
            playPauseButton.setText("▶");
        }
        
        // Initialize progress slider
        if (progressSlider != null) {
            progressSlider.setValue(0);
        }
        
        // Initialize time label
        if (timeLabel != null) {
            timeLabel.setText("00:00 / 00:00");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            return;
        }

        updateStatus("Searching for: " + query);
        
        // Clear previous results
        searchResultsList.clear();        // Perform search in background
        Task<List<Video>> searchTask = new Task<List<Video>>() {
            @Override
            protected List<Video> call() throws Exception {
                return youTubeService.searchVideos(query, 20).join();
            }

            @Override
            protected void succeeded() {
                List<Video> results = getValue();
                Platform.runLater(() -> {
                    searchResultsList.addAll(results);
                    updateStatus("Found " + results.size() + " videos");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    updateStatus("Search failed: " + getException().getMessage());
                });
            }
        };

        new Thread(searchTask).start();
    }

    private void playVideo(Video video) {
        currentVideo = video;
        updateStatus("Loading video: " + video.getTitle());

        // Get stream URL in background
        Task<String> streamTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return youTubeService.getStreamUrl(video.getVideoId(), "720p").join();
            }

            @Override
            protected void succeeded() {
                String streamUrl = getValue();
                Platform.runLater(() -> {
                    playMediaFromUrl(streamUrl);
                    addToPlaylist(video);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    updateStatus("Failed to load video: " + getException().getMessage());
                });
            }
        };

        new Thread(streamTask).start();
    }

    private void playMediaFromUrl(String url) {
        try {
            // Stop current player if exists
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            // Create new media player
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            
            if (mediaView != null) {
                mediaView.setMediaPlayer(mediaPlayer);
            }

            // Set up player event handlers
            setupMediaPlayerHandlers();

            // Start playback
            mediaPlayer.play();
            updateStatus("Playing: " + currentVideo.getTitle());
            
            if (playPauseButton != null) {
                playPauseButton.setText("⏸");
            }

        } catch (Exception e) {
            updateStatus("Error playing video: " + e.getMessage());
        }
    }

    private void setupMediaPlayerHandlers() {
        if (mediaPlayer == null) return;

        mediaPlayer.setOnReady(() -> {
            // Set up progress tracking
            if (progressSlider != null) {
                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (!progressSlider.isValueChanging()) {
                        double progress = newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds();
                        progressSlider.setValue(progress * 100);
                    }
                    updateTimeLabel();
                });
            }
        });

        mediaPlayer.setOnError(() -> {
            updateStatus("Media player error: " + mediaPlayer.getError().getMessage());
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            // Auto-play next video in playlist if available
            playNextInPlaylist();
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            if (playPauseButton != null) {
                playPauseButton.setText("▶");
            }
        } else {
            mediaPlayer.play();
            if (playPauseButton != null) {
                playPauseButton.setText("⏸");
            }
        }
    }

    private void addToPlaylist(Video video) {
        if (!playlistItems.contains(video)) {
            playlistItems.add(video);
        }
    }

    private void playNextInPlaylist() {
        if (currentVideo == null || playlistItems.isEmpty()) return;

        int currentIndex = playlistItems.indexOf(currentVideo);
        if (currentIndex >= 0 && currentIndex < playlistItems.size() - 1) {
            Video nextVideo = playlistItems.get(currentIndex + 1);
            playVideo(nextVideo);
        }
    }

    private void updateTimeLabel() {
        if (mediaPlayer == null || timeLabel == null) return;

        double currentSeconds = mediaPlayer.getCurrentTime().toSeconds();
        double totalSeconds = mediaPlayer.getTotalDuration().toSeconds();
        
        String currentTime = formatTime(currentSeconds);
        String totalTime = formatTime(totalSeconds);
        
        timeLabel.setText(currentTime + " / " + totalTime);
    }

    private String formatTime(double seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            Platform.runLater(() -> statusLabel.setText(message));
        }
        System.out.println("Status: " + message);
    }

    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
                if (event.isControlDown()) {
                    togglePlayPause();
                    event.consume();
                }
                break;
            case F:
                if (event.isControlDown() && searchField != null) {
                    searchField.requestFocus();
                    event.consume();
                }
                break;
            case ESCAPE:
                if (stage != null && stage.isFullScreen()) {
                    stage.setFullScreen(false);
                    event.consume();
                }
                break;
            case F11:
                if (stage != null) {
                    stage.setFullScreen(!stage.isFullScreen());
                    event.consume();
                }
                break;
        }
    }

    // Cleanup method
    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }
}