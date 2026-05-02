package com.pranayharjai7.myemotions.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pranayharjai7.myemotions.core.utils.GoogleAuthManager
import com.pranayharjai7.myemotions.ui.components.*
import kotlinx.coroutines.launch

/**
 * Authentication screen providing login, signup, and Google authentication.
 */
@Composable
fun AuthScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
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
                AuthHeader(isSignUp = isSignUp)

                Spacer(modifier = Modifier.height(48.dp))

                AuthFields(
                    isSignUp = isSignUp,
                    name = name,
                    onNameChange = { name = it },
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                AuthActions(
                    isSignUp = isSignUp,
                    isLoading = authState is AuthState.Loading,
                    onPrimaryAction = {
                        if (isSignUp) viewModel.signUp(email, password)
                        else viewModel.signIn(email, password)
                    },
                    onToggleMode = { isSignUp = !isSignUp },
                    onGoogleSignIn = {
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
                    }
                )

                AuthError(authState = authState)
            }
        }
    }
}

@Composable
private fun AuthHeader(isSignUp: Boolean) {
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

    Text(
        text = "My Emotions",
        style = MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    )

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
}

@Composable
private fun AuthFields(
    isSignUp: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    Column {
        AnimatedVisibility(
            visible = isSignUp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                EmotionTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = "Full Name",
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        EmotionTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email Address",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmotionTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}

@Composable
private fun AuthActions(
    isSignUp: Boolean,
    isLoading: Boolean,
    onPrimaryAction: () -> Unit,
    onToggleMode: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    EmotionButton(
        onClick = onPrimaryAction,
        isLoading = isLoading
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

    TextButton(onClick = onToggleMode) {
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

    EmotionDivider()

    Spacer(modifier = Modifier.height(16.dp))

    GoogleSignInButton(
        onClick = onGoogleSignIn,
        enabled = !isLoading
    )
}

@Composable
private fun AuthError(authState: AuthState) {
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
