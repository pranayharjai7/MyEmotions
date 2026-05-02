package com.pranayharjai7.myemotions.data.local.mtcnn

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EmotionPyTorchClassifier(context: Context) {

    private val labels: List<String>
    private val module: Module
    private val width = 224
    private val height = 224

    init {
        module = LiteModuleLoader.load(assetFilePath(context, MODEL_FILE))
        labels = loadLabels(context)
    }

    fun recognize(bitmap: Bitmap): String {
        val res = classifyImage(bitmap)
        val scores = res.second
        val numEmotions = Math.min(labels.size, scores.size)
        val index = Array(numEmotions) { it }
        
        index.sortWith { idx1, idx2 -> java.lang.Float.compare(scores[idx2], scores[idx1]) }
        return labels[index[0]]
    }
    
    fun recognizeWithConfidence(bitmap: Bitmap): Pair<String, Float> {
        val res = classifyImage(bitmap)
        val scores = res.second
        val numEmotions = Math.min(labels.size, scores.size)
        val index = Array(numEmotions) { it }
        index.sortWith { idx1, idx2 -> java.lang.Float.compare(scores[idx2], scores[idx1]) }
        
        val topLabel = labels[index[0]]
        val topScore = scores[index[0]]
        
        return Pair(topLabel, topScore)
    }

    private fun classifyImage(bitmap: Bitmap): Pair<Long, FloatArray> {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            scaledBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val startTime = SystemClock.uptimeMillis()
        val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
        val timecostMs = SystemClock.uptimeMillis() - startTime
        val scores = outputTensor.dataAsFloatArray
        return Pair(timecostMs, scores)
    }

    private fun loadLabels(context: Context): List<String> {
        val labels = mutableListOf<String>()
        try {
            context.assets.open("emotionsLabel.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val categoryInfo = line.trim().split(":")
                    if (categoryInfo.size > 1) {
                        labels.add(categoryInfo[1])
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading label file", e)
        }
        return labels
    }

    companion object {
        private const val TAG = "EmotionPyTorch"
        private const val MODEL_FILE = "enet_b0_8_va_mtl.ptl"

        @Throws(IOException::class)
        fun assetFilePath(context: Context, assetName: String): String {
            val file = File(context.filesDir, assetName)
            if (file.exists() && file.length() > 0) {
                return file.absolutePath
            }

            context.assets.open(assetName).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (`is`.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
            }
            return file.absolutePath
        }
    }
}
