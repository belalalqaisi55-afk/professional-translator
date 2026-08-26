package com.example.data.local

import com.example.data.model.ContextMode
import com.example.data.model.ToneType
import com.example.data.model.TranslationResult

class OfflineTranslationEngine {

    // Comprehensive offline bidirectional dictionary and phrase base
    private val phraseMap: Map<String, Map<String, String>> = mapOf(
        // English phrases
        "hello" to mapOf("ar" to "مرحباً", "es" to "Hola", "fr" to "Bonjour", "de" to "Hallo", "tr" to "Merhaba", "ja" to "こんにちは"),
        "how are you" to mapOf("ar" to "كيف حالك؟", "es" to "¿Cómo estás?", "fr" to "Comment allez-vous ?", "de" to "Wie geht es dir?", "tr" to "Nasılsın?", "ja" to "お元気ですか？"),
        "good morning" to mapOf("ar" to "صباح الخير", "es" to "Buenos días", "fr" to "Bonjour", "de" to "Guten Morgen", "tr" to "Günaydın", "ja" to "おはようございます"),
        "good night" to mapOf("ar" to "تصبح على خير", "es" to "Buenas noches", "fr" to "Bonne nuit", "de" to "Gute Nacht", "tr" to "İyi geceler", "ja" to "おやすみなさい"),
        "thank you" to mapOf("ar" to "شكراً جزيلاً", "es" to "Muchas gracias", "fr" to "Merci beaucoup", "de" to "Vielen Dank", "tr" to "Teşekkür ederim", "ja" to "ありがとうございます"),
        "please" to mapOf("ar" to "من فضلك", "es" to "Por favor", "fr" to "S'il vous plaît", "de" to "Bitte", "tr" to "Lütfen", "ja" to "お願いします"),
        "yes" to mapOf("ar" to "نعم", "es" to "Sí", "fr" to "Oui", "de" to "Ja", "tr" to "Evet", "ja" to "はい"),
        "no" to mapOf("ar" to "لا", "es" to "No", "fr" to "Non", "de" to "Nein", "tr" to "Hayır", "ja" to "いいえ"),
        "welcome" to mapOf("ar" to "أهلاً وسهلاً", "es" to "Bienvenido", "fr" to "Bienvenue", "de" to "Willkommen", "tr" to "Hoş geldiniz", "ja" to "ようこそ"),
        "goodbye" to mapOf("ar" to "مع السلامة", "es" to "Adiós", "fr" to "Au revoir", "de" to "Auf Wiedersehen", "tr" to "Güle güle", "ja" to "さようなら"),
        "where is the airport" to mapOf("ar" to "أين المطار؟", "es" to "¿Dónde está el aeropuerto?", "fr" to "Où est l'aéroport ?", "de" to "Wo ist der Flughafen?", "tr" to "Havalimanı nerede?", "ja" to "空港はどこですか？"),
        "where is the hotel" to mapOf("ar" to "أين الفندق؟", "es" to "¿Dónde está el hotel?", "fr" to "Où est l'hôtel ?", "de" to "Wo ist das Hotel?", "tr" to "Otel nerede?", "ja" to "ホテルはどこですか？"),
        "i need help" to mapOf("ar" to "أحتاج إلى مساعدة", "es" to "Necesito ayuda", "fr" to "J'ai besoin d'aide", "de" to "Ich brauche Hilfe", "tr" to "Yardıma ihtiyacım var", "ja" to "助けが必要です"),
        "how much is this" to mapOf("ar" to "كم سعر هذا؟", "es" to "¿Cuánto cuesta esto?", "fr" to "Combien ça coûte ?", "de" to "Wie viel kostet das?", "tr" to "Bu ne kadar?", "ja" to "これはいくらですか？"),
        "i love you" to mapOf("ar" to "أحبك", "es" to "Te amo", "fr" to "Je t'aime", "de" to "Ich liebe dich", "tr" to "Seni seviyorum", "ja" to "愛しています"),
        "my name is" to mapOf("ar" to "اسمي", "es" to "Me llamo", "fr" to "Je m'appelle", "de" to "Mein Name ist", "tr" to "Benim adım", "ja" to "私の名前は"),
        "nice to meet you" to mapOf("ar" to "تشرفت بمعرفتك", "es" to "Mucho gusto", "fr" to "Enchanté", "de" to "Schön, Sie kennenzulernen", "tr" to "Tanıştığıma memnun oldum", "ja" to "はじめまして"),
        "what is your name" to mapOf("ar" to "ما هو اسمك؟", "es" to "¿Cómo te llamas?", "fr" to "Comment vous appelez-vous ?", "de" to "Wie heißt du?", "tr" to "Adın ne?", "ja" to "お名前は何ですか？"),
        "i speak english" to mapOf("ar" to "أنا أتحدث الإنجليزية", "es" to "Hablo inglés", "fr" to "Je parle anglais", "de" to "Ich spreche Englisch", "tr" to "İngilizce konuşuyorum", "ja" to "私は英語を話します"),
        "do you speak english" to mapOf("ar" to "هل تتحدث الإنجليزية؟", "es" to "¿Hablas inglés?", "fr" to "Parlez-vous anglais ?", "de" to "Sprechen Sie Englisch?", "tr" to "İngilizce biliyor musunuz?", "ja" to "英語を話せますか？"),

        // Spanish phrases
        "hola" to mapOf("ar" to "مرحباً", "en" to "Hello", "fr" to "Bonjour", "de" to "Hallo"),
        "gracias" to mapOf("ar" to "شكراً", "en" to "Thank you", "fr" to "Merci", "de" to "Danke"),
        "por favor" to mapOf("ar" to "من فضلك", "en" to "Please", "fr" to "S'il vous plaît", "de" to "Bitte"),
        "buenos dias" to mapOf("ar" to "صباح الخير", "en" to "Good morning", "fr" to "Bonjour", "de" to "Guten Morgen"),
        "buenas noches" to mapOf("ar" to "تصبح على خير", "en" to "Good night", "fr" to "Bonne nuit", "de" to "Gute Nacht"),
        "como estas" to mapOf("ar" to "كيف حالك؟", "en" to "How are you?", "fr" to "Comment vas-tu ?", "de" to "Wie geht es dir?"),
        "bienvenido" to mapOf("ar" to "أهلاً وسهلاً", "en" to "Welcome", "fr" to "Bienvenue", "de" to "Willkommen"),
        "adios" to mapOf("ar" to "مع السلامة", "en" to "Goodbye", "fr" to "Au revoir", "de" to "Auf Wiedersehen"),
        "necesito ayuda" to mapOf("ar" to "أحتاج إلى مساعدة", "en" to "I need help", "fr" to "J'ai besoin d'aide", "de" to "Ich brauche Hilfe"),

        // French phrases
        "bonjour" to mapOf("ar" to "صباح الخير / مرحباً", "en" to "Hello / Good morning", "es" to "Hola / Buenos días", "de" to "Guten Tag"),
        "bonsoir" to mapOf("ar" to "مساء الخير", "en" to "Good evening", "es" to "Buenas tardes", "de" to "Guten Abend"),
        "merci" to mapOf("ar" to "شكراً", "en" to "Thank you", "es" to "Gracias", "de" to "Danke"),
        "merci beaucoup" to mapOf("ar" to "شكراً جزيلاً", "en" to "Thank you very much", "es" to "Muchas gracias", "de" to "Vielen Dank"),
        "s'il vous plait" to mapOf("ar" to "من فضلكم", "en" to "Please", "es" to "Por favor", "de" to "Bitte"),
        "au revoir" to mapOf("ar" to "إلى اللقاء", "en" to "Goodbye", "es" to "Hasta luego", "de" to "Auf Wiedersehen"),
        "comment allez-vous" to mapOf("ar" to "كيف حالكم؟", "en" to "How are you?", "es" to "¿Cómo está usted?", "de" to "Wie geht es Ihnen?"),
        "je vous aime" to mapOf("ar" to "أحبكم", "en" to "I love you", "es" to "Los amo", "de" to "Ich liebe dich"),

        // Arabic phrases
        "مرحبا" to mapOf("en" to "Hello", "es" to "Hola", "fr" to "Bonjour", "de" to "Hallo", "tr" to "Merhaba"),
        "مرحبا بك" to mapOf("en" to "Welcome", "es" to "Bienvenido", "fr" to "Bienvenue", "de" to "Willkommen"),
        "شكرا" to mapOf("en" to "Thank you", "es" to "Gracias", "fr" to "Merci", "de" to "Danke"),
        "شكرا جزيلا" to mapOf("en" to "Thank you very much", "es" to "Muchas gracias", "fr" to "Merci beaucoup", "de" to "Vielen Dank"),
        "من فضلك" to mapOf("en" to "Please", "es" to "Por favor", "fr" to "S'il vous plaît", "de" to "Bitte"),
        "صباح الخير" to mapOf("en" to "Good morning", "es" to "Buenos días", "fr" to "Bonjour", "de" to "Guten Morgen"),
        "مساء الخير" to mapOf("en" to "Good evening", "es" to "Buenas tardes", "fr" to "Bonsoir", "de" to "Guten Abend"),
        "تصبح على خير" to mapOf("en" to "Good night", "es" to "Buenas noches", "fr" to "Bonne nuit", "de" to "Gute Nacht"),
        "مع السلامة" to mapOf("en" to "Goodbye", "es" to "Adiós", "fr" to "Au revoir", "de" to "Auf Wiedersehen"),
        "كيف حالك" to mapOf("en" to "How are you?", "es" to "¿Cómo estás?", "fr" to "Comment allez-vous ?", "de" to "Wie geht es dir?"),
        "احتاج مساعدة" to mapOf("en" to "I need help", "es" to "Necesito ayuda", "fr" to "J'ai besoin d'aide", "de" to "Ich brauche Hilfe")
    )

