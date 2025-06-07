package com.liskovsoft.smarttube.desktop.model;

/**
 * Represents a video format/stream with specific quality and codec information
 */
public class VideoFormat {
    
    private String url;
    private String mimeType;
    private String qualityLabel;
    private String codec;
    private int width;
    private int height;
    private int fps;
    private long bitrate;
    private long contentLength;
    private boolean hasVideo;
    private boolean hasAudio;
    private String audioCodec;
    private int audioBitrate;
    private int audioSampleRate;
    
    public VideoFormat() {}
    
    public VideoFormat(String url, String qualityLabel) {
        this.url = url;
        this.qualityLabel = qualityLabel;
    }
    
    // Getters and setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getQualityLabel() {
        return qualityLabel;
    }
    
    public void setQualityLabel(String qualityLabel) {
        this.qualityLabel = qualityLabel;
    }
    
    public String getCodec() {
        return codec;
    }
    
    public void setCodec(String codec) {
        this.codec = codec;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getFps() {
        return fps;
    }
    
    public void setFps(int fps) {
        this.fps = fps;
    }
    
    public long getBitrate() {
        return bitrate;
    }
    
    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }
    
    public long getContentLength() {
        return contentLength;
    }
    
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }
    
    public boolean hasVideo() {
        return hasVideo;
    }
    
    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }
    
    public boolean hasAudio() {
        return hasAudio;
    }
    
    public void setHasAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }
    
    public String getAudioCodec() {
        return audioCodec;
    }
    
    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }
    
    public int getAudioBitrate() {
        return audioBitrate;
    }
    
    public void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = audioBitrate;
    }
    
    public int getAudioSampleRate() {
        return audioSampleRate;
    }
    
    public void setAudioSampleRate(int audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }
    
    // Additional methods needed by YouTubeService
    public String getQuality() {
        return qualityLabel;
    }
    
    public void setQuality(String quality) {
        this.qualityLabel = quality;
    }
    
    public String getVideoCodec() {
        return codec;
    }
    
    public void setVideoCodec(String videoCodec) {
        this.codec = videoCodec;
    }
    
    // Utility methods
    public String getResolution() {
        if (width > 0 && height > 0) {
            return width + "x" + height;
        }
        return qualityLabel != null ? qualityLabel : "Unknown";
    }
    
    public String getFormattedBitrate() {
        if (bitrate <= 0) return "Unknown";
        
        if (bitrate < 1000) {
            return bitrate + " bps";
        } else if (bitrate < 1000000) {
            return String.format("%.1f Kbps", bitrate / 1000.0);
        } else {
            return String.format("%.1f Mbps", bitrate / 1000000.0);
        }
    }
    
    public String getFormattedSize() {
        if (contentLength <= 0) return "Unknown";
        
        if (contentLength < 1024) {
            return contentLength + " B";
        } else if (contentLength < 1024 * 1024) {
            return String.format("%.1f KB", contentLength / 1024.0);
        } else if (contentLength < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", contentLength / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", contentLength / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    public boolean isVideoOnly() {
        return hasVideo && !hasAudio;
    }
    
    public boolean isAudioOnly() {
        return hasAudio && !hasVideo;
    }
    
    public boolean isCombined() {
        return hasVideo && hasAudio;
    }
    
    @Override
    public String toString() {
        return "VideoFormat{" +
               "qualityLabel='" + qualityLabel + '\'' +
               ", resolution=" + getResolution() +
               ", hasVideo=" + hasVideo +
               ", hasAudio=" + hasAudio +
               ", bitrate=" + getFormattedBitrate() +
               '}';
    }
}
