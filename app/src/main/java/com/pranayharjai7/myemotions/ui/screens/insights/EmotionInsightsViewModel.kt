package com.pranayharjai7.myemotions.ui.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

enum class TimeFilter { TODAY, WEEK, MONTH, ALL_TIME }

data class DailyTimelineData(
    val date: LocalDate,
    val records: List<EmotionRecord>,
    val dominantEmotion: String,
    val entryCount: Int,
    val dailyBalanceScore: Int
)

data class AnalyticsData(
    val moodDistribution: Map<String, Float>, // emotion -> percentage
    val moodTrend: List<Pair<Long, Float>>, // timestamp -> valence average
    val mostCommonEmotion: String?,
    val mostCommonEmotionCount: Int,
    val balanceScore: Int,
    val balanceText: String
)

data class InsightsUiState(
    val isLoading: Boolean = false,
    val timeFilter: TimeFilter = TimeFilter.WEEK,
    val selectedEmotionFilter: String? = null,
    val allRecords: List<EmotionRecord> = emptyList(),
    val filteredRecords: List<EmotionRecord> = emptyList(),
    val analyticsData: AnalyticsData? = null,
    val groupedTimeline: Map<LocalDate, DailyTimelineData> = emptyMap()
)

@HiltViewModel
class EmotionInsightsViewModel @Inject constructor(
    private val repository: EmotionRepository
) : ViewModel() {

    private val _timeFilter = MutableStateFlow(TimeFilter.WEEK)
    private val _selectedEmotionFilter = MutableStateFlow<String?>(null)
    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<InsightsUiState> = combine(
        repository.getEmotionHistory(),
        _timeFilter,
        _selectedEmotionFilter,
        _isRefreshing
    ) { allRecords, timeFilter, selectedEmotion, isRefreshing ->
        val now = Instant.now()
        val zoneId = ZoneId.systemDefault()
        val todayStart = LocalDate.now().atStartOfDay(zoneId).toInstant()

        // Filter by time
        val timeFilteredRecords = allRecords.filter { record ->
            val recordInstant = Instant.ofEpochMilli(record.timestamp)
            when (timeFilter) {
                TimeFilter.TODAY -> recordInstant.isAfter(todayStart)
                TimeFilter.WEEK -> recordInstant.isAfter(todayStart.minus(7, ChronoUnit.DAYS))
                TimeFilter.MONTH -> recordInstant.isAfter(todayStart.minus(30, ChronoUnit.DAYS))
                TimeFilter.ALL_TIME -> true
            }
        }

        // Calculate Analytics on timeFilteredRecords (before applying emotion filter)
        val analytics = computeAnalytics(timeFilteredRecords, timeFilter)

        // Filter by selected emotion (if any) for the timeline
        val timelineRecords = if (selectedEmotion != null) {
            timeFilteredRecords.filter { it.emotion.equals(selectedEmotion, ignoreCase = true) }
        } else {
            timeFilteredRecords
        }

        // Group timeline
        val grouped = timelineRecords
            .groupBy { Instant.ofEpochMilli(it.timestamp).atZone(zoneId).toLocalDate() }
            .mapValues { (date, records) ->
                val dominant = records.groupingBy { it.emotion }.eachCount().maxByOrNull { it.value }?.key ?: "Unknown"
                val dailyBalance = computeBalanceScore(records)
                DailyTimelineData(
                    date = date,
                    records = records.sortedByDescending { it.timestamp },
                    dominantEmotion = dominant,
                    entryCount = records.size,
                    dailyBalanceScore = dailyBalance.first
                )
            }.toSortedMap(reverseOrder())

        InsightsUiState(
            isLoading = isRefreshing,
            timeFilter = timeFilter,
            selectedEmotionFilter = selectedEmotion,
            allRecords = allRecords,
            filteredRecords = timelineRecords,
            analyticsData = analytics,
            groupedTimeline = grouped
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsUiState(isLoading = true)
    )

    fun setTimeFilter(filter: TimeFilter) {
        _timeFilter.value = filter
        _selectedEmotionFilter.value = null // reset emotion filter when time changes
    }

    fun setEmotionFilter(emotion: String?) {
        // Toggle off if same emotion is selected again
        _selectedEmotionFilter.value = if (_selectedEmotionFilter.value == emotion) null else emotion
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.fetchEmotionsFromRemote()
            _isRefreshing.value = false
        }
    }

    private fun getValenceScore(emotion: String): Float {
        return when (emotion.lowercase()) {
            "happiness" -> 1.0f
            "calm" -> 0.8f
            "surprise" -> 0.6f
            "neutral" -> 0.5f
            "sadness" -> 0.3f
            "fear" -> 0.2f
            "disgust" -> 0.2f
            "contempt" -> 0.1f
            "anger" -> 0.1f
            else -> 0.5f
        }
    }

    private fun computeAnalytics(records: List<EmotionRecord>, filter: TimeFilter): AnalyticsData? {
        if (records.isEmpty()) return null

        // Mood Distribution
        val total = records.size.toFloat()
        val distribution = records.groupingBy { it.emotion }
            .eachCount()
            .mapValues { it.value / total }

        // Most Common
        val mostCommonEntry = records.groupingBy { it.emotion }.eachCount().maxByOrNull { it.value }
        val mostCommonEmotion = mostCommonEntry?.key
        val mostCommonCount = mostCommonEntry?.value ?: 0

        // Balance Score
        val (balanceScore, balanceText) = computeBalanceScore(records)

        // Mood Trend
        // Simple grouping by time buckets.
        val zoneId = ZoneId.systemDefault()
        val trend = when (filter) {
            TimeFilter.TODAY -> {
                // Group by hour
                records.groupBy { Instant.ofEpochMilli(it.timestamp).atZone(zoneId).hour }
                    .map { (hour, recs) -> 
                        val avgValence = recs.map { getValenceScore(it.emotion) }.average().toFloat()
                        // Use a dummy timestamp for the hour
                        val ts = LocalDate.now().atTime(hour, 0).atZone(zoneId).toInstant().toEpochMilli()
                        Pair(ts, avgValence)
                    }.sortedBy { it.first }
            }
            else -> {
                // Group by day
                records.groupBy { Instant.ofEpochMilli(it.timestamp).atZone(zoneId).toLocalDate() }
                    .map { (date, recs) ->
                        val avgValence = recs.map { getValenceScore(it.emotion) }.average().toFloat()
                        val ts = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
                        Pair(ts, avgValence)
                    }.sortedBy { it.first }
            }
        }

        return AnalyticsData(
            moodDistribution = distribution,
            moodTrend = trend,
            mostCommonEmotion = mostCommonEmotion,
            mostCommonEmotionCount = mostCommonCount,
            balanceScore = balanceScore,
            balanceText = balanceText
        )
    }

    private fun computeBalanceScore(records: List<EmotionRecord>): Pair<Int, String> {
        if (records.isEmpty()) return Pair(0, "Not enough data")

        val total = records.size.toFloat()
        
        // Positive Ratio: % of Happiness, Calm, Surprise
        val positiveEmotions = setOf("happiness", "calm", "surprise")
        val positiveCount = records.count { it.emotion.lowercase() in positiveEmotions }
        val positiveRatio = positiveCount / total // 0.0 to 1.0

        // Stability Factor: Inverse of variance of valence scores
        val valences = records.map { getValenceScore(it.emotion) }
        val meanValence = valences.average().toFloat()
        val variance = valences.map { Math.pow((it - meanValence).toDouble(), 2.0) }.average().toFloat()
        // variance is typically small, max variance is around 0.2. So stability = 1 - (variance * 4), clamped 0..1
        val stabilityFactor = (1f - (variance * 4f)).coerceIn(0f, 1f)

        // Emotion Diversity
        val uniqueEmotions = records.map { it.emotion.lowercase() }.toSet().size
        val emotionDiversity = when {
            uniqueEmotions <= 1 -> 0.0f
            uniqueEmotions in 2..3 -> 0.5f
            else -> 1.0f
        }

        // Formula: (PositiveRatio * 50) + (StabilityFactor * 30) + (EmotionDiversity * 20)
        val score = (positiveRatio * 50f) + (stabilityFactor * 30f) + (emotionDiversity * 20f)
        val finalScore = score.toInt().coerceIn(0, 100)

        val text = when {
            finalScore >= 80 -> "Your emotional health appears highly stable."
            finalScore >= 50 -> "Your mood is fluctuating, but generally balanced."
            else -> "You've experienced significant emotional shifts."
        }

        return Pair(finalScore, text)
    }
}
