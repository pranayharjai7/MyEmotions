package com.pranayharjai7.myemotions.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.pranayharjai7.myemotions.data.local.mtcnn.EmotionPyTorchClassifier
import com.pranayharjai7.myemotions.data.local.mtcnn.MTCNN
import com.pranayharjai7.myemotions.domain.model.EmotionResult
import com.pranayharjai7.myemotions.domain.model.EmotionType
import com.pranayharjai7.myemotions.domain.repository.EmotionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class EmotionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
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
}
