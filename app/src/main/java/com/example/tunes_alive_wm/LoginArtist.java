package com.example.tunes_alive_wm;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Import Firebase
import com.google.firebase.auth.FirebaseAuth;

public class LoginArtist extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_artist);

        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI Components
        Button returnButton = findViewById(R.id.returnButton1);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button submitLoginButton = findViewById(R.id.submitLoginButton);

        // Return Button Logic
        returnButton.setOnClickListener(v -> finish());

        // Submit Login Button Logic
        submitLoginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Client-Side Validation
            if (email.isEmpty()) {
                emailInput.setError("Email is required");
                emailInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordInput.setError("Password is required");
                passwordInput.requestFocus();
                return;
            }

            // Authenticate with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Success! Send them to the Home Screen
                            Toast.makeText(LoginArtist.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginArtist.this, HomeActivity.class);
                            // Clears the back stack so they can't press 'back' to return to the login screen
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Generic Failure Message for Security
                            Toast.makeText(LoginArtist.this, "Authentication failed. Check your credentials.", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}