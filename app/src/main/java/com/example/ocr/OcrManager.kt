package com.example.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.example.data.model.ScreenOcrBlock
import com.example.data.remote.GeminiApiService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OcrManager(
    private val context: Context,
    private val geminiApiService: GeminiApiService
) {
    private val mlKitRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Extracts text blocks and coordinates from a Bitmap using Google ML Kit on-device
     */
    suspend fun extractTextWithMlKit(bitmap: Bitmap): Result<List<ScreenOcrBlock>> = withContext(Dispatchers.Default) {
        try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            val visionText = mlKitRecognizer.process(inputImage).await()

            val blocks = mutableListOf<ScreenOcrBlock>()
            for (block in visionText.textBlocks) {
                val box = block.boundingBox
                val rectF = if (box != null && bitmap.width > 0 && bitmap.height > 0) {
                    RectF(
                        box.left.toFloat() / bitmap.width,
                        box.top.toFloat() / bitmap.height,
                        box.right.toFloat() / bitmap.width,
                        box.bottom.toFloat() / bitmap.height
                    )
                } else null

                blocks.add(
                    ScreenOcrBlock(
                        text = block.text,
                        translatedText = "",
                        boundingBox = rectF,
                        confidence = 0.98f
                    )
                )
            }

            Result.success(blocks)
        } catch (e: Exception) {
            Log.e("OcrManager", "ML Kit OCR failed", e)
            Result.failure(e)
        }
    }

    /**
     * Extracts and translates text from a specific region/crop of a bitmap
     */
    suspend fun extractAndTranslateCrop(
        fullBitmap: Bitmap,
        cropRect: Rect,
        targetLang: String = "ar"
    ): Result<ScreenOcrBlock> = withContext(Dispatchers.Default) {
        try {
            val safeLeft = cropRect.left.coerceIn(0, fullBitmap.width - 1)
            val safeTop = cropRect.top.coerceIn(0, fullBitmap.height - 1)
            val safeWidth = cropRect.width().coerceIn(1, fullBitmap.width - safeLeft)
            val safeHeight = cropRect.height().coerceIn(1, fullBitmap.height - safeTop)

            val cropped = Bitmap.createBitmap(fullBitmap, safeLeft, safeTop, safeWidth, safeHeight)
            val mlKitResult = extractTextWithMlKit(cropped)

            val extractedText = mlKitResult.getOrNull()?.joinToString("\n") { it.text } ?: ""

            if (extractedText.isNotBlank()) {
                val translationResult = geminiApiService.translateText(
                    text = extractedText,
                    sourceLang = "auto",
                    targetLang = targetLang
                )
                val translated = translationResult.getOrNull()?.translatedText ?: extractedText

                Result.success(
                    ScreenOcrBlock(
                        text = extractedText,
                        translatedText = translated,
                        boundingBox = RectF(
                            safeLeft.toFloat() / fullBitmap.width,
                            safeTop.toFloat() / fullBitmap.height,
                            (safeLeft + safeWidth).toFloat() / fullBitmap.width,
                            (safeTop + safeHeight).toFloat() / fullBitmap.height
                        )
                    )
                )
            } else {
                // If on-device MLKit found nothing, try Gemini Vision OCR
                val geminiBlocks = geminiApiService.analyzeScreenImage(cropped, targetLang).getOrNull()
                val first = geminiBlocks?.firstOrNull()
                if (first != null) {
                    Result.success(first)
                } else {
                    Result.success(ScreenOcrBlock(text = "لم يتم العثور على نص واضح", translatedText = "No clear text detected"))
                }
            }
        } catch (e: Exception) {
            Log.e("OcrManager", "Crop OCR error", e)
            Result.failure(e)
        }
    }

    /**
     * Translates all extracted ML Kit blocks using Gemini API
     */
    suspend fun translateBlocks(
        blocks: List<ScreenOcrBlock>,
        targetLang: String = "ar"
    ): List<ScreenOcrBlock> = withContext(Dispatchers.IO) {
        blocks.map { block ->
            if (block.text.isNotBlank()) {
                val translation = geminiApiService.translateText(
                    text = block.text,
                    sourceLang = "auto",
                    targetLang = targetLang
                ).getOrNull()
                block.copy(translatedText = translation?.translatedText ?: block.text)
            } else {
                block
            }
        }
    }
}
