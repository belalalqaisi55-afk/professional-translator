package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.OfflinePackManager
import com.example.data.local.OfflineTranslationEngine
import com.example.data.local.TranslationEntity
import com.example.data.local.UserPreferencesRepository
import com.example.data.model.AppThemeMode
import com.example.data.model.ContextMode
import com.example.data.model.FontSizeScale
import com.example.data.model.GeneratedVisualResult
import com.example.data.model.Language
import com.example.data.model.LiveSubtitleLine
import com.example.data.model.OfflineLanguagePack
import com.example.data.model.ScreenOcrBlock
import com.example.data.model.ToneType
import com.example.data.model.TranslationResult
import com.example.data.model.UserPreferences
import com.example.data.remote.FirebaseRepository
import com.example.data.remote.GeminiApiService
import com.example.ocr.OcrManager
import com.example.speech.SpeechManager
import com.example.speech.TtsManager
import com.example.subtitle.SubtitleParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TranslationViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiApiService = GeminiApiService()
    private val database = AppDatabase.getDatabase(application)
    private val translationDao = database.translationDao()
    private val firebaseRepository = FirebaseRepository(application)

    // User Preferences & Offline Engine
    val preferencesRepository = UserPreferencesRepository(application)
    val offlinePackManager = OfflinePackManager(application)
    val offlineTranslationEngine = OfflineTranslationEngine()

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.preferences
    val offlinePacks: StateFlow<List<OfflineLanguagePack>> = offlinePackManager.packs

    val speechManager = SpeechManager(application)
    val ttsManager = TtsManager(application, geminiApiService)
    val ocrManager = OcrManager(application, geminiApiService)
    val subtitleParser = SubtitleParser(geminiApiService)

    // Language selection
    private val _sourceLanguage = MutableStateFlow(
        if (preferencesRepository.preferences.value.defaultSourceLang == "auto") Language.AUTO
        else Language.findByCode(preferencesRepository.preferences.value.defaultSourceLang)
    )
    val sourceLanguage: StateFlow<Language> = _sourceLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow(
        Language.findByCode(preferencesRepository.preferences.value.defaultTargetLang)
    )
    val targetLanguage: StateFlow<Language> = _targetLanguage.asStateFlow()

    // Context & Tone
    private val _contextMode = MutableStateFlow(preferencesRepository.preferences.value.defaultContextMode)
    val contextMode: StateFlow<ContextMode> = _contextMode.asStateFlow()

    private val _selectedTone = MutableStateFlow(preferencesRepository.preferences.value.defaultTone)
    val selectedTone: StateFlow<ToneType> = _selectedTone.asStateFlow()

    // Translation UI States
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _currentResult = MutableStateFlow<TranslationResult?>(null)
    val currentResult: StateFlow<TranslationResult?> = _currentResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Subtitle Workspace
    private val _subtitlesList = MutableStateFlow<List<LiveSubtitleLine>>(emptyList())
    val subtitlesList: StateFlow<List<LiveSubtitleLine>> = _subtitlesList.asStateFlow()

    private val _isTranslatingSubtitles = MutableStateFlow(false)
    val isTranslatingSubtitles: StateFlow<Boolean> = _isTranslatingSubtitles.asStateFlow()

    private val _subtitleProgress = MutableStateFlow(Pair(0, 0))
    val subtitleProgress: StateFlow<Pair<Int, Int>> = _subtitleProgress.asStateFlow()

    // Visual Translation & Image Generation / Edit Workspace
    private val _visualResult = MutableStateFlow<GeneratedVisualResult?>(null)
    val visualResult: StateFlow<GeneratedVisualResult?> = _visualResult.asStateFlow()

    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    // OCR Screen Blocks
    private val _ocrBlocks = MutableStateFlow<List<ScreenOcrBlock>>(emptyList())
    val ocrBlocks: StateFlow<List<ScreenOcrBlock>> = _ocrBlocks.asStateFlow()

    private val _isOcrRunning = MutableStateFlow(false)
    val isOcrRunning: StateFlow<Boolean> = _isOcrRunning.asStateFlow()

    // Room Database Flows
    val allHistory: StateFlow<List<TranslationEntity>> = translationDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTranslations: StateFlow<List<TranslationEntity>> = translationDao.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSourceLanguage(lang: Language) {
        _sourceLanguage.value = lang
    }

    fun setTargetLanguage(lang: Language) {
        _targetLanguage.value = lang
    }

    fun swapLanguages() {
        if (_sourceLanguage.value.code != "auto") {
            val prevSource = _sourceLanguage.value
            _sourceLanguage.value = _targetLanguage.value
            _targetLanguage.value = prevSource
        }
    }

    fun setContextMode(mode: ContextMode) {
        _contextMode.value = mode
    }

    fun setSelectedTone(tone: ToneType) {
        _selectedTone.value = tone
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun toggleOfflineMode(enabled: Boolean) {
        preferencesRepository.updateOfflineMode(enabled)
    }

    fun downloadOfflinePack(code: String) {
        offlinePackManager.downloadPack(code)
    }

    fun deleteOfflinePack(code: String) {
        offlinePackManager.deletePack(code)
    }

    fun updateThemeMode(mode: AppThemeMode) {
        preferencesRepository.updateThemeMode(mode)
    }

    fun updateFontSizeScale(scale: FontSizeScale) {
        preferencesRepository.updateFontSizeScale(scale)
    }

    fun updateDefaultLanguages(source: String, target: String) {
        preferencesRepository.updateDefaultLanguages(source, target)
        _sourceLanguage.value = if (source == "auto") Language.AUTO else Language.findByCode(source)
        _targetLanguage.value = Language.findByCode(target)
    }

    fun updateDefaultTone(tone: ToneType) {
        preferencesRepository.updateDefaultTone(tone)
        _selectedTone.value = tone
    }

    fun updateDefaultContextMode(mode: ContextMode) {
        preferencesRepository.updateDefaultContextMode(mode)
        _contextMode.value = mode
    }

    fun updateAutoTts(enabled: Boolean) {
        preferencesRepository.updateAutoTts(enabled)
    }

    fun updateAutoCopy(enabled: Boolean) {
        preferencesRepository.updateAutoCopy(enabled)
    }

    fun translateCurrentText(category: String = "TEXT") {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return

        _isLoading.value = true
        _errorMessage.value = null

        val isOffline = userPreferences.value.isOfflineModeEnabled

        viewModelScope.launch {
            try {
                if (isOffline) {
                    // Check if target language pack is downloaded
                    val targetCode = _targetLanguage.value.code
                    val isPackReady = offlinePackManager.isPackDownloaded(targetCode)

                    val offlineResult = offlineTranslationEngine.translateOffline(
                        text = text,
                        sourceLang = _sourceLanguage.value.code,
                        targetLang = targetCode,
                        contextMode = _contextMode.value,
                        tone = _selectedTone.value
                    )

                    _currentResult.value = offlineResult

                    // Save to Room Database
                    val entity = TranslationEntity(
                        sourceText = offlineResult.sourceText,
                        translatedText = offlineResult.translatedText,
                        sourceLang = offlineResult.sourceLang,
                        targetLang = offlineResult.targetLang,
                        contextMode = offlineResult.contextMode.name,
                        category = if (category == "TEXT") "OFFLINE_TEXT" else category,
                        explanation = if (isPackReady) "نموذج الترجمة بدون إنترنت (مكتمل)" else "نموذج الترجمة بدون إنترنت (قاموس أساسي)"
                    )
                    val insertedId = translationDao.insert(entity)
                    firebaseRepository.saveTranslationToFirestore(entity.copy(id = insertedId))

                    if (userPreferences.value.isAutoTtsEnabled) {
                        speakText(offlineResult.translatedText, offlineResult.targetLang)
                    }
                } else {
                    val result = geminiApiService.translateText(
                        text = text,
                        sourceLang = _sourceLanguage.value.code,
                        targetLang = _targetLanguage.value.code,
                        contextMode = _contextMode.value,
                        tone = _selectedTone.value
                    )

                    val translation = result.getOrNull()
                    if (translation != null) {
                        _currentResult.value = translation
                        
                        // Save to Room Database
                        val entity = TranslationEntity(
                            sourceText = translation.sourceText,
                            translatedText = translation.translatedText,
                            sourceLang = translation.sourceLang,
                            targetLang = translation.targetLang,
                            contextMode = translation.contextMode.name,
                            category = category,
                            explanation = translation.explanation
                        )
                        val insertedId = translationDao.insert(entity)
                        firebaseRepository.saveTranslationToFirestore(entity.copy(id = insertedId))

                        if (userPreferences.value.isAutoTtsEnabled) {
                            speakText(translation.translatedText, translation.targetLang)
                        }
                    } else {
                        // Fallback to offline engine seamlessly if cloud call failed
                        val fallback = offlineTranslationEngine.translateOffline(
                            text = text,
                            sourceLang = _sourceLanguage.value.code,
                            targetLang = _targetLanguage.value.code,
                            contextMode = _contextMode.value,
                            tone = _selectedTone.value
                        )
                        _currentResult.value = fallback
                    }
                }
            } catch (e: Exception) {
                // Fallback to offline engine on exception
                val fallback = offlineTranslationEngine.translateOffline(
                    text = text,
                    sourceLang = _sourceLanguage.value.code,
                    targetLang = _targetLanguage.value.code,
                    contextMode = _contextMode.value,
                    tone = _selectedTone.value
                )
                _currentResult.value = fallback
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(entity: TranslationEntity) {
        viewModelScope.launch {
            val updated = entity.copy(isFavorite = !entity.isFavorite)
            translationDao.update(updated)
            firebaseRepository.saveTranslationToFirestore(updated)
        }
    }

    fun deleteHistoryItem(entity: TranslationEntity) {
        viewModelScope.launch {
            translationDao.delete(entity)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            translationDao.clearNonFavorites()
        }
    }

    /**
     * Text-To-Speech with gemini-3.1-flash-tts-preview
     */
    fun speakText(text: String, lang: String? = null) {
        val targetLangCode = lang ?: _targetLanguage.value.code
        ttsManager.speak(text, targetLangCode, preferGeminiTts = !userPreferences.value.isOfflineModeEnabled)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    /**
     * Generates or Edits Image with gemini-3.1-flash-image-preview
     */
    fun generateOrEditVisualScene(prompt: String, inputBitmap: Bitmap? = null) {
        if (prompt.isBlank()) return
        _isGeneratingImage.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val result = geminiApiService.generateOrEditImage(
                    prompt = prompt,
                    inputBitmap = inputBitmap,
                    targetLang = _targetLanguage.value.code
                )
                _visualResult.value = result.getOrNull()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isGeneratingImage.value = false
            }
        }
    }

    /**
     * Runs OCR on a bitmap and translates detected blocks
     */
    fun processBitmapOcr(bitmap: Bitmap) {
        _isOcrRunning.value = true
        viewModelScope.launch {
            try {
                val mlBlocks = ocrManager.extractTextWithMlKit(bitmap).getOrDefault(emptyList())
                val translatedBlocks = if (mlBlocks.isNotEmpty()) {
                    ocrManager.translateBlocks(mlBlocks, _targetLanguage.value.code)
                } else {
                    geminiApiService.analyzeScreenImage(bitmap, _targetLanguage.value.code).getOrDefault(emptyList())
                }
                _ocrBlocks.value = translatedBlocks

                if (translatedBlocks.isNotEmpty()) {
                    val fullSource = translatedBlocks.joinToString("\n") { it.text }
                    val fullTranslated = translatedBlocks.joinToString("\n") { it.translatedText }
                    val entity = TranslationEntity(
                        sourceText = fullSource,
                        translatedText = fullTranslated,
                        sourceLang = "auto",
                        targetLang = _targetLanguage.value.code,
                        contextMode = _contextMode.value.name,
                        category = "SCREEN_OCR"
                    )
                    translationDao.insert(entity)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isOcrRunning.value = false
            }
        }
    }

    /**
     * Loads Subtitle File content and starts batch translation
     */
    fun loadAndTranslateSubtitles(rawContent: String) {
        val parsed = subtitleParser.parseSubtitleContent(rawContent)
        _subtitlesList.value = parsed

        if (parsed.isNotEmpty()) {
            _isTranslatingSubtitles.value = true
            _subtitleProgress.value = Pair(0, parsed.size)

            viewModelScope.launch {
                try {
                    val translated = subtitleParser.translateSubtitles(
                        subtitles = parsed,
                        targetLang = _targetLanguage.value.code,
                        onProgress = { curr, total ->
                            _subtitleProgress.value = Pair(curr, total)
                        }
                    )
                    _subtitlesList.value = translated
                } catch (e: Exception) {
                    _errorMessage.value = e.message
                } finally {
                    _isTranslatingSubtitles.value = false
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.destroy()
        speechManager.stopListening()
    }
}

