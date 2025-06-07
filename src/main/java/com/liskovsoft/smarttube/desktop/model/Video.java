package com.liskovsoft.smarttube.desktop.model;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Video model representing a YouTube video with all its metadata
 */
public class Video {    private String videoId;
    private String title;
    private String description;
    private String channelName;
    private String channelId;
    private String channelUrl;
    private String thumbnailUrl;
    private Duration duration;
    private String durationText;
    private long viewCount;
    private long likes;
    private String uploadDate;
    private String streamUrl;
    private List<VideoFormat> formats;
    private boolean isLive;
    private String category;
    private List<String> tags;
    
    public Video() {}
    
    public Video(String videoId, String title) {
        this.videoId = videoId;
        this.title = title;
    }
    
    // Getters and setters
    public String getVideoId() {
        return videoId;
    }
    
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getChannelName() {
        return channelName;
    }
    
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    
    public String getChannelId() {
        return channelId;
    }
    
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public Duration getDuration() {
        return duration;
    }
    
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    public String getDurationText() {
        return durationText;
    }
    
    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }
    
    public long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
    
    public String getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
    
    public List<VideoFormat> getFormats() {
        return formats;
    }
    
    public void setFormats(List<VideoFormat> formats) {
        this.formats = formats;
    }
    
    public boolean isLive() {
        return isLive;
    }
    
    public void setLive(boolean live) {
        isLive = live;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    // Additional methods needed by YouTubeService
    public String getId() {
        return videoId;
    }
    
    public void setId(String id) {
        this.videoId = id;
    }
    
    public String getChannelUrl() {
        return channelUrl;
    }
    
    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }
    
    public long getLikes() {
        return likes;
    }
    
    public void setLikes(long likes) {
        this.likes = likes;
    }
    
    public String getStreamUrl() {
        return streamUrl;
    }
    
    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
    
    // Utility methods
    public String getYouTubeUrl() {
        return "https://www.youtube.com/watch?v=" + videoId;
    }
    
    public String getFormattedViewCount() {
        if (viewCount < 1000) {
            return String.valueOf(viewCount);
        } else if (viewCount < 1000000) {
            return String.format("%.1fK", viewCount / 1000.0);
        } else if (viewCount < 1000000000) {
            return String.format("%.1fM", viewCount / 1000000.0);
        } else {
            return String.format("%.1fB", viewCount / 1000000000.0);
        }
    }
    
    public String getFormattedDuration() {
        if (duration == null) return durationText != null ? durationText : "Unknown";
        
        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    public VideoFormat getBestFormat(String preferredQuality) {
        if (formats == null || formats.isEmpty()) {
            return null;
        }
        
        // Try to find exact quality match
        for (VideoFormat format : formats) {
            if (format.getQualityLabel().equals(preferredQuality)) {
                return format;
            }
        }
        
        // If no exact match, return highest quality
        return formats.stream()
                .filter(f -> f.hasVideo() && f.hasAudio())
                .max((f1, f2) -> Integer.compare(f1.getHeight(), f2.getHeight()))
                .orElse(formats.get(0));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(videoId, video.videoId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(videoId);
    }
    
    @Override
    public String toString() {
        return "Video{" +
               "videoId='" + videoId + '\'' +
               ", title='" + title + '\'' +
               ", channelName='" + channelName + '\'' +
               ", duration=" + getFormattedDuration() +
               '}';
    }
}
