package com.example.tunes_alive_wm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastFmApi {
    // URL: ?method=track.search&format=json&track=[YOUR_SEARCH]&api_key=[YOUR_KEY]
    @GET("?method=track.search&format=json")
    Call<LastFmResponse> searchTrack(
            @Query("track") String trackName,
            @Query("api_key") String apiKey
    );
}