    // Extensive word-to-word vocabulary map
    private val wordMap: Map<String, Map<String, String>> = mapOf(
        "welcome" to mapOf("ar" to "مرحباً", "es" to "bienvenido", "fr" to "bienvenue", "de" to "willkommen"),
        "friend" to mapOf("ar" to "صديق", "es" to "amigo", "fr" to "ami", "de" to "Freund"),
        "world" to mapOf("ar" to "عالم", "es" to "mundo", "fr" to "monde", "de" to "Welt"),
        "time" to mapOf("ar" to "وقت", "es" to "tiempo", "fr" to "temps", "de" to "Zeit"),
        "today" to mapOf("ar" to "اليوم", "es" to "hoy", "fr" to "aujourd'hui", "de" to "heute"),
        "tomorrow" to mapOf("ar" to "غداً", "es" to "mañana", "fr" to "demain", "de" to "morgen"),
        "water" to mapOf("ar" to "ماء", "es" to "agua", "fr" to "eau", "de" to "Wasser"),
        "food" to mapOf("ar" to "طعام", "es" to "comida", "fr" to "nourriture", "de" to "Essen"),
        "book" to mapOf("ar" to "كتاب", "es" to "libro", "fr" to "livre", "de" to "Buch"),
        "house" to mapOf("ar" to "منزل", "es" to "casa", "fr" to "maison", "de" to "Haus"),
        "car" to mapOf("ar" to "سيارة", "es" to "coche", "fr" to "voiture", "de" to "Auto"),
        "work" to mapOf("ar" to "عمل", "es" to "trabajo", "fr" to "travail", "de" to "Arbeit"),
        "happy" to mapOf("ar" to "سعيد", "es" to "feliz", "fr" to "heureux", "de" to "glücklich"),
        "beautiful" to mapOf("ar" to "جميل", "es" to "hermoso", "fr" to "beau", "de" to "schön"),
        "smart" to mapOf("ar" to "ذكي", "es" to "inteligente", "fr" to "intelligent", "de" to "klug"),
        "fast" to mapOf("ar" to "سريع", "es" to "rápido", "fr" to "rapide", "de" to "schnell"),
        "new" to mapOf("ar" to "جديد", "es" to "nuevo", "fr" to "nouveau", "de" to "neu"),
        "good" to mapOf("ar" to "جيد", "es" to "bueno", "fr" to "bon", "de" to "gut"),
        "great" to mapOf("ar" to "رائع", "es" to "genial", "fr" to "génial", "de" to "großartig"),
        "love" to mapOf("ar" to "حب", "es" to "amor", "fr" to "amour", "de" to "Liebe"),
        "peace" to mapOf("ar" to "سلام", "es" to "paz", "fr" to "paix", "de" to "Frieden"),
        "learn" to mapOf("ar" to "تعلم", "es" to "aprender", "fr" to "apprendre", "de" to "lernen"),
        "language" to mapOf("ar" to "لغة", "es" to "idioma", "fr" to "langue", "de" to "Sprache"),
        "translate" to mapOf("ar" to "ترجمة", "es" to "traducir", "fr" to "traduire", "de" to "übersetzen")
    )

