package com.liskovsoft.smarttube.desktop.service;

import okhttp3.*;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of NewPipe Downloader using OkHttp
 */
public class DownloaderImpl extends Downloader {
    
    private final OkHttpClient client;
    
    public DownloaderImpl() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }
    
    @Override
    public Response execute(Request request) throws IOException, ReCaptchaException {
        
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
            .url(request.url())
            .method(request.httpMethod(), null);
        
        // Add headers
        for (Map.Entry<String, List<String>> header : request.headers().entrySet()) {
            for (String value : header.getValue()) {
                requestBuilder.addHeader(header.getKey(), value);
            }
        }
        
        // Add User-Agent if not present
        if (!request.headers().containsKey("User-Agent")) {
            requestBuilder.addHeader("User-Agent", 
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        }
        
        try (okhttp3.Response response = client.newCall(requestBuilder.build()).execute()) {
            
            if (response.code() == 429) {
                throw new ReCaptchaException("Too many requests", request.url());
            }
            
            String responseBody = response.body() != null ? response.body().string() : "";
            
            // Convert response headers
            Map<String, List<String>> responseHeaders = new HashMap<>();
            for (String name : response.headers().names()) {
                responseHeaders.put(name, response.headers().values(name));
            }
            
            return new Response(
                response.code(),
                response.message(),
                responseHeaders,
                responseBody,
                response.request().url().toString()
            );
            
        } catch (IOException e) {
            throw new IOException("Network error: " + e.getMessage(), e);
        }
    }
}
