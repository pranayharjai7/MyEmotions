package com.pranayharjai7.myemotions.ui.screens.insights

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pranayharjai7.myemotions.ui.theme.getThemeForEmotion
import com.pranayharjai7.myemotions.ui.utils.getEmojiForEmotion
import java.time.LocalDate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EmotionInsightsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit = {},
    viewModel: EmotionInsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emotion Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    TimeFilterRow(
                        currentFilter = uiState.timeFilter,
                        onFilterSelected = viewModel::setTimeFilter
                    )
                }

                if (uiState.allRecords.isEmpty()) {
                    item {
                        EmptyInsightsState()
                    }
                } else {
                    item {
                        uiState.analyticsData?.let { analytics ->
                            AnalyticsSection(
                                analytics = analytics,
                                selectedEmotion = uiState.selectedEmotionFilter,
                                onEmotionSelected = viewModel::setEmotionFilter
                            )
                        }
                    }
                    
                    // Timeline Section
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Your Emotional History",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                        )
                    }

                    uiState.groupedTimeline.forEach { (date, dailyData) ->
                        stickyHeader {
                            DateHeaderWithSummary(dailyData)
                        }
                        
                        items(dailyData.records) { record ->
                            EmotionCard(record = record, onClick = { onNavigateToDetails(record.id) })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeFilterRow(
    currentFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TimeFilter.entries.forEach { filter ->
            val isSelected = currentFilter == filter
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                modifier = Modifier
                    .clickable { onFilterSelected(filter) }
                    .padding(4.dp)
            ) {
                Text(
                    text = filter.name.replace("_", " "),
                    color = textColor,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AnalyticsSection(
    analytics: AnalyticsData,
    selectedEmotion: String?,
    onEmotionSelected: (String?) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your Emotional Patterns",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        MoodDistributionCard(
            distribution = analytics.moodDistribution,
            selectedEmotion = selectedEmotion,
            onEmotionSelected = onEmotionSelected
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        MoodTrendCard(trend = analytics.moodTrend)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                title = "Most Common",
                value = analytics.mostCommonEmotion ?: "-",
                subtitle = "${analytics.mostCommonEmotionCount} entries",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatCard(
                title = "Balance Score",
                value = "${analytics.balanceScore}",
                subtitle = "Out of 100",
                modifier = Modifier.weight(1f)
            )
        }
        
        if (analytics.balanceText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = analytics.balanceText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    val emotionTheme = getThemeForEmotion(if (title == "Most Common") value else null)
    val valueColor = if (title == "Balance Score") MaterialTheme.colorScheme.primary else emotionTheme.primaryColor

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = valueColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun MoodDistributionCard(
    distribution: Map<String, Float>,
    selectedEmotion: String?,
    onEmotionSelected: (String?) -> Unit
) {
    var animationPlayed by remember { mutableStateOf(false) }
    LaunchedEffect(distribution) { animationPlayed = true }
    
    val growthProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "GrowthAnimation"
    )

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Mood Distribution", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))

            val maxPercentage = distribution.values.maxOrNull() ?: 1f
            
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(distribution.entries.toList()) { (emotion, percentage) ->
                    val theme = getThemeForEmotion(emotion)
                    val isSelected = selectedEmotion == null || selectedEmotion == emotion
                    val alpha = if (isSelected) 1f else 0.3f
                    val barHeight = (120.dp * (percentage / maxPercentage) * growthProgress).coerceAtLeast(10.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                if (selectedEmotion == emotion) {
                                    onEmotionSelected(null)
                                } else {
                                    onEmotionSelected(emotion)
                                }
                            }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "${(percentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(barHeight)
                                .background(theme.primaryColor.copy(alpha = alpha), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = getEmojiForEmotion(emotion),
                            fontSize = 20.sp,
                            modifier = Modifier.alpha(alpha)
                        )
                        Text(
                            text = emotion,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tap a bar to filter history",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun MoodTrendCard(trend: List<Pair<Long, Float>>) {
    var animationPlayed by remember { mutableStateOf(false) }
    LaunchedEffect(trend) { animationPlayed = true }
    
    val drawProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "DrawAnimation"
    )

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Mood Trend", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))

            if (trend.size < 2) {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("Not enough data points", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                val lineColor = MaterialTheme.colorScheme.primary
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    
                    val maxValence = 1.0f
                    val minValence = 0.0f
                    
                    val path = Path()
                    val points = trend.mapIndexed { index, pair ->
                        val x = (index.toFloat() / (trend.size - 1)) * width * drawProgress
                        val y = height - ((pair.second - minValence) / (maxValence - minValence) * height)
                        Offset(x, y)
                    }
                    
                    path.moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val p0 = points[i - 1]
                        val p1 = points[i]
                        val cx = (p0.x + p1.x) / 2
                        path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }
                    
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyInsightsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("📊", fontSize = 48.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("No emotional data yet", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Log your mood to start seeing insights and tracking your emotional journey.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun DateHeaderWithSummary(dailyData: DailyTimelineData) {
    val isToday = dailyData.date == LocalDate.now()
    val dateText = if (isToday) "TODAY" else dailyData.date.toString()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = dateText,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${dailyData.entryCount} entries",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Dominant: ${dailyData.dominantEmotion}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Score: ${dailyData.dailyBalanceScore}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
    }
}

@Composable
fun EmotionCard(record: com.pranayharjai7.myemotions.domain.model.EmotionRecord, onClick: () -> Unit) {
    val theme = getThemeForEmotion(record.emotion)
    val dateFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
    val timeString = dateFormat.format(java.util.Date(record.timestamp))

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(theme.primaryColor.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Assuming EmotionIcon exists in your components, if not we fall back to text emoji
                // com.pranayharjai7.myemotions.ui.components.EmotionIcon(emotion = record.emotion, modifier = Modifier.size(24.dp), tint = theme.primaryColor)
                Text(text = getEmojiForEmotion(record.emotion), fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = record.emotion, style = MaterialTheme.typography.titleMedium, color = theme.primaryColor)
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { record.confidence },
                    modifier = Modifier.fillMaxWidth(0.5f).height(4.dp),
                    color = theme.primaryColor,
                    trackColor = theme.primaryColor.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${(record.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (record.source == "manual") Icons.Default.EditNote else androidx.compose.material.icons.Icons.Default.CameraAlt,
                    contentDescription = "Source",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


