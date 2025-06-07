package com.liskovsoft.smarttube.desktop.player;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhanced video player for SmartTube Desktop
 * Supports various video formats and provides advanced playback controls
 */
public class VideoPlayer {
    
    private static final Logger logger = LoggerFactory.getLogger(VideoPlayer.class);
    
    private final MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private Media currentMedia;
    private boolean isInitialized = false;
    
    public VideoPlayer(MediaView mediaView) {
        this.mediaView = mediaView;
        setupMediaView();
    }
    
    private void setupMediaView() {
        // Configure media view for optimal playback
        mediaView.setPreserveRatio(true);
        mediaView.setSmooth(true);
        mediaView.setFitWidth(800);
        mediaView.setFitHeight(600);
    }
    
    public void loadVideo(String videoUrl) {
        logger.info("Loading video: {}", videoUrl);
        
        try {
            // Clean up previous media player
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            
            // Create new media and player
            currentMedia = new Media(videoUrl);
            mediaPlayer = new MediaPlayer(currentMedia);
            
            // Set up media player
            setupMediaPlayer();
            
            // Bind to media view
            mediaView.setMediaPlayer(mediaPlayer);
            
            isInitialized = true;
            logger.info("Video loaded successfully");
            
        } catch (Exception e) {
            logger.error("Failed to load video: {}", videoUrl, e);
            isInitialized = false;
        }
    }
    
    private void setupMediaPlayer() {
        if (mediaPlayer == null) return;
        
        // Set up event handlers
        mediaPlayer.setOnReady(() -> {
            logger.info("Media player ready - Duration: {} seconds", 
                       mediaPlayer.getTotalDuration().toSeconds());
        });
        
        mediaPlayer.setOnError(() -> {
            if (mediaPlayer.getError() != null) {
                logger.error("Media player error: {}", mediaPlayer.getError().getMessage());
            }
        });
        
        mediaPlayer.setOnEndOfMedia(() -> {
            logger.info("End of media reached");
        });
        
        mediaPlayer.setOnPlaying(() -> {
            logger.debug("Media player started playing");
        });
        
        mediaPlayer.setOnPaused(() -> {
            logger.debug("Media player paused");
        });
        
        mediaPlayer.setOnStopped(() -> {
            logger.debug("Media player stopped");
        });
        
        // Auto-play disabled by default
        mediaPlayer.setAutoPlay(false);
        
        // Set volume to 50% by default
        mediaPlayer.setVolume(0.5);
    }
    
    public void play() {
        if (isInitialized && mediaPlayer != null) {
            Platform.runLater(() -> {
                try {
                    mediaPlayer.play();
                    logger.debug("Playing video");
                } catch (Exception e) {
                    logger.error("Error playing video", e);
                }
            });
        }
    }
    
    public void pause() {
        if (isInitialized && mediaPlayer != null) {
            Platform.runLater(() -> {
                try {
                    mediaPlayer.pause();
                    logger.debug("Paused video");
                } catch (Exception e) {
                    logger.error("Error pausing video", e);
                }
            });
        }
    }
    
    public void stop() {
        if (isInitialized && mediaPlayer != null) {
            Platform.runLater(() -> {
                try {
                    mediaPlayer.stop();
                    logger.debug("Stopped video");
                } catch (Exception e) {
                    logger.error("Error stopping video", e);
                }
            });
        }
    }
    
    public void seek(double seconds) {
        if (isInitialized && mediaPlayer != null) {
            Platform.runLater(() -> {
                try {
                    Duration seekTime = Duration.seconds(seconds);
                    mediaPlayer.seek(seekTime);
                    logger.debug("Seeking to {} seconds", seconds);
                } catch (Exception e) {
                    logger.error("Error seeking video", e);
                }
            });
        }
    }
    
    public void setVolume(double volume) {
        if (isInitialized && mediaPlayer != null) {
            Platform.runLater(() -> {
                try {
                    // Clamp volume between 0.0 and 1.0
                    double clampedVolume = Math.max(0.0, Math.min(1.0, volume));
                    mediaPlayer.setVolume(clampedVolume);
                    logger.debug("Set volume to {}", clampedVolume);
                } catch (Exception e) {
                    logger.error("Error setting volume", e);
                }
            });
        }
    }
    
    public double getCurrentTime() {
        if (isInitialized && mediaPlayer != null) {
            try {
                return mediaPlayer.getCurrentTime().toSeconds();
            } catch (Exception e) {
                logger.error("Error getting current time", e);
            }
        }
        return 0.0;
    }
    
    public double getTotalTime() {
        if (isInitialized && mediaPlayer != null) {
            try {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                return totalDuration != null ? totalDuration.toSeconds() : 0.0;
            } catch (Exception e) {
                logger.error("Error getting total time", e);
            }
        }
        return 0.0;
    }
    
    public boolean isPlaying() {
        if (isInitialized && mediaPlayer != null) {
            try {
                return mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
            } catch (Exception e) {
                logger.error("Error checking playing status", e);
            }
        }
        return false;
    }
    
    public void setPlaybackRate(double rate) {
        if (isInitialized && mediaPlayer != null) {
            Platform.runLater(() -> {
                try {
                    // Clamp playback rate between 0.1 and 3.0
                    double clampedRate = Math.max(0.1, Math.min(3.0, rate));
                    mediaPlayer.setRate(clampedRate);
                    logger.debug("Set playback rate to {}", clampedRate);
                } catch (Exception e) {
                    logger.error("Error setting playback rate", e);
                }
            });
        }
    }
    
    public void setMute(boolean mute) {
        if (isInitialized && mediaPlayer != null) {
            Platform.runLater(() -> {
                try {
                    mediaPlayer.setMute(mute);
                    logger.debug("Set mute to {}", mute);
                } catch (Exception e) {
                    logger.error("Error setting mute", e);
                }
            });
        }
    }
    
    public void dispose() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
                currentMedia = null;
                isInitialized = false;
                logger.info("Media player disposed");
            } catch (Exception e) {
                logger.error("Error disposing media player", e);
            }
        }
    }
}
