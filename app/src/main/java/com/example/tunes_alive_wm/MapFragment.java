package com.example.tunes_alive_wm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

// Activity Result API
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

// Google Maps Imports
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

// OnMapReadyCallback listens for when the map finishes loading
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    // DECLARATION: Activity Results Launcher
    // Prompts User to allow location in order for blue dot for live location to be displayed on the map integration
    @SuppressLint("MissingPermission")
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // If permission "Allow", display blue dot
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        zoomToUserLocation();
                    }
                } else {
                    // If permission "Deny", display that location should be enabled for live map
                    Toast.makeText(requireContext(), "Location permission is required for the live map.", Toast.LENGTH_SHORT).show();
                }
            });

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Change the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    // Following method runs immediately after the XML UI loads
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Location Client initialised
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Find the map fragment container from the XML
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        // Load the map in the background via Google
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // This triggers automatically the exact millisecond the map finishes downloading!
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Custom JSON style to hide default Google location icons on the integrated map
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        // ---------- Hard-coded LANDMARKS ----------
        // LANDMARK 1 - Library of Birmingham
        LatLng libraryOfBirmingham = new LatLng(52.47942434097707, -1.9084586933856722);
        MarkerOptions marker1 = new MarkerOptions()
                .position(libraryOfBirmingham)
                .title("Library of Birmingham")
                .snippet("Tap to view music shares!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        // LANDMARK 2 - Symphony Hall
        LatLng symphonyHall = new LatLng(52.47858504552411, -1.9100900772704792);
        MarkerOptions marker2 = new MarkerOptions()
                .position(symphonyHall)
                .title("Symphony Hall")
                .snippet("Tap to view music shares!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        LatLng djBrianRadioShow = new LatLng(52.44631615047508, -1.9912227161070768);
        MarkerOptions marker3 = new MarkerOptions()
                .position(djBrianRadioShow)
                .title("DJBRS (DJBrian Radio Show)")
                .snippet("Tap to view music shares!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        // Appending Map Markers
        mMap.addMarker(marker1);
        mMap.addMarker(marker2);
        mMap.addMarker(marker3);

        enableMyLocation();

        mMap.setOnInfoWindowClickListener(marker -> {
            Intent intent = new Intent(requireActivity(), SpecificLandmarkActivity.class);

            // Attach landmark title to intent
            intent.putExtra("LANDMARK_NAME", marker.getTitle());

            startActivity(intent);
        });
    }

    // Method to check permissions given and to activate the blue dot
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // Check if the user has already granted us permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Permission Granted! Turn on the live blue dot and "Locate Me" button
            mMap.setMyLocationEnabled(true);
            zoomToUserLocation();

        } else {
            // If not, trigger the Android permission pop-up
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void zoomToUserLocation() {
        // Ask the location client for the last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                // We found the user! Create a LatLng and animate the camera there.
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
            }
        });
    }
}