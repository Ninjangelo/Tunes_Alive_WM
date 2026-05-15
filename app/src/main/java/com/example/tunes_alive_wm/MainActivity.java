package com.example.tunes_alive_wm;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // NAVIGATION
        // LOGIN & REGISTER Buttons identified in XML
        Button loginOption = findViewById(R.id.loginOption);
        Button registerOption = findViewById(R.id.registerOption);


        // LOGIN Button clickListener
        loginOption.setOnClickListener(v -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginOptions.class);
            startActivity(loginIntent);
        });

        // REGISTER Button clickListener
        registerOption.setOnClickListener(v -> {
            Intent registerIntent = new Intent(MainActivity.this, RegisterOptions.class);
            startActivity(registerIntent);
        });


    }
}