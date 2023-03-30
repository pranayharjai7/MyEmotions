package com.pranayharjai7.myemotions.Utils.Adapters;

import static com.pranayharjai7.myemotions.Fragments.FriendsActivityFragments.AddFriendsFragment.ADD_FRIENDS_FRAGMENT;
import static com.pranayharjai7.myemotions.Fragments.FriendsActivityFragments.FriendRequestsFragment.FRIEND_REQUESTS_FRAGMENT;
import static com.pranayharjai7.myemotions.Fragments.FriendsActivityFragments.RemoveFriendsFragment.REMOVE_FRIENDS_FRAGMENT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranayharjai7.myemotions.Database.Friend;
import com.pranayharjai7.myemotions.Database.FriendRequest;
import com.pranayharjai7.myemotions.Database.UserProfile;
import com.pranayharjai7.myemotions.R;
import com.pranayharjai7.myemotions.Utils.Interfaces.Callback;
import com.pranayharjai7.myemotions.databinding.UserprofileCardLayoutBinding;

import java.util.List;

public class UserProfileViewAdapter extends RecyclerView.Adapter<UserProfileViewAdapter.UserProfileViewHolder> {

    private List<UserProfile> userProfiles;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    @NonNull
    private String contextClass;

    public UserProfileViewAdapter(List<UserProfile> userProfiles, Context context, @NonNull String contextClass) {
        this.userProfiles = userProfiles;
        this.context = context;
        this.contextClass = contextClass;
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

        setCardViewListener(holder, userId, username);
    }

    private void setCardViewListener(UserProfileViewHolder holder, String userId, String username) {
        switch (contextClass) {
            case ADD_FRIENDS_FRAGMENT: {
                holder.binding.userProfileCardView.setOnClickListener(v -> addFriendsListener(userId, username));
                break;
            }
            case FRIEND_REQUESTS_FRAGMENT: {
                holder.binding.userProfileCardView.setOnClickListener(v -> acceptFriendRequestListener(userId, username));
                break;
            }
            case REMOVE_FRIENDS_FRAGMENT: {
                holder.binding.userProfileCardView.setOnClickListener(v -> removeFriendListener(userId, username));
                break;
            }
            default: {
                holder.binding.userProfileCardView.setOnClickListener(null);
            }
        }
    }

    private void addFriendsListener(String friendId, String username) {
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle("Add " + username + " as friend?")
                .setPositiveButton("YES", (dialog, i) -> {
                    sendFriendRequest(friendId, username);
                })
                .setNegativeButton("NO", (dialog, i) -> {
                    dialog.dismiss();
                }).show();
    }

    private void acceptFriendRequestListener(String friendId, String username) {
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle("Add " + username + " as friend?")
                .setPositiveButton("YES", (dialog, i) -> {
                    addUserAsFriend(friendId, username);
                })
                .setNegativeButton("NO", (dialog, i) -> {
                    dialog.dismiss();
                }).show();
    }

    private void removeFriendListener(String friendId, String username) {
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle("Are you sure you want to remove " + username + " as friend?")
                .setPositiveButton("YES", (dialog, i) -> {
                    removeUserAsFriend(friendId, username);
                })
                .setNegativeButton("NO", (dialog, i) -> {
                    dialog.dismiss();
                }).show();
    }

    private void sendFriendRequest(String friendId, String usernameOfReceiver) {
        FriendRequest friendRequest = new FriendRequest(friendId, mAuth.getCurrentUser().getUid());

        getUsernameOfCurrentUser(usernameOfSender -> {
            firebaseDatabase.getReference("MyEmotions")
                    .child("UserProfile")
                    .child(friendRequest.getReceiverUserId())
                    .child("friendRequests")
                    .child(friendRequest.getFriendRequestSenderId())
                    .setValue(usernameOfSender)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Friend Request sent to " + usernameOfReceiver, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Couldn't add friend, please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void addUserAsFriend(String friendId, String friendName) {
        Friend friend = new Friend(mAuth.getCurrentUser().getUid(), friendId);

        getUsernameOfCurrentUser(usernameOfCurrentUser -> {
            addFriendToCurrentUser(friend, friendName, result -> {
                addCurrentUserAsFriendToFriendRequestSender(friend, usernameOfCurrentUser, friendName, result1 -> {
                    removeFriendRequestFromFirebase(friend, friendName);
                });
            });
        });
    }

    private void removeUserAsFriend(String friendId, String username) {
        Friend friend = new Friend(mAuth.getCurrentUser().getUid(), friendId);
        removeFriendFromCurrentUser(friend, result -> {
            removeCurrentUserFromFriend(friend, username);
        });
    }

    private void getUsernameOfCurrentUser(Callback<String> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(mAuth.getCurrentUser().getUid())
                .child("username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.getValue(String.class);
                        callback.onSuccess(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addFriendToCurrentUser(Friend friend, String friendName, Callback<?> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(friend.getUserId())
                .child("friends")
                .child(friend.getFriendId())
                .setValue(friendName)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        Toast.makeText(context, "Couldn't add friend, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addCurrentUserAsFriendToFriendRequestSender(Friend friend, String usernameOfCurrentUser, String friendName, Callback<String> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(friend.getFriendId())
                .child("friends")
                .child(friend.getUserId())
                .setValue(usernameOfCurrentUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, friendName + " added as friend!", Toast.LENGTH_SHORT).show();
                        callback.onSuccess(friendName);
                    } else {
                        Toast.makeText(context, "Couldn't add friend, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFriendRequestFromFirebase(Friend friend, String friendName) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(friend.getUserId())
                .child("friendRequests")
                .child(friend.getFriendId())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Error in removing friend request, please check database.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFriendFromCurrentUser(Friend friend, Callback<?> callback) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(friend.getUserId())
                .child("friends")
                .child(friend.getFriendId())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    }
                });
    }

    private void removeCurrentUserFromFriend(Friend friend, String username) {
        firebaseDatabase.getReference("MyEmotions")
                .child("UserProfile")
                .child(friend.getFriendId())
                .child("friends")
                .child(friend.getUserId())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, username + " removed as a friend!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error in removing friend, please check database.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
