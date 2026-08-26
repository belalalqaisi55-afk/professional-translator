package com.example.data.model

enum class OfflinePackStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED
}

data class OfflineLanguagePack(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val sizeMb: Int,
    val status: OfflinePackStatus = OfflinePackStatus.NOT_DOWNLOADED,
    val progress: Float = 0f,
    val version: String = "v2.4",
    val description: String = ""
) {
    companion object {
        val DEFAULT_PACKS = listOf(
            OfflineLanguagePack(
                code = "en",
                name = "English",
                nativeName = "English",
                flag = "🇺🇸",
                sizeMb = 38,
                status = OfflinePackStatus.DOWNLOADED,
                progress = 1f,
                description = "حزمة اللغة الإنجليزية الأساسية (القاموس العصبي + القواعد)"
            ),
            OfflineLanguagePack(
                code = "ar",
                name = "Arabic",
                nativeName = "العربية",
                flag = "🇸🇦",
                sizeMb = 42,
                status = OfflinePackStatus.DOWNLOADED,
                progress = 1f,
                description = "حزمة اللغة العربية (المفردات، الإعراب، والسياق)"
            ),
            OfflineLanguagePack(
                code = "es",
                name = "Spanish",
                nativeName = "Español",
                flag = "🇪🇸",
                sizeMb = 36,
                status = OfflinePackStatus.NOT_DOWNLOADED,
                progress = 0f,
                description = "حزمة اللغة الإسبانية الشاملة (قواعد ومحادثات سريعة)"
            ),
            OfflineLanguagePack(
                code = "fr",
                name = "French",
                nativeName = "Français",
                flag = "🇫🇷",
                sizeMb = 39,
                status = OfflinePackStatus.NOT_DOWNLOADED,
                progress = 0f,
                description = "حزمة اللغة الفرنسية الذكية (تصريف الأفعال والمفردات)"
            ),
            OfflineLanguagePack(
                code = "de",
                name = "German",
                nativeName = "Deutsch",
                flag = "🇩🇪",
                sizeMb = 44,
                status = OfflinePackStatus.NOT_DOWNLOADED,
                progress = 0f,
                description = "حزمة اللغة الألمانية الدقيقة (التركيب اللغوي والمصطلحات)"
            ),
            OfflineLanguagePack(
                code = "tr",
                name = "Turkish",
                nativeName = "Türkçe",
                flag = "🇹🇷",
                sizeMb = 35,
                status = OfflinePackStatus.NOT_DOWNLOADED,
                progress = 0f,
                description = "حزمة اللغة التركية السياقية بدون إنترنت"
            ),
            OfflineLanguagePack(
                code = "ja",
                name = "Japanese",
                nativeName = "日本語",
                flag = "🇯🇵",
                sizeMb = 50,
                status = OfflinePackStatus.NOT_DOWNLOADED,
                progress = 0f,
                description = "حزمة الكانجي والمحادثات اليابانية بدون إنترنت"
            )
        )
    }
}
