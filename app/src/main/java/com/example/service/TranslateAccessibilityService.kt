package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class CapturedScreenText(
    val packageName: String,
    val text: String,
    val isEditable: Boolean = false,
    val viewId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class TranslateAccessibilityService : AccessibilityService() {

    companion object {
        var instance: TranslateAccessibilityService? = null
            private set

        private val _isServiceEnabled = MutableStateFlow(false)
        val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()

        private val _capturedTexts = MutableSharedFlow<List<CapturedScreenText>>(replay = 1)
        val capturedTexts: SharedFlow<List<CapturedScreenText>> = _capturedTexts.asSharedFlow()

        private val _lastFocusedText = MutableStateFlow<String?>("")
        val lastFocusedText: StateFlow<String?> = _lastFocusedText.asStateFlow()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceEnabled.value = true

        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_VIEW_FOCUSED or
                    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val rootNode = rootInActiveWindow ?: return
        val pkg = event.packageName?.toString() ?: ""

        // Skip our own app package to avoid self-loop
        if (pkg == packageName) return

        val focused = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            ?: rootNode.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)

        if (focused?.text != null && focused.text.isNotBlank()) {
            _lastFocusedText.value = focused.text.toString()
        }
    }

    /**
     * Reads all visible text elements on screen from current active window
     */
    fun extractAllVisibleScreenTexts(): List<CapturedScreenText> {
        val rootNode = rootInActiveWindow ?: return emptyList()
        val pkg = rootNode.packageName?.toString() ?: "unknown"
        val results = mutableListOf<CapturedScreenText>()

        fun traverse(node: AccessibilityNodeInfo?) {
            if (node == null) return

            val nodeText = node.text?.toString() ?: node.contentDescription?.toString()
            if (!nodeText.isNullOrBlank() && nodeText.length > 1) {
                results.add(
                    CapturedScreenText(
                        packageName = pkg,
                        text = nodeText.trim(),
                        isEditable = node.isEditable,
                        viewId = node.viewIdResourceName
                    )
                )
            }

            for (i in 0 until node.childCount) {
                traverse(node.getChild(i))
            }
        }

        traverse(rootNode)
        return results
    }

    override fun onInterrupt() {
        Log.w("TranslateAccessibility", "Service interrupted")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        _isServiceEnabled.value = false
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        _isServiceEnabled.value = false
    }
}
