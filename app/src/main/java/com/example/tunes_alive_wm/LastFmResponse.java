package com.example.tunes_alive_wm;

import java.util.List;

public class LastFmResponse {
    public Results results;

    public static class Results {
        public TrackMatches trackmatches;
    }

    public static class TrackMatches {
        public List<Track> track;
    }
}