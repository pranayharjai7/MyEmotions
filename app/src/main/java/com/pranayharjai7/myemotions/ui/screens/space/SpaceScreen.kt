package com.pranayharjai7.myemotions.ui.screens.space

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pranayharjai7.myemotions.ui.utils.generateQrCode
import com.pranayharjai7.myemotions.ui.components.EmotionIcon
import com.pranayharjai7.myemotions.ui.theme.getThemeForEmotion
import com.pranayharjai7.myemotions.domain.model.Profile
import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.border
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SpaceScreen(
    onNavigateToQrScanner: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    feedViewModel: FeedViewModel = hiltViewModel(),
    friendsViewModel: FriendsViewModel = hiltViewModel()
) {
    val feedItems by feedViewModel.feedItems.collectAsStateWithLifecycle()
    val friendships by friendsViewModel.friendships.collectAsStateWithLifecycle()
    val profiles by friendsViewModel.profiles.collectAsStateWithLifecycle()
    val currentUser by friendsViewModel.currentUser.collectAsStateWithLifecycle()
    val searchResults by friendsViewModel.searchResults.collectAsStateWithLifecycle()
    val searchQuery by friendsViewModel.searchQuery.collectAsStateWithLifecycle()
    
    var showMyQrModal by remember { mutableStateOf(false) }
    val currentUserId = currentUser?.id ?: ""
    
    val incomingRequests = friendships.filter { it.status == "pending" && it.friend_id == currentUserId }
    val myFriends = friendships.filter { it.status == "accepted" }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // --- SECTION 1: EMOTION FEED ---
            item {
                Text(
                    text = "Friends' Emotions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
                )
            }

            if (feedItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No recent emotions from friends",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(feedItems) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        FeedItemCard(
                            item = item,
                            onReact = { type -> feedViewModel.sendReaction(item.emotion_id, type) },
                            onRemoveReaction = { feedViewModel.removeReaction(item.emotion_id) }
                        )
                    }
                }
            }

            // --- SECTION 2: PEOPLE HUB ---
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "People",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Search and QR Actions
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { friendsViewModel.onSearchQueryChanged(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search users") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        IconButton(
                            onClick = onNavigateToQrScanner,
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = MaterialTheme.colorScheme.primary)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { showMyQrModal = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = "My QR", tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    
                    if (searchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Search Results", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Search Results
            if (searchQuery.isNotEmpty()) {
                items(searchResults.filter { it.id != currentUserId }) { profile ->
                    val existingFriendship = friendships.find { 
                        (it.user_id == profile.id && it.friend_id == currentUserId) ||
                        (it.friend_id == profile.id && it.user_id == currentUserId)
                    }
                    
                    val actionText = when (existingFriendship?.status) {
                        "accepted" -> "Friends"
                        "pending" -> if (existingFriendship.user_id == currentUserId) "Sent" else "Accept"
                        else -> "Add"
                    }

                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        ProfileCard(
                            profile = profile,
                            actionText = actionText,
                            actionEnabled = actionText == "Add" || actionText == "Accept",
                            onProfileClick = { onNavigateToProfile(profile.id) },
                            onAction = {
                                if (actionText == "Add") {
                                    friendsViewModel.sendFriendRequest(profile.id)
                                } else if (actionText == "Accept" && existingFriendship != null) {
                                    friendsViewModel.acceptRequest(existingFriendship.id)
                                }
                            }
                        )
                    }
                }
            }

            // Friend Requests Section
            if (incomingRequests.isNotEmpty()) {
                item {
                    Text(
                        text = "Friend Requests",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
                items(incomingRequests) { request ->
                    val profile = profiles.find { it.id == request.user_id }
                    if (profile != null) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ProfileCard(
                                profile = profile,
                                actionText = "Accept",
                                secondaryActionText = "Reject",
                                onProfileClick = { onNavigateToProfile(profile.id) },
                                onAction = { friendsViewModel.acceptRequest(request.id) },
                                onSecondaryAction = { friendsViewModel.rejectRequest(request.id) }
                            )
                        }
                    }
                }
            }

            // Friends List Section
            item {
                Text(
                    text = "My Friends",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            if (myFriends.isEmpty()) {
                item {
                    Text(
                        "No friends yet. Start adding some!",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                items(myFriends) { friendship ->
                    val friendId = if (friendship.user_id == currentUserId) friendship.friend_id else friendship.user_id
                    val profile = profiles.find { it.id == friendId }
                    if (profile != null) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ProfileCard(
                                profile = profile,
                                actionText = "Remove",
                                onProfileClick = { onNavigateToProfile(profile.id) },
                                onAction = { friendsViewModel.removeFriend(friendship.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // My QR Code Modal (Reused from FriendsHubScreen logic)
    if (showMyQrModal && currentUser != null) {
        ModalBottomSheet(
            onDismissRequest = { showMyQrModal = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "My QR Code", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "Share this with friends to connect", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(32.dp))
                Surface(
                    modifier = Modifier.size(240.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    val qrContent = "myemotions://addfriend/${currentUser!!.id}"
                    val qrBitmap = remember(qrContent) { generateQrCode(qrContent) }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (qrBitmap != null) {
                            androidx.compose.foundation.Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.size(200.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "@${currentUser?.userMetadata?.get("username")?.toString()?.replace("\"", "") ?: currentUser!!.email}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { /* Share logic */ }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Profile Link")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// --- REUSED COMPONENTS FROM PREVIOUS SCREENS ---

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedItemCard(item: FeedItem, onReact: (String) -> Unit, onRemoveReaction: () -> Unit) {
    val theme = getThemeForEmotion(item.emotion)
    var showReactionPicker by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = theme.primaryColor.copy(alpha = 0.05f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, theme.primaryColor.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header: Avatar & Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(theme.primaryColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (item.display_name ?: item.username ?: "?").firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = theme.primaryColor
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.display_name ?: item.username ?: "Unknown", style = MaterialTheme.typography.titleMedium)
                    val timeAgo = DateUtils.getRelativeTimeSpanString(item.unix_timestamp, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                    Text(timeAgo.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body: Emotion and Note
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmotionIcon(emotion = item.emotion, modifier = Modifier.size(32.dp), tint = theme.primaryColor)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Feeling ${item.emotion}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = theme.primaryColor)
            }

            if (!item.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${item.note}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Reactions List
            if (item.reactions.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val reactionCounts = item.reactions.groupingBy { it.reaction_type }.eachCount()
                    reactionCounts.forEach { (type, count) ->
                        val emoji = when (type) {
                            "Support" -> "❤️"
                            "Hug" -> "🫂"
                            "Relate" -> "🙌"
                            "Proud" -> "🌟"
                            "Care" -> "💛"
                            else -> "❤️"
                        }
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("$emoji $count", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Action Button
            Box(contentAlignment = Alignment.Center) {
                if (showReactionPicker) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(32.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            "Support" to "❤️",
                            "Hug" to "🫂",
                            "Relate" to "🙌",
                            "Proud" to "🌟",
                            "Care" to "💛"
                        ).forEach { (type, emoji) ->
                            TextButton(
                                onClick = {
                                    onReact(type)
                                    showReactionPicker = false
                                }
                            ) {
                                Text(emoji, fontSize = 24.sp)
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onReact("Support") },
                                onLongClick = { showReactionPicker = true }
                            ),
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(1.dp, theme.primaryColor.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = theme.primaryColor
                        )
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("React")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileCard(
    profile: Profile,
    actionText: String,
    actionEnabled: Boolean = true,
    secondaryActionText: String? = null,
    onProfileClick: () -> Unit,
    onAction: () -> Unit,
    onSecondaryAction: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onProfileClick,
                onLongClick = { }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (profile.display_name ?: profile.username ?: "?").firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.display_name ?: profile.username ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (profile.username != null) {
                    Text(
                        text = "@${profile.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (secondaryActionText != null && onSecondaryAction != null) {
                    TextButton(
                        onClick = onSecondaryAction,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(secondaryActionText)
                    }
                }
                
                Button(
                    onClick = onAction,
                    enabled = actionEnabled,
                    shape = RoundedCornerShape(16.dp),
                    colors = if (actionText == "Add" || actionText == "Accept") 
                        ButtonDefaults.buttonColors() 
                        else ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text(actionText)
                }
            }
        }
    }
}

