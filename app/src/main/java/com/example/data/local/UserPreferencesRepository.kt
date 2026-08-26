package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppThemeMode
import com.example.data.model.ContextMode
import com.example.data.model.FontSizeScale
import com.example.data.model.ToneType
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("omni_user_prefs", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val themeModeStr = prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
        val fontScaleStr = prefs.getString(KEY_FONT_SCALE, FontSizeScale.MEDIUM.name) ?: FontSizeScale.MEDIUM.name
        val sourceLang = prefs.getString(KEY_DEFAULT_SOURCE_LANG, "auto") ?: "auto"
        val targetLang = prefs.getString(KEY_DEFAULT_TARGET_LANG, "ar") ?: "ar"
        val toneStr = prefs.getString(KEY_DEFAULT_TONE, ToneType.NATURAL.name) ?: ToneType.NATURAL.name
        val contextModeStr = prefs.getString(KEY_DEFAULT_CONTEXT, ContextMode.STANDARD.name) ?: ContextMode.STANDARD.name
        val offlineMode = prefs.getBoolean(KEY_OFFLINE_MODE, false)
        val autoTts = prefs.getBoolean(KEY_AUTO_TTS, false)
        val autoCopy = prefs.getBoolean(KEY_AUTO_COPY, false)
        val haptic = prefs.getBoolean(KEY_HAPTIC, true)

        val themeMode = runCatching { AppThemeMode.valueOf(themeModeStr) }.getOrDefault(AppThemeMode.DARK)
        val fontScale = runCatching { FontSizeScale.valueOf(fontScaleStr) }.getOrDefault(FontSizeScale.MEDIUM)
        val tone = runCatching { ToneType.valueOf(toneStr) }.getOrDefault(ToneType.NATURAL)
        val contextMode = runCatching { ContextMode.valueOf(contextModeStr) }.getOrDefault(ContextMode.STANDARD)

        return UserPreferences(
            themeMode = themeMode,
            fontSizeScale = fontScale,
            defaultSourceLang = sourceLang,
            defaultTargetLang = targetLang,
            defaultTone = tone,
            defaultContextMode = contextMode,
            isOfflineModeEnabled = offlineMode,
            isAutoTtsEnabled = autoTts,
            isAutoCopyEnabled = autoCopy,
            isHapticFeedbackEnabled = haptic
        )
    }

    fun updateThemeMode(themeMode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, themeMode.name).apply()
        _preferences.value = _preferences.value.copy(themeMode = themeMode)
    }

    fun updateFontSizeScale(fontScale: FontSizeScale) {
        prefs.edit().putString(KEY_FONT_SCALE, fontScale.name).apply()
        _preferences.value = _preferences.value.copy(fontSizeScale = fontScale)
    }

    fun updateDefaultLanguages(source: String, target: String) {
        prefs.edit()
            .putString(KEY_DEFAULT_SOURCE_LANG, source)
            .putString(KEY_DEFAULT_TARGET_LANG, target)
            .apply()
        _preferences.value = _preferences.value.copy(defaultSourceLang = source, defaultTargetLang = target)
    }

    fun updateDefaultTone(tone: ToneType) {
        prefs.edit().putString(KEY_DEFAULT_TONE, tone.name).apply()
        _preferences.value = _preferences.value.copy(defaultTone = tone)
    }

    fun updateDefaultContextMode(mode: ContextMode) {
        prefs.edit().putString(KEY_DEFAULT_CONTEXT, mode.name).apply()
        _preferences.value = _preferences.value.copy(defaultContextMode = mode)
    }

    fun updateOfflineMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_OFFLINE_MODE, enabled).apply()
        _preferences.value = _preferences.value.copy(isOfflineModeEnabled = enabled)
    }

    fun updateAutoTts(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_TTS, enabled).apply()
        _preferences.value = _preferences.value.copy(isAutoTtsEnabled = enabled)
    }

    fun updateAutoCopy(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_COPY, enabled).apply()
        _preferences.value = _preferences.value.copy(isAutoCopyEnabled = enabled)
    }

    fun updateHaptic(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _preferences.value = _preferences.value.copy(isHapticFeedbackEnabled = enabled)
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_FONT_SCALE = "font_scale"
        private const val KEY_DEFAULT_SOURCE_LANG = "default_source_lang"
        private const val KEY_DEFAULT_TARGET_LANG = "default_target_lang"
        private const val KEY_DEFAULT_TONE = "default_tone"
        private const val KEY_DEFAULT_CONTEXT = "default_context"
        private const val KEY_OFFLINE_MODE = "offline_mode"
        private const val KEY_AUTO_TTS = "auto_tts"
        private const val KEY_AUTO_COPY = "auto_copy"
        private const val KEY_HAPTIC = "haptic"
    }
}
