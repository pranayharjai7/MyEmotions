package com.pranayharjai7.myemotions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pranayharjai7.myemotions.ui.MainViewModel
import com.pranayharjai7.myemotions.ui.components.AnimatedGradientBackground
import com.pranayharjai7.myemotions.ui.navigation.MyEmotionsNavHost
import com.pranayharjai7.myemotions.ui.theme.MyEmotionsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val theme by viewModel.currentTheme.collectAsStateWithLifecycle()

            MyEmotionsTheme(moodTheme = theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedGradientBackground(theme = theme) {
                        val navController = rememberNavController()
                        MyEmotionsNavHost(navController = navController)
                    }
                }
            }
        }
    }
}

