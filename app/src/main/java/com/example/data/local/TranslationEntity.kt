package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val contextMode: String,
    val category: String = "TEXT", // TEXT, SCREEN_OCR, SPEECH, MOVIE_SUBTITLE, CHAT, IMAGE
    val isFavorite: Boolean = false,
    val explanation: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
