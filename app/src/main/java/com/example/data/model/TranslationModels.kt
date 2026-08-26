package com.example.data.model

import android.graphics.Bitmap

enum class ContextMode(val titleAr: String, val titleEn: String, val icon: String, val promptContext: String) {
    STANDARD(
        "عام وطبيعي",
        "General",
        "✨",
        "Provide a highly natural, fluent, and idiomatic translation adapted to everyday modern language."
    ),
    MOVIES_DIALOGUE(
        "أفلام وفيديو",
        "Movies & Cinema",
        "🎬",
        "Context: Movie / TV Show subtitles and screen dialogue. Keep translations punchy, concise, cinematic, emotionally resonant, and formatted for fast reading."
    ),
    CHAT_SOCIAL(
        "شات وتواصل",
        "Chat & Social",
        "💬",
        "Context: Messaging apps (WhatsApp, Telegram, Discord). Handle informal slang, emojis, colloquial expressions, and friendly tone."
    ),
    GAMING_HUD(
        "ألعاب وشاشات",
        "Gaming & UI",
        "🎮",
        "Context: Video game interface, lore, dialogues, inventory, quests, and HUD elements. Keep terminology gamer-accurate."
    ),
    FORMAL_BUSINESS(
        "رسمي ومهني",
        "Formal Business",
        "💼",
        "Context: Professional documents, business communication, and formal diplomatic correspondence."
    ),
    TECHNICAL_SCIENCE(
        "تقني وعلمي",
        "Technical / Science",
        "🔬",
        "Context: Software engineering, AI, mechanics, medicine, or academic literature with precise terminology."
    )
}

enum class ToneType(val titleAr: String, val titleEn: String) {
    NATURAL("طبيعي متوازن", "Natural / Fluent"),
    FORMAL("رسمي ومهذب", "Formal & Polite"),
    CASUAL("عفوي وعامي", "Casual & Colloquial"),
    SHORT_SUBTITLE("موجز ومكثف (لسترينج الشاشة)", "Concise Subtitle"),
    EXPLANATORY("مترجم مع شرح سياقي", "With Cultural Context")
}

data class TranslationResult(
    val sourceText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val contextMode: ContextMode = ContextMode.STANDARD,
    val explanation: String? = null,
    val alternatives: List<String> = emptyList(),
    val pronunciation: String? = null,
    val audioBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class LiveSubtitleLine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val startTimeMs: Long = 0,
    val endTimeMs: Long = 0,
    val originalText: String,
    val translatedText: String,
    val speaker: String? = null
)

data class ScreenOcrBlock(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val translatedText: String = "",
    val boundingBox: android.graphics.RectF? = null,
    val confidence: Float = 1.0f
)

data class GeneratedVisualResult(
    val originalBitmap: Bitmap? = null,
    val editedBitmap: Bitmap? = null,
    val imageUrl: String? = null,
    val translatedDescription: String = "",
    val prompt: String = ""
)

enum class FloatingWidgetMode {
    MINIMIZED_BUBBLE,
    EXPANDED_CARD,
    LIVE_SUBTITLES_BAR,
    SNIP_OCR_OVERLAY,
    CHAT_FLOATING_LAYER
}
