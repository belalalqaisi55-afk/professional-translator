package com.example.data.model

enum class AppThemeMode(val titleAr: String, val titleEn: String, val icon: String) {
    DARK("داكن زجاجي (Frosted Dark)", "Dark Glass", "🌙"),
    LIGHT("فاتح كريستالي (Crystal Light)", "Light Crystal", "☀️"),
    SYSTEM("تلقائي (حسب النظام)", "System Auto", "⚙️")
}

enum class FontSizeScale(val scaleFactor: Float, val labelAr: String, val sampleSizeSp: Int) {
    SMALL(0.85f, "صغير (85%)", 13),
    MEDIUM(1.0f, "متوسط (قياسي)", 15),
    LARGE(1.15f, "كبير (115%)", 17),
    EXTRA_LARGE(1.30f, "كبير جداً (130%)", 19)
}

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val fontSizeScale: FontSizeScale = FontSizeScale.MEDIUM,
    val defaultSourceLang: String = "auto",
    val defaultTargetLang: String = "ar",
    val defaultTone: ToneType = ToneType.NATURAL,
    val defaultContextMode: ContextMode = ContextMode.STANDARD,
    val isOfflineModeEnabled: Boolean = false,
    val isAutoTtsEnabled: Boolean = false,
    val isAutoCopyEnabled: Boolean = false,
    val isHapticFeedbackEnabled: Boolean = true
)
