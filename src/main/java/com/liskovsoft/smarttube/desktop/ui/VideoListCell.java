package com.liskovsoft.smarttube.desktop.ui;

import com.liskovsoft.smarttube.desktop.model.Video;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Custom list cell for displaying video items in search results and playlists
 */
public class VideoListCell extends ListCell<Video> {
    
    private final HBox content;
    private final ImageView thumbnail;
    private final VBox textContainer;
    private final Label titleLabel;
    private final Label channelLabel;
    private final Label durationLabel;
    private final Label viewCountLabel;
    
    public VideoListCell() {
        super();
        
        // Create thumbnail
        thumbnail = new ImageView();
        thumbnail.setFitWidth(120);
        thumbnail.setFitHeight(68);
        thumbnail.setPreserveRatio(true);
        thumbnail.setSmooth(true);
        thumbnail.getStyleClass().add("video-thumbnail");
        
        // Create text container
        textContainer = new VBox();
        textContainer.setSpacing(4);
        textContainer.setPadding(new Insets(0, 0, 0, 10));
        HBox.setHgrow(textContainer, Priority.ALWAYS);
        
        // Create labels
        titleLabel = new Label();
        titleLabel.getStyleClass().add("video-item-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        
        channelLabel = new Label();
        channelLabel.getStyleClass().add("video-item-channel");
        
        durationLabel = new Label();
        durationLabel.getStyleClass().add("video-item-duration");
        
        viewCountLabel = new Label();
        viewCountLabel.getStyleClass().add("video-item-channel");
        
        // Arrange text labels
        HBox metaContainer = new HBox();
        metaContainer.setSpacing(15);
        metaContainer.setAlignment(Pos.CENTER_LEFT);
        metaContainer.getChildren().addAll(channelLabel, viewCountLabel, durationLabel);
        
        textContainer.getChildren().addAll(titleLabel, metaContainer);
        
        // Create main content container
        content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(0);
        content.getStyleClass().add("video-list-item");
        content.getChildren().addAll(thumbnail, textContainer);
        
        // Set cell properties
        setGraphic(content);
        setPrefWidth(0);
    }
    
    @Override
    protected void updateItem(Video video, boolean empty) {
        super.updateItem(video, empty);
        
        if (empty || video == null) {
            setGraphic(null);
            return;
        }
        
        // Update text content
        titleLabel.setText(video.getTitle());
        channelLabel.setText(video.getChannelName() != null ? video.getChannelName() : "Unknown Channel");
          // Format duration
        if (video.getDuration() != null && !video.getDuration().isZero()) {
            durationLabel.setText(video.getFormattedDuration());
        } else {
            durationLabel.setText("");
        }
        
        // Format view count
        if (video.getViewCount() > 0) {
            viewCountLabel.setText(formatViewCount(video.getViewCount()) + " views");
        } else {
            viewCountLabel.setText("");
        }
        
        // Load thumbnail
        loadThumbnail(video.getThumbnailUrl());
        
        setGraphic(content);
    }
    
    private void loadThumbnail(String thumbnailUrl) {
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            try {
                // Load thumbnail asynchronously
                Image image = new Image(thumbnailUrl, 120, 68, true, true, true);
                thumbnail.setImage(image);
                
                // Add error handling
                image.errorProperty().addListener((obs, oldError, newError) -> {
                    if (newError) {
                        loadDefaultThumbnail();
                    }
                });
                
            } catch (Exception e) {
                loadDefaultThumbnail();
            }
        } else {
            loadDefaultThumbnail();
        }
    }
    
    private void loadDefaultThumbnail() {
        // Create a simple default thumbnail
        thumbnail.setImage(null);
        thumbnail.setStyle("-fx-background-color: #404040;");
    }
    
    private String formatDuration(long durationSeconds) {
        if (durationSeconds <= 0) {
            return "";
        }
        
        long hours = durationSeconds / 3600;
        long minutes = (durationSeconds % 3600) / 60;
        long seconds = durationSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
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
}
