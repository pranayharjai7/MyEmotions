package com.pranayharjai7.myemotions.ui.screens.space

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pranayharjai7.myemotions.domain.model.Profile
import com.pranayharjai7.myemotions.ui.utils.generateQrCode
import io.github.jan.supabase.gotrue.user.UserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFriendsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQrScanner: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val friendships by viewModel.friendships.collectAsStateWithLifecycle()
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var showMyQrModal by remember { mutableStateOf(false) }
    val tabs = listOf("My Friends", "Requests", "Add Friends", "Blocked")
    
    val currentUserId = currentUser?.id ?: ""
    val acceptedFriendships = friendships.filter { it.status == "accepted" }
    val incomingRequests = friendships.filter { it.status == "pending" && it.friend_id == currentUserId }
    val sentRequests = friendships.filter { it.status == "pending" && it.user_id == currentUserId }
    val blockedFriendships = friendships.filter { it.status == "blocked" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Manage Friends", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Connect and share emotions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Azure Glass Segmented Control
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .padding(horizontal = 2.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            onClick = { selectedTab = index }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp)
            ) {
                when (selectedTab) {
                    0 -> { // My Friends
                        if (acceptedFriendships.isEmpty()) {
                            item { EmptyState("No friends yet") }
                        } else {
                            items(acceptedFriendships) { friendship ->
                                val friendId = if (friendship.user_id == currentUserId) friendship.friend_id else friendship.user_id
                                val profile = profiles.find { it.id == friendId }
                                if (profile != null) {
                                    FriendItem(
                                        profile = profile,
                                        onViewProfile = { onNavigateToProfile(profile.id) },
                                        onRemove = { viewModel.removeFriend(friendship.id) },
                                        onBlock = { viewModel.blockUser(profile.id) }
                                    )
                                }
                            }
                        }
                    }
                    1 -> { // Requests
                        if (incomingRequests.isEmpty() && sentRequests.isEmpty()) {
                            item { EmptyState("No pending requests") }
                        } else {
                            if (incomingRequests.isNotEmpty()) {
                                item { SectionHeader("Incoming Requests") }
                                items(incomingRequests) { request ->
                                    val profile = profiles.find { it.id == request.user_id }
                                    if (profile != null) {
                                        RequestItem(
                                            profile = profile,
                                            isIncoming = true,
                                            onAccept = { viewModel.acceptRequest(request.id) },
                                            onDecline = { viewModel.rejectRequest(request.id) }
                                        )
                                    }
                                }
                            }
                            if (sentRequests.isNotEmpty()) {
                                item { SectionHeader("Sent Requests") }
                                items(sentRequests) { request ->
                                    val profile = profiles.find { it.id == request.friend_id }
                                    if (profile != null) {
                                        RequestItem(
                                            profile = profile,
                                            isIncoming = false,
                                            onCancel = { viewModel.rejectRequest(request.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Add Friends
                        item {
                            AddFriendsSection(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onScanQr = onNavigateToQrScanner,
                                onShowMyQr = { showMyQrModal = true }
                            )
                        }
                        
                        if (searchQuery.length >= 3) {
                            items(searchResults.filter { it.id != currentUserId }) { profile ->
                                val existingFriendship = friendships.find { 
                                    (it.user_id == profile.id && it.friend_id == currentUserId) ||
                                    (it.friend_id == profile.id && it.user_id == currentUserId)
                                }
                                val status = existingFriendship?.status ?: "none"
                                SearchResultItem(
                                    profile = profile,
                                    status = status,
                                    isSender = existingFriendship?.user_id == currentUserId,
                                    onAdd = { viewModel.sendFriendRequest(profile.id) },
                                    onAccept = { existingFriendship?.let { viewModel.acceptRequest(it.id) } }
                                )
                            }
                        }
                    }
                    3 -> { // Blocked
                        if (blockedFriendships.isEmpty()) {
                            item { EmptyState("No blocked users") }
                        } else {
                            items(blockedFriendships) { friendship ->
                                val friendId = if (friendship.user_id == currentUserId) friendship.friend_id else friendship.user_id
                                val profile = profiles.find { it.id == friendId }
                                if (profile != null) {
                                    BlockedUserItem(
                                        profile = profile,
                                        onUnblock = { viewModel.unblockUser(profile.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val user = currentUser
    if (showMyQrModal && user != null) {
        MyQrModal(
            currentUser = user,
            onDismiss = { showMyQrModal = false }
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.PersonOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
private fun FriendItem(
    profile: Profile,
    onViewProfile: () -> Unit,
    onRemove: () -> Unit,
    onBlock: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Text(profile.display_name?.take(1) ?: profile.username?.take(1) ?: "?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.display_name ?: profile.username ?: "Unknown", fontWeight = FontWeight.Bold)
                Text("@${profile.username ?: "unknown"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("View Profile") }, onClick = { onViewProfile(); showMenu = false })
                    DropdownMenuItem(text = { Text("Remove Friend", color = MaterialTheme.colorScheme.error) }, onClick = { onRemove(); showMenu = false })
                    DropdownMenuItem(text = { Text("Block User", color = MaterialTheme.colorScheme.error) }, onClick = { onBlock(); showMenu = false })
                }
            }
        }
    }
}

@Composable
private fun RequestItem(
    profile: Profile,
    isIncoming: Boolean,
    onAccept: () -> Unit = {},
    onDecline: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Text(profile.display_name?.take(1) ?: profile.username?.take(1) ?: "?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.display_name ?: profile.username ?: "Unknown", fontWeight = FontWeight.Bold)
                Text(if (isIncoming) "wants to be friends" else "Request sent", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            if (isIncoming) {
                IconButton(onClick = onAccept, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                    Icon(Icons.Default.Check, contentDescription = "Accept")
                }
                IconButton(onClick = onDecline, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Close, contentDescription = "Decline")
                }
            } else {
                TextButton(onClick = onCancel, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun AddFriendsSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onScanQr: () -> Unit,
    onShowMyQr: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by username or email") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                unfocusedBorderColor = Color.Transparent
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onScanQr,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan QR")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onShowMyQr,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), contentColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.QrCode, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("My QR")
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    profile: Profile,
    status: String,
    isSender: Boolean,
    onAdd: () -> Unit,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Text(profile.display_name?.take(1) ?: profile.username?.take(1) ?: "?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.display_name ?: profile.username ?: "Unknown", style = MaterialTheme.typography.titleSmall)
                Text("@${profile.username ?: "unknown"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            when (status) {
                "accepted" -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                "pending" -> if (isSender) Text("Sent", style = MaterialTheme.typography.labelSmall) else Button(onClick = onAccept, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.height(32.dp)) { Text("Accept", fontSize = 12.sp) }
                "blocked" -> Text("Blocked", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                else -> Button(onClick = onAdd, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.height(32.dp)) { Text("Add", fontSize = 12.sp) }
            }
        }
    }
}

@Composable
private fun BlockedUserItem(
    profile: Profile,
    onUnblock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Text(profile.display_name?.take(1) ?: profile.username?.take(1) ?: "?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.display_name ?: profile.username ?: "Unknown", fontWeight = FontWeight.Bold)
                Text("@${profile.username ?: "unknown"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            TextButton(onClick = onUnblock) {
                Text("Unblock", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyQrModal(
    currentUser: UserInfo,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
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
                val qrContent = "myemotions://addfriend/${currentUser.id}"
                val qrBitmap = remember(qrContent) { generateQrCode(qrContent) }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (qrBitmap != null) {
                        Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.size(200.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "@${currentUser.userMetadata?.get("username")?.toString()?.replace("\"", "") ?: currentUser.email}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
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
