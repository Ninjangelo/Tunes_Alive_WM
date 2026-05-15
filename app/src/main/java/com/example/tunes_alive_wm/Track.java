package com.example.tunes_alive_wm;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Track {
    public String name;
    public String artist;
    public List<LastFmImage> image;

    // Inner class for the image array
    public static class LastFmImage {
        @SerializedName("#text") // Tells Gson to map the "#text" JSON key to this string
        public String url;
        public String size;
    }

    // Helper method to easily get the best quality image URL
    public String getImageUrl() {
        if (image != null && image.size() > 2) {
            String extractedUrl = image.get(2).url;

            // 1. If Last.fm gave us a blank string due to copyright, return empty.
            if (extractedUrl == null || extractedUrl.isEmpty()) {
                return "";
            }

            // 2. The Fix: Force Android to use secure HTTPS instead of blocked HTTP
            if (extractedUrl.startsWith("http://")) {
                return extractedUrl.replace("http://", "https://");
            }

            return extractedUrl;
        }
        return "";
    }
}