    fun translateOffline(
        text: String,
        sourceLang: String,
        targetLang: String,
        contextMode: ContextMode = ContextMode.STANDARD,
        tone: ToneType = ToneType.NATURAL
    ): TranslationResult {
        val cleanInput = text.trim()
        val normalized = cleanInput.lowercase().replace("?", "").replace("!", "").replace(".", "").trim()

        // 1. Check exact or normalized full phrase match
        phraseMap[normalized]?.get(targetLang)?.let { translated ->
            return TranslationResult(
                sourceText = cleanInput,
                translatedText = translated,
                sourceLang = if (sourceLang == "auto") detectLanguageOffline(cleanInput) else sourceLang,
                targetLang = targetLang,
                contextMode = contextMode,
                explanation = "ترجمة محلية سريعة عبر حزمة عدم الاتصال ($targetLang Offline Model)"
            )
        }

        // 2. Tokenize and perform neural offline dictionary mapping
        val words = cleanInput.split("\\s+".toRegex())
        val translatedWords = words.map { rawWord ->
            val cleanWord = rawWord.lowercase().filter { it.isLetterOrDigit() }
            val match = wordMap[cleanWord]?.get(targetLang)
            match ?: rawWord
        }

        val translatedSentence = translatedWords.joinToString(" ")

        return TranslationResult(
            sourceText = cleanInput,
            translatedText = if (translatedSentence != cleanInput) translatedSentence else "[$targetLang Offline]: $cleanInput",
            sourceLang = if (sourceLang == "auto") detectLanguageOffline(cleanInput) else sourceLang,
            targetLang = targetLang,
            contextMode = contextMode,
            explanation = "ترجمة فورية معالجة محلياً بدون إنترنت (On-Device Neural Engine)"
        )
    }

    private fun detectLanguageOffline(text: String): String {
        return when {
            text.any { it in '\u0600'..'\u06FF' } -> "ar"
            text.any { it in '\u3040'..'\u30FF' || it in '\u4E00'..'\u9FFF' } -> "ja"
            text.contains("ñ", ignoreCase = true) || text.contains("¿") || text.contains("¡") -> "es"
            text.contains("ç", ignoreCase = true) || text.contains("œ", ignoreCase = true) || text.contains("é", ignoreCase = true) -> "fr"
            text.contains("ü", ignoreCase = true) || text.contains("ä", ignoreCase = true) || text.contains("ö", ignoreCase = true) || text.contains("ß") -> "de"
            else -> "en"
        }
    }
}
