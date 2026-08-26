package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import com.example.service.FloatingTranslatorService
import com.example.service.ScreenCaptureService
import com.example.service.TranslateAccessibilityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OverlayViewModel(application: Application) : AndroidViewModel(application) {

    val isFloatingServiceRunning: StateFlow<Boolean> = FloatingTranslatorService.isServiceRunning
    val isAccessibilityEnabled: StateFlow<Boolean> = TranslateAccessibilityService.isServiceEnabled
    val isScreenCapturing: StateFlow<Boolean> = ScreenCaptureService.isCapturing

    private val _floatingMode = MutableStateFlow("ALL") // "ALL", "CHAT", "SUBTITLES", "GAMING"
    val floatingMode: StateFlow<String> = _floatingMode.asStateFlow()

    private val _snipAreaSelected = MutableStateFlow(false)
    val snipAreaSelected: StateFlow<Boolean> = _snipAreaSelected.asStateFlow()

    fun checkOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun requestOverlayPermissionIntent(context: Context): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
        } else null
    }

    fun requestAccessibilitySettingsIntent(): Intent {
        return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    }

    fun toggleFloatingService(context: Context) {
        if (isFloatingServiceRunning.value) {
            FloatingTranslatorService.stop(context)
        } else {
            if (checkOverlayPermission(context)) {
                FloatingTranslatorService.start(context)
            }
        }
    }

    fun setFloatingMode(mode: String) {
        _floatingMode.value = mode
    }
}
