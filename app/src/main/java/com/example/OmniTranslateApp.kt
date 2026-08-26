package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class OmniTranslateApp : Application() {

    companion object {
        const val CHANNEL_FLOATING_SERVICE = "channel_floating_service"
        const val CHANNEL_SCREEN_CAPTURE = "channel_screen_capture"
        const val CHANNEL_SUBTITLES = "channel_subtitles"
        
        lateinit var instance: OmniTranslateApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val floatingChannel = NotificationChannel(
                CHANNEL_FLOATING_SERVICE,
                "Floating Translator Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the floating translation overlay active"
            }

            val screenCaptureChannel = NotificationChannel(
                CHANNEL_SCREEN_CAPTURE,
                "Screen Live Translation Capture",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Captures screen content for instant OCR translation"
            }

            val subtitlesChannel = NotificationChannel(
                CHANNEL_SUBTITLES,
                "Live Video Subtitles Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Provides live translated subtitles over playing media"
            }

            notificationManager.createNotificationChannels(
                listOf(floatingChannel, screenCaptureChannel, subtitlesChannel)
            )
        }
    }
}
