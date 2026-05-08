package com.pranayharjai7.myemotions.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun ProfileMenu(
    userInfo: UserInfo?,
    onDismiss: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToManageFriends: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLogout: () -> Unit
) {
    val name = userInfo?.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull ?: "User"
    val email = userInfo?.email ?: ""
    val avatarUrl = userInfo?.userMetadata?.get("avatar_url")?.jsonPrimitive?.contentOrNull

    Popup(
        alignment = Alignment.TopEnd,
        offset = IntOffset(0, 160),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        // Azure Glass Styling
        Card(
            modifier = Modifier
                .width(280.dp)
                .padding(end = 16.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                // Header (Profile Card)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onNavigateToProfile()
                            onDismiss()
                        },
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
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
                                Text(
                                    text = name.take(1).uppercase(),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (email.isNotEmpty()) {
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                MenuListItem(icon = Icons.Default.Group, label = "Manage Friends", onClick = { onNavigateToManageFriends(); onDismiss() })

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                MenuListItem(icon = Icons.Default.Settings, label = "Settings", onClick = { onNavigateToSettings(); onDismiss() })
                MenuListItem(icon = Icons.Default.Notifications, label = "Reminders", onClick = { onNavigateToReminders(); onDismiss() })
                MenuListItem(icon = Icons.Default.HelpOutline, label = "Help & About", onClick = { onNavigateToHelp(); onDismiss() })

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                MenuListItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    label = "Sign Out",
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = { onLogout(); onDismiss() }
                )
            }
        }
    }
}

@Composable
private fun MenuListItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
