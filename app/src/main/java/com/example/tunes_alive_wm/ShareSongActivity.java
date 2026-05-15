package com.example.tunes_alive_wm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShareSongActivity extends AppCompatActivity {

    private String targetLandmark;
    private String selectedSongTitle = null;
    private String selectedSongArtist = null;

    private TextView tvSongChoiceDisplay; // To update the text inside the purple card

    // NEW: The Launcher to catch the returning data from Last.fm
    private final ActivityResultLauncher<Intent> songSearchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // 1. Extract the returned data
                    selectedSongTitle = result.getData().getStringExtra("SELECTED_TITLE");
                    selectedSongArtist = result.getData().getStringExtra("SELECTED_ARTIST");

                    // 2. Update the UI to show the user's choice!
                    tvSongChoiceDisplay.setText(selectedSongTitle + "\nby " + selectedSongArtist);
                    tvSongChoiceDisplay.setTextSize(16f); // Slightly smaller to fit the artist
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share_song);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup the sub-header
        targetLandmark = getIntent().getStringExtra("LANDMARK_NAME");
        TextView subLandmarkText = findViewById(R.id.tv_sub_landmark);
        if (targetLandmark != null) {
            subLandmarkText.setText("at " + targetLandmark);
        }

        // Find the TextView inside the purple CardView (We have to navigate through the layout to find it)
        CardView cardSongChoice = findViewById(R.id.card_song_choice);
        LinearLayout cardLayout = (LinearLayout) cardSongChoice.getChildAt(0);
        tvSongChoiceDisplay = (TextView) cardLayout.getChildAt(1);

        // Launch the Search screen using our new Launcher
        cardSongChoice.setOnClickListener(v -> {
            Intent intent = new Intent(ShareSongActivity.this, SongChoiceActivity.class);
            songSearchLauncher.launch(intent); // Launches waiting for a result!
        });

        // Setup Share button
        Button btnConfirmShare = findViewById(R.id.btn_confirm_share);
        EditText etDescription = findViewById(R.id.et_description);

        btnConfirmShare.setOnClickListener(v -> {
            if (selectedSongTitle == null) {
                Toast.makeText(this, "Please select a song first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Saving to cloud...", Toast.LENGTH_SHORT).show();
            String description = etDescription.getText().toString();

            // 1. Initialize Firestore AND FirebaseAuth
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            // 2. Dynamically extract the username!
            String actualUsername = "Unknown User";
            if (currentUser != null) {
                // If they have a display name set, use it!
                if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                    actualUsername = currentUser.getDisplayName();
                }
                // If not, fall back to their email address so we at least know who they are
                else if (currentUser.getEmail() != null) {
                    actualUsername = currentUser.getEmail();
                }
            }

            // 3. Package the data into a Map using the REAL username
            Map<String, Object> newShare = new HashMap<>();
            newShare.put("landmarkName", targetLandmark);
            newShare.put("songTitle", selectedSongTitle);
            newShare.put("artistName", selectedSongArtist);
            newShare.put("description", description);
            newShare.put("username", actualUsername); // <-- DYNAMIC DATA HERE!
            newShare.put("timestamp", System.currentTimeMillis());

            // 4. Push it to the "shares" collection
            db.collection("shares")
                    .add(newShare)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Song shared successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error sharing song: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}