package com.pranayharjai7.myemotions.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.pranayharjai7.myemotions.data.local.mtcnn.EmotionPyTorchClassifier
import com.pranayharjai7.myemotions.data.local.mtcnn.MTCNN
import com.pranayharjai7.myemotions.data.local.room.EmotionDao
import com.pranayharjai7.myemotions.data.local.room.EmotionRecordEntity
import com.pranayharjai7.myemotions.data.remote.supabase.EmotionRecordDto
import com.pranayharjai7.myemotions.data.worker.EmotionSyncWorker
import com.pranayharjai7.myemotions.domain.model.EmotionRecord
import com.pranayharjai7.myemotions.domain.model.EmotionResult
import com.pranayharjai7.myemotions.domain.model.EmotionType
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class EmotionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emotionDao: EmotionDao,
    private val supabaseClient: SupabaseClient
) : EmotionRepository {

    private val mtcnn by lazy { MTCNN(context) }
    private val classifier by lazy { EmotionPyTorchClassifier(context) }

    override suspend fun detectEmotion(bitmap: Bitmap): Result<EmotionResult> = withContext(Dispatchers.IO) {
        try {
            val minSize = 600.0
            val scale = min(bitmap.width, bitmap.height) / minSize
            var resizedBitmap = bitmap
            if (scale > 1.0) {
                resizedBitmap = Bitmap.createScaledBitmap(
                    bitmap, 
                    (bitmap.width / scale).toInt(), 
                    (bitmap.height / scale).toInt(), 
                    false
                )
            }

            val minFaceSize = 32
            val boxes = mtcnn.detectFaces(resizedBitmap, minFaceSize)
            
            if (boxes.isEmpty()) {
                return@withContext Result.failure(Exception("No face detected"))
            }

            val largestBox = boxes.maxByOrNull { it.area() } ?: return@withContext Result.failure(Exception("No face detected"))
            
            val bbox = largestBox.transform2Rect()
            
            val w = bitmap.width
            val h = bitmap.height
            val rw = resizedBitmap.width.toFloat()
            val rh = resizedBitmap.height.toFloat()
            
            val leftOrig = max(0, (w * bbox.left / rw).toInt())
            val topOrig = max(0, (h * bbox.top / rh).toInt())
            val rightOrig = min(w, (w * bbox.right / rw).toInt())
            val bottomOrig = min(h, (h * bbox.bottom / rh).toInt())
            
            val cropW = rightOrig - leftOrig
            val cropH = bottomOrig - topOrig
            
            if (cropW <= 0 || cropH <= 0) {
                return@withContext Result.failure(Exception("Invalid face bounding box"))
            }

            val faceBitmap = Bitmap.createBitmap(bitmap, leftOrig, topOrig, cropW, cropH)
            val (label, confidence) = classifier.recognizeWithConfidence(faceBitmap)
            val emotionType = EmotionType.fromLabel(label)
            
            Result.success(EmotionResult(emotionType, confidence))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveEmotion(record: EmotionRecord) {
        val user = supabaseClient.auth.currentUserOrNull() ?: throw IllegalStateException("User not logged in")
        val userRecord = record.copy(userId = user.id)
        emotionDao.insertEmotion(EmotionRecordEntity.fromDomainModel(userRecord, synced = false))
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncRequest = OneTimeWorkRequestBuilder<EmotionSyncWorker>()
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(context).enqueue(syncRequest)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getEmotionHistory(): Flow<List<EmotionRecord>> {
        return supabaseClient.auth.sessionStatus.flatMapLatest { status ->
            when (status) {
                is io.github.jan.supabase.gotrue.SessionStatus.Authenticated -> {
                    val userId = status.session.user?.id ?: return@flatMapLatest kotlinx.coroutines.flow.flowOf(emptyList())
                    emotionDao.getAllEmotions(userId).map { entities ->
                        entities.map { it.toDomainModel() }
                    }
                }
                else -> kotlinx.coroutines.flow.flowOf(emptyList())
            }
        }
    }

    override fun getEmotionsByUserId(userId: String): Flow<List<EmotionRecord>> = kotlinx.coroutines.flow.flow {
        try {
            val remoteRecords = supabaseClient.postgrest["emotion_records"]
                .select {
                    filter {
                        eq("user_id", userId)
                        // Only fetch records with visibility = 'friends' or 'public'
                        // Since we are fetching a friend's records, 'private' should be excluded
                        neq("visibility", "private")
                    }
                    order("timestamp", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<EmotionRecordDto>()
            
            emit(remoteRecords.map { dto ->
                EmotionRecord(
                    id = dto.id,
                    userId = dto.userId,
                    timestamp = dto.timestamp,
                    emotion = dto.emotion,
                    confidence = dto.confidence,
                    source = dto.source,
                    imageUri = dto.imageUri,
                    visibility = dto.visibility,
                    note = dto.note
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override suspend fun syncPendingEmotions() {
        try {
            val user = supabaseClient.auth.currentUserOrNull() ?: return
            val unsynced = emotionDao.getUnsyncedEmotions(user.id)
            if (unsynced.isEmpty()) return

            val dtos = unsynced.map { entity ->
                EmotionRecordDto(
                    id = entity.id,
                    userId = user.id,
                    timestamp = entity.timestamp,
                    emotion = entity.emotion,
                    confidence = entity.confidence,
                    source = entity.source,
                    imageUri = entity.imageUri,
                    visibility = entity.visibility,
                    note = entity.note
                )
            }

            supabaseClient.postgrest["emotion_records"].insert(dtos)
            
            unsynced.forEach {
                emotionDao.markAsSynced(it.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun fetchEmotionsFromRemote() {
        try {
            val user = supabaseClient.auth.currentUserOrNull() ?: return
            
            val remoteRecords = supabaseClient.postgrest["emotion_records"]
                .select {
                    filter {
                        eq("user_id", user.id)
                    }
                }
                .decodeList<EmotionRecordDto>()
                
            remoteRecords.forEach { dto ->
                val domainRecord = EmotionRecord(
                    id = dto.id,
                    userId = dto.userId,
                    timestamp = dto.timestamp,
                    emotion = dto.emotion,
                    confidence = dto.confidence,
                    source = dto.source,
                    imageUri = dto.imageUri,
                    visibility = dto.visibility,
                    note = dto.note
                )
                emotionDao.insertEmotion(EmotionRecordEntity.fromDomainModel(domainRecord, synced = true))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
