package com.pranayharjai7.myemotions.Fragments.MainActivityFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.LatLngUtils;
import com.pranayharjai7.myemotions.databinding.FragmentMapsBinding;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private FragmentMapsBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public MapsFragment() {
        super(R.layout.fragment_maps);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng myHouse = new LatLng(47.5399011, 21.6226994);
        MarkerOptions marker = new MarkerOptions();
        marker.position(myHouse);
        marker.title("Home");
        marker.snippet("Emotion: Happy");
        mMap.addMarker(marker);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myHouse));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myHouse, 16f));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

        getLastLocationOfDevice();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getLastLocationOfDevice();
        return false;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocationOfDevice() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                setLastLocationOfUserInFirebase(currentLocation);
            } else {
                Toast.makeText(getContext(), "Could not get current location. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLastLocationOfUserInFirebase(LatLng currentLocation) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("location")
                .setValue(LatLngUtils.convertLatLngToString(currentLocation));
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
