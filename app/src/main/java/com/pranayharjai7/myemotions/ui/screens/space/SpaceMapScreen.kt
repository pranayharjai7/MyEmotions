package com.pranayharjai7.myemotions.ui.screens.space

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.pranayharjai7.myemotions.ui.theme.getThemeForEmotion
import com.pranayharjai7.myemotions.ui.utils.fetchAndUpdateLocation
import com.pranayharjai7.myemotions.ui.utils.getCurrentLocation
import com.pranayharjai7.myemotions.ui.components.EmotionIcon
import kotlinx.coroutines.launch
import android.text.format.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceMapScreen(
    onNavigateToProfile: (String) -> Unit,
    viewModel: SpaceMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val markers by viewModel.mapMarkers.collectAsStateWithLifecycle()
    val isGhostMode by viewModel.isGhostMode.collectAsStateWithLifecycle()
    val selectedFriend by viewModel.selectedFriend.collectAsStateWithLifecycle()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.values.any { it }
            if (hasLocationPermission) {
                // If we want to force fetch here, we could pass a callback to the ViewModel
                // For now, the user can use the default Maps "My Location" button
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f) // Default global view
    }
    
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
    val isMapReady = lifecycleState == androidx.lifecycle.Lifecycle.State.RESUMED

    Box(modifier = Modifier.fillMaxSize()) {
        if (isMapReady) {
            GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false
            )
        ) {
            markers.forEach { marker ->
                key(marker.profile.id) {
                    val position = LatLng(marker.location.latitude, marker.location.longitude)
                    val markerState = rememberMarkerState(position = position)
                    MarkerComposable(
                        state = markerState,
                        onClick = {
                            viewModel.selectFriend(marker)
                            true // consume click so no default info window
                        }
                    ) {
                        // Custom Marker on Map
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (marker.isStale) Color.Gray else getThemeForEmotion(marker.location.lastEmotion).primaryColor)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (marker.profile.display_name ?: marker.profile.username ?: "?").firstOrNull()?.toString() ?: "?",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        } // End GoogleMap
        } // End isMapReady

        // Overlay floating controls
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onClick = { viewModel.toggleGhostMode() },
                containerColor = if (isGhostMode) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
                contentColor = if (isGhostMode) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isGhostMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Ghost Mode"
                )
            }

            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val myLocation = getCurrentLocation(context)
                        if (myLocation != null) {
                            cameraPositionState.animate(
                                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                                    myLocation, 15f
                                )
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Recenter")
            }
        }

        // Ghost Mode Banner
        if (isGhostMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "👻 Ghost Mode Enabled",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Friend Emotion Card Bottom Sheet
        if (selectedFriend != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.selectFriend(null) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                val friend = selectedFriend!!
                val theme = getThemeForEmotion(friend.location.lastEmotion)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!friend.profile.avatar_url.isNullOrBlank()) {
                        AsyncImage(
                            model = friend.profile.avatar_url,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(theme.primaryColor.copy(alpha = 0.2f))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(theme.primaryColor.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (friend.profile.display_name ?: friend.profile.username ?: "?").firstOrNull()?.toString() ?: "?",
                                style = MaterialTheme.typography.displaySmall,
                                color = theme.primaryColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = friend.profile.display_name ?: friend.profile.username ?: "Unknown",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Emotion Label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(theme.primaryColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        EmotionIcon(emotion = friend.location.lastEmotion ?: "Unknown", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = friend.location.lastEmotion ?: "Unknown",
                            style = MaterialTheme.typography.bodyLarge,
                            color = theme.primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = DateUtils.getRelativeTimeSpanString(friend.location.updatedAt).toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToProfile(friend.profile.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primaryColor)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Profile")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
