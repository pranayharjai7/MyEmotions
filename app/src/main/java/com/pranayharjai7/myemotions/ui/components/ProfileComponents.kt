package com.pranayharjai7.myemotions.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * A circular profile entry point that displays the user's Google profile picture.
 */
@Composable
fun ProfileAvatar(
    userInfo: UserInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color? = null
) {
    val avatarUrl = userInfo?.userMetadata?.get("avatar_url")?.jsonPrimitive?.contentOrNull
    val name = userInfo?.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull ?: "User"

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .then(
                if (borderColor != null) Modifier.border(2.dp, borderColor, CircleShape)
                else Modifier
            )
            .background(Color.White.copy(alpha = 0.2f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrEmpty()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Profile",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                // Add crossfade for smoother transition
                // Placeholder can be handled by the Box background
            )
        } else {
            Text(
                text = name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * A premium glassmorphic bottom sheet containing account and app settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    userInfo: UserInfo?,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val name = userInfo?.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull ?: "Welcome back"
    val email = userInfo?.email ?: ""
    val avatarUrl = userInfo?.userMetadata?.get("avatar_url")?.jsonPrimitive?.contentOrNull

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A).copy(alpha = 0.98f), // Deep Glass Dark
        scrimColor = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 64.dp)
        ) {
            // Profile Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!avatarUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (email.isNotEmpty()) {
                        Text(
                            text = email,
                            color = Color.White.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Menu Groups
            ProfileMenuItem(
                icon = Icons.Default.History,
                label = "Emotion History",
                onClick = {
                    onNavigateToHistory()
                    onDismiss()
                }
            )
            ProfileMenuItem(
                icon = Icons.Default.AutoGraph,
                label = "Mood Insights",
                onClick = onNavigateToInsights
            )
            ProfileMenuItem(
                icon = Icons.Default.Palette,
                label = "Theme & Appearance",
                onClick = onNavigateToSettings
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Account & App",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ProfileMenuItem(icon = Icons.Default.Notifications, label = "Reminders", onClick = {})
            ProfileMenuItem(icon = Icons.Default.Shield, label = "Privacy", onClick = {})
            ProfileMenuItem(icon = Icons.Default.Feedback, label = "Feedback", onClick = {})
            ProfileMenuItem(icon = Icons.Default.Info, label = "About MyEmotions", onClick = {})
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                label = "Sign Out",
                textColor = Color(0xFFFF5252),
                iconColor = Color(0xFFFF5252),
                showArrow = false,
                onClick = {
                    onLogout()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    textColor: Color = Color.White,
    iconColor: Color = Color.White.copy(alpha = 0.6f),
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.2f)), // Increased contrast
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
