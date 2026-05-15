package com.example.tunes_alive_wm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_options);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // RETURN Button
        Button returnButton2 = findViewById(R.id.returnButton2);
        returnButton2.setOnClickListener(v ->{
            finish();
        });

        // RETURN Button clickListener
        Button artistOption2 = findViewById(R.id.artistOption2);
        artistOption2.setOnClickListener(v -> {
            Intent intent = new Intent(LoginOptions.this, LoginArtist.class);
            startActivity(intent);
        });
    }
}