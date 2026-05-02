package com.pranayharjai7.myemotions.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranayharjai7.myemotions.ui.theme.MoodHappy
import com.pranayharjai7.myemotions.ui.theme.MoodNeutral
import com.pranayharjai7.myemotions.ui.theme.MoodSad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mood Analytics") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            ChartCard("Weekly Mood Distribution") {
                MoodDistributionChart()
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ChartCard("Mood Trends") {
                MoodTrendsChart()
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StatCard("Most Common Emotion", "Happiness", MoodHappy)
            Spacer(modifier = Modifier.height(16.dp))
            StatCard("Overall Balance", "Calm & Positive", MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
private fun MoodDistributionChart() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val data = listOf(0.4f, 0.2f, 0.15f, 0.1f, 0.15f)
        val colors = listOf(MoodHappy, MoodSad, MaterialTheme.colorScheme.primary, MoodNeutral, Color.Gray)
        val labels = listOf("Happy", "Sad", "Calm", "Angry", "Other")

        data.forEachIndexed { index, value ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height((value * 180).dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(colors[index])
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = labels[index], style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun MoodTrendsChart() {
    // A simple placeholder for a line chart using boxes
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(7) { index ->
            val height = (40..100).random().dp
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(height)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = if (index == 6) 1f else 0.3f))
            )
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text(text = value, style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}
