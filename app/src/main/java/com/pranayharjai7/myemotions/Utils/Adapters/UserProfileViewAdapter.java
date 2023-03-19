package com.pranayharjai7.myemotions.Utils.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pranayharjai7.myemotions.Database.Friend;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.databinding.UserprofileCardLayoutBinding;

import java.util.List;

public class UserProfileViewAdapter extends RecyclerView.Adapter<UserProfileViewAdapter.UserProfileViewHolder> {

    private List<UserProfile> userProfiles;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    public UserProfileViewAdapter(List<UserProfile> userProfiles, Context context) {
        this.userProfiles = userProfiles;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    public static class UserProfileViewHolder extends RecyclerView.ViewHolder {
        UserprofileCardLayoutBinding binding;

        public UserProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserprofileCardLayoutBinding.bind(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userprofile_card_layout, parent, false);
        return new UserProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        String userId = userProfiles.get(position).getUserId();
        String username = userProfiles.get(position).getUsername();
        String email = userProfiles.get(position).getEmail();

        holder.binding.usernameTextView.setText(username);
        holder.binding.emailTextView.setText(email);
        holder.binding.userProfileCardView.setOnClickListener(v -> userProfileCardViewClicked(userId, username));
    }

    private void userProfileCardViewClicked(String userId, String username) {
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle("Add " + username + " as friend?")
                .setPositiveButton("YES", (dialog, i) -> {
                    addUserAsFriend(userId, username);
                })
                .setNegativeButton("NO", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).show();
    }

    private void addUserAsFriend(String friendId, String username) {
        Friend friend = new Friend(mAuth.getCurrentUser().getUid(), friendId);

        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(friend.getUserId())
                .child("friends")
                .child(friend.getFriendId())
                .setValue(username)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, username + " added as friend!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Couldn't add friend, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
