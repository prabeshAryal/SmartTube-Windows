package com.liskovsoft.smarttube.desktop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of videos with a title (e.g., "Trending", "Recommended", etc.)
 * Similar to the VideoGroup class in SmartTube-TV Android app
 */
public class VideoGroup {
    private String title;
    private String subtitle;
    private List<Video> videos;
    private String id;
    private String nextPageKey;
    private boolean isAuthRequired;
    
    public VideoGroup() {
        this.videos = new ArrayList<>();
    }
    
    public VideoGroup(String title) {
        this();
        this.title = title;
    }
    
    public VideoGroup(String title, List<Video> videos) {
        this.title = title;
        this.videos = videos != null ? new ArrayList<>(videos) : new ArrayList<>();
    }
    
    // Getters and setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSubtitle() {
        return subtitle;
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    
    public List<Video> getVideos() {
        return videos;
    }
    
    public void setVideos(List<Video> videos) {
        this.videos = videos != null ? new ArrayList<>(videos) : new ArrayList<>();
    }
    
    public void addVideo(Video video) {
        if (video != null) {
            this.videos.add(video);
        }
    }
    
    public void addVideos(List<Video> videos) {
        if (videos != null) {
            this.videos.addAll(videos);
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNextPageKey() {
        return nextPageKey;
    }
    
    public void setNextPageKey(String nextPageKey) {
        this.nextPageKey = nextPageKey;
    }
    
    public boolean isAuthRequired() {
        return isAuthRequired;
    }
    
    public void setAuthRequired(boolean authRequired) {
        isAuthRequired = authRequired;
    }
    
    public boolean isEmpty() {
        return videos == null || videos.isEmpty();
    }
    
    public int size() {
        return videos != null ? videos.size() : 0;
    }
    
    public void clear() {
        if (videos != null) {
            videos.clear();
        }
    }
    
    @Override
    public String toString() {
        return "VideoGroup{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", videoCount=" + (videos != null ? videos.size() : 0) +
                ", id='" + id + '\'' +
                ", nextPageKey='" + nextPageKey + '\'' +
                ", isAuthRequired=" + isAuthRequired +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        VideoGroup that = (VideoGroup) o;
        
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        
        return title != null ? title.equals(that.title) : that.title == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (title != null ? title.hashCode() : 0);
    }
}
