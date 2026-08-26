package com.example.data.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val ttsLocale: String = code,
    val region: String = "Global",
    val supportsOffline: Boolean = false
) {
    companion object {
        val AUTO = Language("auto", "Auto Detect", "تحديد تلقائي", "🌐", region = "Auto")

        val SUPPORTED_LANGUAGES = listOf(
            // Primary & Most Popular
            Language("ar", "Arabic", "العربية", "🇸🇦", "ar", "Middle East", supportsOffline = true),
            Language("en", "English", "English", "🇺🇸", "en-US", "Americas/Europe", supportsOffline = true),
            Language("es", "Spanish", "Español", "🇪🇸", "es-ES", "Europe/Americas", supportsOffline = true),
            Language("fr", "French", "Français", "🇫🇷", "fr-FR", "Europe/Africa", supportsOffline = true),
            Language("de", "German", "Deutsch", "🇩🇪", "de-DE", "Europe", supportsOffline = true),
            Language("it", "Italian", "Italiano", "🇮🇹", "it-IT", "Europe", supportsOffline = true),
            Language("pt", "Portuguese", "Português", "🇧🇷", "pt-BR", "Americas/Europe", supportsOffline = true),
            Language("tr", "Turkish", "Türkçe", "🇹🇷", "tr-TR", "Europe/Asia", supportsOffline = true),
            Language("ru", "Russian", "Русский", "🇷🇺", "ru-RU", "Europe/Asia", supportsOffline = true),
            Language("ja", "Japanese", "日本語", "🇯🇵", "ja-JP", "Asia", supportsOffline = true),
            Language("ko", "Korean", "한국어", "🇰🇷", "ko-KR", "Asia", supportsOffline = true),
            Language("zh", "Chinese (Simplified)", "中文 (简体)", "🇨🇳", "zh-CN", "Asia", supportsOffline = true),
            Language("hi", "Hindi", "हिन्दी", "🇮🇳", "hi-IN", "Asia", supportsOffline = true),
            Language("ur", "Urdu", "اردو", "🇵🇰", "ur-PK", "Asia", supportsOffline = true),
            Language("id", "Indonesian", "Bahasa Indonesia", "🇮🇩", "id-ID", "Asia", supportsOffline = true),
            Language("fa", "Persian", "فارسی", "🇮🇷", "fa-IR", "Middle East", supportsOffline = true),

            // 24 Additional Languages
            Language("nl", "Dutch", "Nederlands", "🇳🇱", "nl-NL", "Europe", supportsOffline = true),
            Language("pl", "Polish", "Polski", "🇵🇱", "pl-PL", "Europe", supportsOffline = true),
            Language("sv", "Swedish", "Svenska", "🇸🇪", "sv-SE", "Europe", supportsOffline = true),
            Language("no", "Norwegian", "Norsk", "🇳🇴", "no-NO", "Europe", supportsOffline = true),
            Language("da", "Danish", "Dansk", "🇩🇰", "da-DK", "Europe", supportsOffline = true),
            Language("fi", "Finnish", "Suomi", "🇫🇮", "fi-FI", "Europe", supportsOffline = true),
            Language("el", "Greek", "Ελληνικά", "🇬🇷", "el-GR", "Europe", supportsOffline = true),
            Language("he", "Hebrew", "עברית", "🇮🇱", "he-IL", "Middle East", supportsOffline = true),
            Language("th", "Thai", "ไทย", "🇹🇭", "th-TH", "Asia", supportsOffline = true),
            Language("vi", "Vietnamese", "Tiếng Việt", "🇻🇳", "vi-VN", "Asia", supportsOffline = true),
            Language("ms", "Malay", "Bahasa Melayu", "🇲🇾", "ms-MY", "Asia", supportsOffline = true),
            Language("tl", "Filipino / Tagalog", "Tagalog", "🇵🇭", "fil-PH", "Asia", supportsOffline = true),
            Language("cs", "Czech", "Čeština", "🇨🇿", "cs-CZ", "Europe", supportsOffline = true),
            Language("ro", "Romanian", "Română", "🇷🇴", "ro-RO", "Europe", supportsOffline = true),
            Language("hu", "Hungarian", "Magyar", "🇭🇺", "hu-HU", "Europe", supportsOffline = true),
            Language("uk", "Ukrainian", "Українська", "🇺🇦", "uk-UA", "Europe", supportsOffline = true),
            Language("bn", "Bengali", "বাংলা", "🇧🇩", "bn-BD", "Asia", supportsOffline = true),
            Language("sw", "Swahili", "Kiswahili", "🇰🇪", "sw-KE", "Africa", supportsOffline = true),
            Language("bg", "Bulgarian", "Български", "🇧🇬", "bg-BG", "Europe", supportsOffline = true),
            Language("sk", "Slovak", "Slovenčina", "🇸🇰", "sk-SK", "Europe", supportsOffline = true),
            Language("hr", "Croatian", "Hrvatski", "🇭🇷", "hr-HR", "Europe", supportsOffline = true),
            Language("sr", "Serbian", "Српски", "🇷🇸", "sr-RS", "Europe", supportsOffline = true),
            Language("yue", "Cantonese", "粵語", "🇭🇰", "zh-HK", "Asia", supportsOffline = true),
            Language("la", "Latin", "Latina", "🏛️", "la", "Classic", supportsOffline = true)
        )

        fun findByCode(code: String): Language {
            return SUPPORTED_LANGUAGES.find { it.code.equals(code, ignoreCase = true) }
                ?: Language(code, code.uppercase(), code.uppercase(), "🏳️")
        }

        fun searchLanguages(query: String, isSource: Boolean = false): List<Language> {
            val baseList = if (isSource) listOf(AUTO) + SUPPORTED_LANGUAGES else SUPPORTED_LANGUAGES
            if (query.isBlank()) return baseList

            val q = query.trim().lowercase()
            return baseList.filter {
                it.name.lowercase().contains(q) ||
                it.nativeName.lowercase().contains(q) ||
                it.code.lowercase().contains(q) ||
                it.region.lowercase().contains(q)
            }
        }
    }
}
