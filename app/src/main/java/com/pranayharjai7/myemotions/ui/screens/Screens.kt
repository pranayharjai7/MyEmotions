package com.pranayharjai7.myemotions.ui.screens

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.pranayharjai7.myemotions.R
import com.pranayharjai7.myemotions.core.utils.GoogleAuthManager
import com.pranayharjai7.myemotions.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    LaunchedEffect(Unit) {
        delay(1800)
        viewModel.checkAuthStatus { isLoggedIn ->
            if (isLoggedIn) onNavigateToDashboard() else onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = R.drawable.logo,
            contentDescription = "Logo",
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )
    }
}

@Composable
fun AuthScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val googleAuthManager = remember { GoogleAuthManager(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateToDashboard()
        } else if (authState is AuthState.Error) {
            snackbarHostState.showSnackbar((authState as AuthState.Error).message)
        }
    }

    AnimatedGradientBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top Section: Beating Heart Icon Animation
                val infiniteTransition = rememberInfiniteTransition(label = "HeartBeatTransition")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "HeartScaleAnimation"
                )
                
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        ),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = "My Emotions",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                
                // Tagline with Animation
                AnimatedContent(
                    targetState = isSignUp,
                    transitionSpec = {
                        fadeIn(tween(400)) + slideInVertically { it / 2 } togetherWith
                        fadeOut(tween(400)) + slideOutVertically { -it / 2 }
                    },
                    label = "TaglineAnimation"
                ) { targetIsSignUp ->
                    Text(
                        text = if (targetIsSignUp) "Join us to begin your emotional journey" else "Navigate your inner world with ease",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Middle Section: Auth Fields
                Column {
                    var name by remember { mutableStateOf("") }
                    
                    // Optional Name field for Sign Up
                    AnimatedVisibility(
                        visible = isSignUp,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            EmotionTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Full Name",
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    EmotionTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EmotionTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Primary Action Button with Text Animation
                EmotionButton(
                    onClick = {
                        if (isSignUp) viewModel.signUp(email, password)
                        else viewModel.signIn(email, password)
                    },
                    isLoading = authState is AuthState.Loading
                ) {
                    AnimatedContent(
                        targetState = isSignUp,
                        transitionSpec = {
                            fadeIn(tween(300)) + slideInHorizontally { it / 2 } togetherWith
                            fadeOut(tween(300)) + slideOutHorizontally { -it / 2 }
                        },
                        label = "ButtonTextAnimation"
                    ) { targetIsSignUp ->
                        Text(
                            text = if (targetIsSignUp) "Create Account" else "Sign In",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Option: Mode Toggle with Animation
                TextButton(onClick = { isSignUp = !isSignUp }) {
                    AnimatedContent(
                        targetState = isSignUp,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                        },
                        label = "ModeToggleAnimation"
                    ) { targetIsSignUp ->
                        Text(
                            text = if (targetIsSignUp) "Already have an account? Sign In" else "New here? Create Account",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider Section
                EmotionDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign In
                GoogleSignInButton(
                    onClick = {
                        scope.launch {
                            viewModel.setLoading(true) 
                            val idToken = googleAuthManager.signIn()
                            if (idToken != null) {
                                viewModel.signInWithGoogle(idToken)
                            } else {
                                viewModel.setLoading(false)
                                snackbarHostState.showSnackbar("Google Sign-In cancelled")
                            }
                        }
                    },
                    enabled = authState !is AuthState.Loading
                )

                // Error Message (shown only when relevant)
                AnimatedVisibility(
                    visible = authState is AuthState.Error,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val errorMsg = (authState as? AuthState.Error)?.message ?: ""
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onLogout: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "My Emotions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Pranay",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            EmotionCard(
                modifier = Modifier.height(200.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Emotion Insights",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                    )
                }
            }
        }
    }
}
