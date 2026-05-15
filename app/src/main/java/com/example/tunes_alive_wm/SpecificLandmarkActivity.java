package com.example.tunes_alive_wm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SpecificLandmarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_specific_landmark);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Identify Header Image and Title Component ID
        ImageView headerImage = findViewById(R.id.iv_landmark_header);
        TextView titleText = findViewById(R.id.tv_landmark_title);

        // Get the passed data of the specific landmark from the intent
        String passedLandmarkName = getIntent().getStringExtra("LANDMARK_NAME");

        // SAFETY CHECK: Ensure the passed name is not null before doing anything!
        if (passedLandmarkName != null) {

            // 1. Set the Title Text
            titleText.setText(passedLandmarkName);

            // 2. Dynamically swap the image depending on what marker has been clicked
            if (passedLandmarkName.equals("Library of Birmingham")) {
                headerImage.setImageResource(R.drawable.library_of_birmingham);
            }
            else if (passedLandmarkName.equals("Symphony Hall")) {
                headerImage.setImageResource(R.drawable.symphony_hall);
            }
            else if (passedLandmarkName.equals("DJBRS (DJBrian Radio Show)")) {
                headerImage.setImageResource(R.drawable.djbrian_radio_show);
            }
        }

        // Identify the RecyclerView from the XML
        RecyclerView recyclerView = findViewById(R.id.recycler_view_shares);

        // Set the Layout Manager (enforces it into scrolling list)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create an empty list and set the adapter so the screen isn't blank while loading
        List<MusicShare> liveShares = new ArrayList<>();
        recyclerView.setAdapter(new MusicShareAdapter(liveShares));

        if (passedLandmarkName != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Ask Firestore ONLY for songs where the landmarkName matches the current screen!
            db.collection("shares")
                    .whereEqualTo("landmarkName", passedLandmarkName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            liveShares.clear(); // Clear the list just in case

                            // Loop through every song Firestore found
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String title = document.getString("songTitle");
                                String artist = document.getString("artistName");
                                String user = document.getString("username");

                                // Add it to our list!
                                liveShares.add(new MusicShare(
                                        title != null ? title : "Unknown",
                                        artist != null ? artist : "Unknown",
                                        user != null ? user : "Unknown"
                                ));
                            }

                            // Refresh the RecyclerView with the newly downloaded live data!
                            recyclerView.setAdapter(new MusicShareAdapter(liveShares));
                        } else {
                            Toast.makeText(this, "Failed to load live shares.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        Button btnShareSong = findViewById(R.id.btn_share_song);
        btnShareSong.setOnClickListener(v -> {
            // 1. Create the Intent to go to the new screen
            Intent intent = new Intent(SpecificLandmarkActivity.this, ShareSongActivity.class);

            // 2. Pass the landmark name forward! The next screen needs to know WHERE we are sharing this song.
            intent.putExtra("LANDMARK_NAME", passedLandmarkName);

            // 3. Launch the screen
            startActivity(intent);
        });
    }
}