package com.example.tunes_alive_wm;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
// Toast Message to confirm registration
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Import Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterArtist extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_artist);

        // Initialise Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI Components
        Button returnButton = findViewById(R.id.returnButton1);
        EditText usernameInput = findViewById(R.id.usernameInput); // NEW
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button submitRegisterButton = findViewById(R.id.submitRegisterButton);

        returnButton.setOnClickListener(v -> finish());

        // Submit Registration Button Logic
        submitRegisterButton.setOnClickListener(v -> {
            // Converts each entry into a string and removes leading and trailing spaces from them
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Client-Side Validation
            // If username is empty
            if (username.isEmpty()) {
                usernameInput.setError("Username is required");
                usernameInput.requestFocus();
                return;
            }

            // If email is empty
            if (email.isEmpty()) {
                emailInput.setError("Email is required");
                emailInput.requestFocus();
                return;
            }

            // Password must be at least 8 characters long
            if (password.length() < 8) {
                passwordInput.setError("Password must be at least 8 characters");
                passwordInput.requestFocus();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Get the newly created user
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Gets unique ID
                                String userUID = user.getUid();
                                // Create a profile update request to attach the username
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();

                                // Submit the username to Firebase Auth
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileTask -> {
                                            if (profileTask.isSuccessful()) {

                                                // Map out data to be saved into Firestore Database
                                                Map<String, Object> userData = new HashMap<>();
                                                userData.put("username", username);
                                                userData.put("email", email);
                                                // Account type set to Artist
                                                userData.put("accountType", "Artist");

                                                // Save it to Firestore
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("users").document(userUID) // Use the Auth UID as the document ID
                                                        .set(userData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            // Profile username update "SUCCESS!!!" message, navigates back home
                                                            Toast.makeText(RegisterArtist.this, "Artist Account Created!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(RegisterArtist.this, HomeActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Database save fail message
                                                            Toast.makeText(RegisterArtist.this, "Failed to save to database: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                        });
                                            }
                                        });
                            }
                        } else {
                            // Catch Firebase Specific Errors
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                emailInput.setError("An account with this email already exists.");
                                emailInput.requestFocus();
                            } catch (Exception e) {  // Registration process fail message
                                Toast.makeText(RegisterArtist.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
            });
        });
    }
}