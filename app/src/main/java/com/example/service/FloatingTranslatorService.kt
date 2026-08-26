package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.OmniTranslateApp
import com.example.R
import com.example.data.model.ContextMode
import com.example.data.remote.GeminiApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FloatingTranslatorService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingBubbleView: View? = null
    private var floatingCardView: View? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private val geminiApiService = GeminiApiService()

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val NOTIFICATION_ID = 1001

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        private val _floatingTranslatedText = MutableStateFlow("")
        val floatingTranslatedText: StateFlow<String> = _floatingTranslatedText.asStateFlow()

        fun start(context: Context) {
            val intent = Intent(context, FloatingTranslatorService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, FloatingTranslatorService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopFloatingOverlay()
                stopForeground(true)
                stopSelf()
                _isServiceRunning.value = false
            }
            ACTION_START, null -> {
                startForeground(NOTIFICATION_ID, buildForegroundNotification())
                showFloatingBubble()
                _isServiceRunning.value = true
            }
        }
        return START_STICKY
    }

    private fun buildForegroundNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, OmniTranslateApp.CHANNEL_FLOATING_SERVICE)
            .setContentTitle("المترجم العائم الذكي نشط")
            .setContentText("اضغط على الفقاعة العائمة للترجمة الفورية لأي تطبيق أو شاشة")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun showFloatingBubble() {
        if (floatingBubbleView != null) return

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        // Create programmatic floating bubble container with modern glass styling
        val bubbleFrame = FrameLayout(this).apply {
            val sizePx = (56 * resources.displayMetrics.density).toInt()
            layoutParams = FrameLayout.LayoutParams(sizePx, sizePx)
            setBackgroundColor(0x00000000)

            val innerCircle = ImageView(context).apply {
                val pad = (6 * resources.displayMetrics.density).toInt()
                setPadding(pad, pad, pad, pad)
                setImageResource(android.R.drawable.ic_menu_search)
                setBackgroundResource(android.R.drawable.dialog_holo_dark_frame)
            }
            addView(innerCircle)
        }

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isClick = false

        bubbleFrame.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isClick = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        isClick = false
                    }
                    params.x = initialX + dx
                    params.y = initialY + dy
                    windowManager?.updateViewLayout(bubbleFrame, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick) {
                        triggerQuickScreenTranslation()
                    }
                    true
                }
                else -> false
            }
        }

        try {
            windowManager?.addView(bubbleFrame, params)
            floatingBubbleView = bubbleFrame
        } catch (e: Exception) {
            Log.e("FloatingTranslator", "Cannot add overlay window", e)
        }
    }

    private fun triggerQuickScreenTranslation() {
        scope.launch(Dispatchers.Main) {
            val accessibility = TranslateAccessibilityService.instance
            val texts = accessibility?.extractAllVisibleScreenTexts() ?: emptyList()

            if (texts.isNotEmpty()) {
                val combined = texts.joinToString("\n") { it.text }
                _floatingTranslatedText.value = "جاري الترجمة بالذكاء الاصطناعي..."
                val result = geminiApiService.translateText(
                    text = combined.take(500),
                    sourceLang = "auto",
                    targetLang = "ar",
                    contextMode = ContextMode.CHAT_SOCIAL
                )
                _floatingTranslatedText.value = result.getOrNull()?.translatedText ?: "تمت القراءة بنجاح"
            } else {
                _floatingTranslatedText.value = "انقر فوق نص لترجمته أو افتح شات/لعبة لقراءة المحتوى"
            }
        }
    }

    private fun stopFloatingOverlay() {
        try {
            floatingBubbleView?.let { windowManager?.removeView(it) }
            floatingBubbleView = null
            floatingCardView?.let { windowManager?.removeView(it) }
            floatingCardView = null
        } catch (e: Exception) {
            Log.e("FloatingTranslator", "Error removing overlay", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopFloatingOverlay()
        _isServiceRunning.value = false
    }
}
