package com.liskovsoft.smarttube.desktop.service;

import com.liskovsoft.smarttube.desktop.model.Video;
import com.liskovsoft.smarttube.desktop.model.VideoFormat;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.VideoStream;
import org.schabi.newpipe.extractor.stream.AudioStream;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service for interacting with YouTube using NewPipe extractor
 */
public class YouTubeService {
    
    static {
        // Initialize NewPipe
        NewPipe.init(new DownloaderImpl());
    }
    
    /**
     * Search for videos on YouTube
     */
    public CompletableFuture<List<Video>> searchVideos(String query, int maxResults) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SearchExtractor searchExtractor = ServiceList.YouTube.getSearchExtractor(query);
                searchExtractor.fetchPage();
                
                List<Video> videos = new ArrayList<>();
                List<InfoItem> items = searchExtractor.getInitialPage().getItems();
                
                int count = 0;
                for (InfoItem item : items) {
                    if (count >= maxResults) break;
                    
                    if (item instanceof StreamInfoItem) {
                        try {
                            Video video = convertStreamInfoItemToVideo((StreamInfoItem) item);
                            if (video != null) {
                                videos.add(video);
                                count++;
                            }
                        } catch (Exception e) {
                            System.err.println("Error converting video item: " + e.getMessage());
                        }
                    }
                }
                
                return videos;
                
            } catch (Exception e) {
                throw new RuntimeException("Error searching videos: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Get detailed video information including stream URLs
     */
    public CompletableFuture<Video> getVideoDetails(String videoId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                StreamExtractor streamExtractor = ServiceList.YouTube.getStreamExtractor(videoUrl);
                streamExtractor.fetchPage();
                
                StreamInfo streamInfo = StreamInfo.getInfo(streamExtractor);
                
                Video video = new Video();
                video.setId(streamInfo.getId());
                video.setTitle(streamInfo.getName());
                video.setDescription(streamInfo.getDescription().getContent());
                video.setChannelName(streamInfo.getUploaderName());
                video.setChannelUrl(streamInfo.getUploaderUrl());
                video.setDuration(Duration.ofSeconds(streamInfo.getDuration()));
                video.setViewCount(streamInfo.getViewCount());
                video.setLikes(streamInfo.getLikeCount());
                
                // Handle upload date
                if (streamInfo.getUploadDate() != null) {
                    video.setUploadDate(streamInfo.getUploadDate().offsetDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
                
                // Handle thumbnails
                if (!streamInfo.getThumbnails().isEmpty()) {
                    video.setThumbnailUrl(streamInfo.getThumbnails().get(0).getUrl());
                }
                
                // Get available formats
                List<VideoFormat> formats = new ArrayList<>();
                
                // Add video streams with audio
                for (VideoStream videoStream : streamInfo.getVideoStreams()) {
                    VideoFormat format = new VideoFormat();
                    format.setUrl(videoStream.getUrl());
                    format.setQuality(videoStream.getResolution());
                    format.setMimeType(videoStream.getFormat().getMimeType());
                    format.setHasVideo(true);
                    format.setVideoCodec(videoStream.getFormat().getName());
                    formats.add(format);
                }
                
                // Add video-only streams
                for (VideoStream videoStream : streamInfo.getVideoOnlyStreams()) {
                    VideoFormat format = new VideoFormat();
                    format.setUrl(videoStream.getUrl());
                    format.setQuality(videoStream.getResolution());
                    format.setMimeType(videoStream.getFormat().getMimeType());
                    format.setHasVideo(true);
                    format.setHasAudio(false);
                    format.setVideoCodec(videoStream.getFormat().getName());
                    formats.add(format);
                }
                
                // Add audio-only streams
                for (AudioStream audioStream : streamInfo.getAudioStreams()) {
                    VideoFormat format = new VideoFormat();
                    format.setUrl(audioStream.getUrl());
                    format.setQuality("Audio Only");
                    format.setMimeType(audioStream.getFormat().getMimeType());
                    format.setHasVideo(false);
                    format.setHasAudio(true);
                    formats.add(format);
                }
                
                video.setFormats(formats);
                
                // Set best stream URL for direct playback
                Optional<VideoStream> bestStream = streamInfo.getVideoStreams().stream()
                    .filter(stream -> stream.getResolution().contains("720"))
                    .findFirst();
                
                if (bestStream.isPresent()) {
                    video.setStreamUrl(bestStream.get().getUrl());
                } else if (!streamInfo.getVideoStreams().isEmpty()) {
                    video.setStreamUrl(streamInfo.getVideoStreams().get(0).getUrl());
                }
                
                return video;
                
            } catch (Exception e) {
                throw new RuntimeException("Error getting video details: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Get trending videos
     */
    public CompletableFuture<List<Video>> getTrendingVideos(int maxResults) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Use a popular search query as a fallback since trending might not be available
                return searchVideos("trending", maxResults).join();
                
            } catch (Exception e) {
                throw new RuntimeException("Error getting trending videos: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Convert StreamInfoItem to Video
     */
    private Video convertStreamInfoItemToVideo(StreamInfoItem item) {
        try {
            Video video = new Video();
            
            // Extract video ID from URL
            String url = item.getUrl();
            String videoId = url.substring(url.lastIndexOf("=") + 1);
            video.setId(videoId);
            
            video.setTitle(item.getName());
            video.setChannelName(item.getUploaderName());
            video.setChannelUrl(item.getUploaderUrl());
            video.setDuration(Duration.ofSeconds(item.getDuration()));
            video.setViewCount(item.getViewCount());
            
            if (!item.getThumbnails().isEmpty()) {
                video.setThumbnailUrl(item.getThumbnails().get(0).getUrl());
            }
            
            // Set YouTube URL for basic playback
            video.setStreamUrl("https://www.youtube.com/watch?v=" + video.getId());
            
            return video;
            
        } catch (Exception e) {
            System.err.println("Error converting StreamInfoItem to Video: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get stream URL for a specific video and quality
     */
    public CompletableFuture<String> getStreamUrl(String videoId, String quality) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Video video = getVideoDetails(videoId).join();
                
                if (video.getFormats() != null && !video.getFormats().isEmpty()) {
                    // Try to find the requested quality
                    for (VideoFormat format : video.getFormats()) {
                        if (format.getQuality().contains(quality) && format.hasVideo() && format.hasAudio()) {
                            return format.getUrl();
                        }
                    }
                    
                    // Fallback to first available format
                    return video.getFormats().get(0).getUrl();
                }
                
                return video.getStreamUrl();
                
            } catch (Exception e) {
                throw new RuntimeException("Error getting stream URL: " + e.getMessage(), e);
            }
        });
    }
}
