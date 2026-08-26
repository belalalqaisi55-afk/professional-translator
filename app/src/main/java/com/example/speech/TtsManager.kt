package com.example.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.remote.GeminiApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class TtsManager(
    private val context: Context,
    private val geminiApiService: GeminiApiService
) {
    private var nativeTts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _selectedVoice = MutableStateFlow("Kore") // "Kore", "Puck", "Fenrir", "Aoede", "Zephyr"
    val selectedVoice: StateFlow<String> = _selectedVoice.asStateFlow()

    init {
        initNativeTts()
    }

    private fun initNativeTts() {
        nativeTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                nativeTts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            } else {
                Log.w("TtsManager", "Native TTS initialization failed with status $status")
            }
        }
    }

    fun setVoice(voiceName: String) {
        _selectedVoice.value = voiceName
    }

    /**
     * Speaks text using Gemini TTS (gemini-3.1-flash-tts-preview) with native TTS fallback
     */
    fun speak(text: String, languageCode: String = "ar", preferGeminiTts: Boolean = true) {
        if (text.isBlank()) return
        stop()

        if (preferGeminiTts) {
            _isSpeaking.value = true
            scope.launch(Dispatchers.IO) {
                try {
                    val result = geminiApiService.generateSpeechAudio(
                        text = text,
                        voiceName = _selectedVoice.value
                    )
                    val audioBytes = result.getOrNull()
                    if (audioBytes != null && audioBytes.isNotEmpty()) {
                        playAudioBytes(audioBytes)
                    } else {
                        // Fallback to Native TTS
                        speakNative(text, languageCode)
                    }
                } catch (e: Exception) {
                    Log.e("TtsManager", "Gemini TTS error, falling back to native", e)
                    speakNative(text, languageCode)
                }
            }
        } else {
            speakNative(text, languageCode)
        }
    }

    private fun speakNative(text: String, languageCode: String) {
        scope.launch(Dispatchers.Main) {
            if (!isTtsInitialized || nativeTts == null) {
                _isSpeaking.value = false
                return@launch
            }

            val locale = when (languageCode.lowercase()) {
                "ar" -> Locale("ar")
                "en", "en-us" -> Locale.US
                "fr" -> Locale.FRENCH
                "de" -> Locale.GERMAN
                "es" -> Locale("es")
                "it" -> Locale.ITALIAN
                "ja" -> Locale.JAPANESE
                "ko" -> Locale.KOREAN
                "zh", "zh-cn" -> Locale.CHINESE
                "tr" -> Locale("tr")
                "ru" -> Locale("ru")
                else -> Locale.getDefault()
            }

            nativeTts?.language = locale
            val utteranceId = "tts_${System.currentTimeMillis()}"
            _isSpeaking.value = true
            nativeTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    private fun playAudioBytes(bytes: ByteArray) {
        scope.launch(Dispatchers.IO) {
            try {
                // Try playing as audio file with MediaPlayer
                val tempFile = File(context.cacheDir, "gemini_tts_${System.currentTimeMillis()}.mp3")
                FileOutputStream(tempFile).use { it.write(bytes) }

                scope.launch(Dispatchers.Main) {
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(tempFile.absolutePath)
                        setOnCompletionListener {
                            _isSpeaking.value = false
                            tempFile.delete()
                        }
                        setOnErrorListener { _, _, _ ->
                            _isSpeaking.value = false
                            tempFile.delete()
                            false
                        }
                        prepare()
                        start()
                        _isSpeaking.value = true
                    }
                }
            } catch (e: Exception) {
                Log.e("TtsManager", "Audio playback error, attempting PCM AudioTrack", e)
                playPcmAudio(bytes)
            }
        }
    }

    private fun playPcmAudio(pcmBytes: ByteArray) {
        try {
            val sampleRate = 24000
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                maxOf(minBufSize, pcmBytes.size),
                AudioTrack.MODE_STATIC
            )

            audioTrack.write(pcmBytes, 0, pcmBytes.size)
            audioTrack.play()
            _isSpeaking.value = true

            // Release after playback estimate
            val durationMs = (pcmBytes.size.toDouble() / (sampleRate * 2)) * 1000
            scope.launch(Dispatchers.IO) {
                kotlinx.coroutines.delay(durationMs.toLong() + 200)
                _isSpeaking.value = false
                audioTrack.release()
            }
        } catch (e: Exception) {
            Log.e("TtsManager", "PCM playback failed", e)
            _isSpeaking.value = false
        }
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null

            nativeTts?.stop()
        } catch (e: Exception) {
            Log.e("TtsManager", "Error stopping TTS", e)
        } finally {
            _isSpeaking.value = false
        }
    }

    fun destroy() {
        stop()
        nativeTts?.shutdown()
        nativeTts = null
        isTtsInitialized = false
    }
}
