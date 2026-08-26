package com.example.subtitle

import com.example.data.model.ContextMode
import com.example.data.model.LiveSubtitleLine
import com.example.data.remote.GeminiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class SubtitleParser(private val geminiApiService: GeminiApiService) {

    /**
     * Parses SRT or WebVTT string content into structured subtitle lines
     */
    fun parseSubtitleContent(content: String): List<LiveSubtitleLine> {
        val lines = mutableListOf<LiveSubtitleLine>()
        val blocks = content.replace("\r\n", "\n").split("\n\n")

        val timePattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}[,\\.]\\d{3})\\s*-->\\s*(\\d{2}:\\d{2}:\\d{2}[,\\.]\\d{3})")

        for (block in blocks) {
            val blockLines = block.trim().lines()
            if (blockLines.isEmpty()) continue

            var timeMatcher: java.util.regex.Matcher? = null
            var textStartIndex = 0

            for (i in blockLines.indices) {
                val matcher = timePattern.matcher(blockLines[i])
                if (matcher.find()) {
                    timeMatcher = matcher
                    textStartIndex = i + 1
                    break
                }
            }

            if (timeMatcher != null && textStartIndex < blockLines.size) {
                val startMs = parseTimestampToMs(timeMatcher.group(1) ?: "00:00:00,000")
                val endMs = parseTimestampToMs(timeMatcher.group(2) ?: "00:00:00,000")
                val text = blockLines.subList(textStartIndex, blockLines.size)
                    .joinToString(" ")
                    .replace(Regex("<[^>]*>"), "") // Remove HTML tags
                    .trim()

                if (text.isNotBlank()) {
                    lines.add(
                        LiveSubtitleLine(
                            startTimeMs = startMs,
                            endTimeMs = endMs,
                            originalText = text,
                            translatedText = ""
                        )
                    )
                }
            }
        }

        // If standard SRT format wasn't found, fallback to line-by-line parsing
        if (lines.isEmpty()) {
            val simpleLines = content.lines().filter { it.isNotBlank() }
            var currentMs = 0L
            for (line in simpleLines) {
                lines.add(
                    LiveSubtitleLine(
                        startTimeMs = currentMs,
                        endTimeMs = currentMs + 3000,
                        originalText = line.trim(),
                        translatedText = ""
                    )
                )
                currentMs += 3500
            }
        }

        return lines
    }

    /**
     * Translates a batch of subtitle lines using Gemini API with Movie/Dialogue context
     */
    suspend fun translateSubtitles(
        subtitles: List<LiveSubtitleLine>,
        targetLang: String = "ar",
        onProgress: (Int, Int) -> Unit = { _, _ -> }
    ): List<LiveSubtitleLine> = withContext(Dispatchers.IO) {
        val resultList = mutableListOf<LiveSubtitleLine>()
        val batchSize = 10

        for (i in subtitles.indices step batchSize) {
            val chunk = subtitles.subList(i, minOf(i + batchSize, subtitles.size))
            val combinedText = chunk.mapIndexed { idx, item -> "[$idx] ${item.originalText}" }.joinToString("\n")

            val translationResult = geminiApiService.translateText(
                text = combinedText,
                sourceLang = "auto",
                targetLang = targetLang,
                contextMode = ContextMode.MOVIES_DIALOGUE
            ).getOrNull()

            val translatedCombined = translationResult?.translatedText ?: combinedText
            val translatedLines = translatedCombined.lines()

            for (j in chunk.indices) {
                val lineIndex = i + j
                val original = chunk[j]
                val translatedText = translatedLines.find { it.startsWith("[$j]") }
                    ?.removePrefix("[$j]")
                    ?.trim()
                    ?: if (j < translatedLines.size) translatedLines[j].replace(Regex("^\\[\\d+\\]\\s*"), "") else original.originalText

                resultList.add(original.copy(translatedText = translatedText))
            }

            onProgress(resultList.size, subtitles.size)
        }

        resultList
    }

    private fun parseTimestampToMs(timeStr: String): Long {
        try {
            val parts = timeStr.replace(',', '.').split(":")
            if (parts.size == 3) {
                val hours = parts[0].toLong()
                val minutes = parts[1].toLong()
                val secParts = parts[2].split(".")
                val seconds = secParts[0].toLong()
                val millis = if (secParts.size > 1) secParts[1].padEnd(3, '0').take(3).toLong() else 0L
                return (hours * 3600 + minutes * 60 + seconds) * 1000 + millis
            }
        } catch (e: Exception) {
            // Ignore parse errors
        }
        return 0L
    }
}
