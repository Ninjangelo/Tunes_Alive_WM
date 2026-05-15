package com.example.tunes_alive_wm;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

// Import Firebase Auth
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Store the layout for this fragment into a 'View' object
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the logout button INSIDE this specific view
        Button logoutButton = view.findViewById(R.id.logoutButton);

        // 3. Apply the Logout Logic
        logoutButton.setOnClickListener(v -> {

            // Firebase ends the current logged-in session
            FirebaseAuth.getInstance().signOut();

            // Navigate back to MainActivity
            // CRITICAL DIFFERENCE: We use getActivity() instead of HomeActivity.this
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 4. Return the fully built view to the screen
        return view;
    }
}