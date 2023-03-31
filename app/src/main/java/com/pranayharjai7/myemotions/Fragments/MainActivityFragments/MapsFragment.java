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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.Friend;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.Enums.MoodVisibility;
import com.pranayharjai7.myemotions.Utils.Interfaces.Callback;
import com.pranayharjai7.myemotions.Utils.LatLngUtils;
import com.pranayharjai7.myemotions.databinding.FragmentMapsBinding;

import java.util.ArrayList;
import java.util.List;

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

        addFriendsOnMap();

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myHouse));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myHouse, 16f));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

        getLastLocationOfDevice();
    }

    private void addFriendsOnMap() {
        getFriendsFromFirebase(friends -> {
            getFriendsUserProfiles(friends, userProfiles -> {
                for (UserProfile userProfile: userProfiles) {
                    if (userProfile.getLocation().equals("")) {
                        continue;
                    }

                    Double lat = Double.parseDouble(userProfile.getLocation().split(",")[0]);
                    Double lng = Double.parseDouble(userProfile.getLocation().split(",")[1]);
                    LatLng userLocation = new LatLng(lat,lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(userLocation);
                    markerOptions.title(userProfile.getUsername());
                    markerOptions.snippet(userProfile.getLatestEmotion());
                    mMap.addMarker(markerOptions).showInfoWindow();
                }

            });
        });

    }

    private void getFriendsFromFirebase(Callback<List<Friend>> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Friend> friends = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                                String friendId = friendSnapshot.getKey();
                                Friend friend = new Friend(mAuth.getCurrentUser().getUid(), friendId);
                                friends.add(friend);
                            }
                            callback.onSuccess(friends);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFriendsUserProfiles(List<Friend> friends, Callback<List<UserProfile>> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<UserProfile> userProfiles = new ArrayList<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            for (Friend friend : friends) {
                                if (friend.getFriendId().equals(friendSnapshot.getKey())) {
                                    String moodVisibility = friendSnapshot.child("moodVisibility").getValue(String.class);
                                    if (!moodVisibility.equals(MoodVisibility.ONLYME.toString())) {
                                        String userId = friendSnapshot.getKey();
                                        String username = friendSnapshot.child("username").getValue(String.class);
                                        String email = friendSnapshot.child("email").getValue(String.class);
                                        String latestEmotion = friendSnapshot.child("latestEmotion").getValue(String.class);
                                        String latestEmotionDateTime = friendSnapshot.child("latestEmotionDateTime").getValue(String.class);
                                        String location = friendSnapshot.child("location").getValue(String.class);
                                        UserProfile userProfile = new UserProfile(userId, username, email, location, moodVisibility, latestEmotion, latestEmotionDateTime);
                                        userProfiles.add(userProfile);
                                    }
                                    break;
                                }
                            }
                        }
                        callback.onSuccess(userProfiles);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
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
