package com.example.tunes_alive_wm;

public class MusicShare {
    private String songTitle;
    private String artistName;
    private String username;

    public MusicShare(String songTitle, String artistName, String username) {
        this.songTitle = songTitle;
        this.artistName = artistName;
        this.username = username;
    }

    public String getSongTitle() { return songTitle; }
    public String getArtistName() { return artistName; }
    public String getUsername() { return username; }
}