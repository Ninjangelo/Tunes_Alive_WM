package com.example.tunes_alive_wm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Retrofit Imports
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SongChoiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongChoiceAdapter adapter;
    private LastFmApi lastFmApi;

    // Your specific Last.fm API Key
    private final String API_KEY = "021f0d858690279b97c4218359e8da10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_choice);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Setup the RecyclerView (UPDATED WITH CLICK LISTENER)
        recyclerView = findViewById(R.id.recycler_view_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pass the new click behavior into the adapter
        adapter = new SongChoiceAdapter(new ArrayList<>(), track -> {
            // When a song is clicked, package the data into an Intent
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_TITLE", track.name);
            resultIntent.putExtra("SELECTED_ARTIST", track.artist);

            // Send it back and close this screen!
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        recyclerView.setAdapter(adapter);

        // 2. Build the Retrofit Client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ws.audioscrobbler.com/2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        lastFmApi = retrofit.create(LastFmApi.class);

        // 3. Listen for the user hitting "Search/Enter" on the keyboard
        EditText searchBar = findViewById(R.id.et_search_bar);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchBar.getText().toString();
                if (!query.isEmpty()) {
                    searchLastFm(query);
                }
                return true;
            }
            return false;
        });
    }

    // 4. The Network Call
    private void searchLastFm(String query) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        lastFmApi.searchTrack(query, API_KEY).enqueue(new Callback<LastFmResponse>() {
            @Override
            public void onResponse(Call<LastFmResponse> call, Response<LastFmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract the list of tracks from the JSON hierarchy
                    LastFmResponse.Results results = response.body().results;
                    if (results != null && results.trackmatches != null) {
                        // Pass the new data directly to the Adapter
                        adapter.updateTracks(results.trackmatches.track);
                    }
                } else {
                    Toast.makeText(SongChoiceActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LastFmResponse> call, Throwable t) {
                Log.e("LastFm", "Search failed", t);
                Toast.makeText(SongChoiceActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}