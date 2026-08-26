package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.FloatingTranslatorService
import com.example.service.ScreenCaptureService
import com.example.service.TranslateAccessibilityService
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.viewmodel.OverlayViewModel
import com.example.ui.viewmodel.TranslationViewModel

@Composable
fun ScreenTranslateScreen(
    translationViewModel: TranslationViewModel,
    overlayViewModel: OverlayViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val isFloatingServiceRunning by overlayViewModel.isFloatingServiceRunning.collectAsState()
    val isAccessibilityEnabled by overlayViewModel.isAccessibilityEnabled.collectAsState()
    val isScreenCapturing by overlayViewModel.isScreenCapturing.collectAsState()
    val ocrBlocks by translationViewModel.ocrBlocks.collectAsState()
    val isOcrRunning by translationViewModel.isOcrRunning.collectAsState()

    var sampleScreenBitmap by remember {
        mutableStateOf<Bitmap?>(createSampleScreenBitmap("RPG Game HUD / Dialogue Screen", "Attack Power: 4500\nQuest: Defeat the Shadow Dragon\nPress [X] to Open Inventory"))
    }

    // MediaProjection Launcher
    val projectionManager = remember {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    val screenCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val serviceIntent = Intent(context, ScreenCaptureService::class.java).apply {
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(ScreenCaptureService.EXTRA_DATA, result.data)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Toast.makeText(context, "تم بدء التقاط وترجمة الشاشة المباشرة", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("screen_translate_screen")
    ) {
        // Section Header
        Text(
            text = "ترجمة الشاشة الفورية وخدمات الـ Overlay",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Text(
            text = "ترجمة كل ما يظهر على الشاشة في أي تطبيق أو لعبة أو مستند دون مغادرة التطبيق.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Floating Bubble Control Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isFloatingServiceRunning) Color(0x2622C55E) else Color(0x331E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = if (isFloatingServiceRunning) AccentGreen else Color(0xFF94A3B8)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "المترجم العائم (Floating Bubble)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (isFloatingServiceRunning) "الفقاعة العائمة نشطة فوق الشاشة" else "متوقفة - انقر للتفعيل",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (isFloatingServiceRunning) AccentGreen else Color(0xFF94A3B8)
                        )
                    }
                }

                Switch(
                    checked = isFloatingServiceRunning,
                    onCheckedChange = {
                        if (!overlayViewModel.checkOverlayPermission(context)) {
                            overlayViewModel.requestOverlayPermissionIntent(context)?.let {
                                context.startActivity(it)
                            }
                            Toast.makeText(context, "يرجى منح إذن العرض فوق التطبيقات الأخرى", Toast.LENGTH_LONG).show()
                        } else {
                            overlayViewModel.toggleFloatingService(context)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2563EB)
                    ),
                    modifier = Modifier.testTag("floating_service_switch")
                )
            }
        }

        // Accessibility Service Control Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isAccessibilityEnabled) Color(0x26A855F7) else Color(0x331E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Accessibility,
                                contentDescription = null,
                                tint = if (isAccessibilityEnabled) AccentPurple else Color(0xFF94A3B8)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "خدمة قراءة الشاشة (Accessibility)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = if (isAccessibilityEnabled) "الخدمة مفعلة وجاهزة لقراءة الرسائل" else "الخدمة غير مفعلة في إعدادات النظام",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (isAccessibilityEnabled) AccentPurple else AccentOrange
                            )
                        }
                    }
                }

                if (!isAccessibilityEnabled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            context.startActivity(overlayViewModel.requestAccessibilitySettingsIntent())
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "تفعيل خدمة إمكانية الوصول في إعدادات الهاتف",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // MediaProjection Screen Capture Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isScreenCapturing) Color(0x2606B6D4) else Color(0x331E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = null,
                            tint = if (isScreenCapturing) AccentCyan else Color(0xFF94A3B8)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "التقاط الشاشة للألعاب والأفلام (MediaProjection)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (isScreenCapturing) "جاري البث المباشر لمعالجة إطارات الفيديو" else "التقاط إطارات الشاشة وترجمتها فورياً",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (isScreenCapturing) AccentCyan else Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (isScreenCapturing) {
                                val stopIntent = Intent(context, ScreenCaptureService::class.java)
                                context.stopService(stopIntent)
                            } else {
                                screenCaptureLauncher.launch(projectionManager.createScreenCaptureIntent())
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isScreenCapturing) AccentRed else Color(0xFF0284C7)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isScreenCapturing) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isScreenCapturing) "إيقاف التقاط الشاشة" else "بدء التقاط الشاشة",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Live Interactive Screen OCR Simulator Box
        Text(
            text = "معاينة وتحليل OCR المباشر للشاشة:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Preset Selector Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            sampleScreenBitmap = createSampleScreenBitmap(
                                "Game Boss Dialogue",
                                "The Dark Knight: 'You shall not pass this dungeon!'\nHP: 9800/10000 | MP: 450\nSkill: Dragon Breath (Ready)"
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("شاشة لعبة", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    }

                    OutlinedButton(
                        onClick = {
                            sampleScreenBitmap = createSampleScreenBitmap(
                                "Social Chat Message",
                                "Alex: 'Hey! Are we still meeting for dinner tonight at 8 PM?'\nSarah: 'Yes, I booked a table at the Italian place!'"
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("شات محادثة", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    }

                    OutlinedButton(
                        onClick = {
                            sampleScreenBitmap = createSampleScreenBitmap(
                                "Movie Scene Subtitle",
                                "[Cinema Mode]\n'Some journeys can only be traveled alone...'\n[Music Swells Dramatic]"
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("مشهد فيلم", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Simulated Screen Bitmap
                sampleScreenBitmap?.let { bmp ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, GlassBorderGlow, RoundedCornerShape(14.dp))
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Sample Screen Preview",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Run OCR Action
                Button(
                    onClick = {
                        sampleScreenBitmap?.let {
                            translationViewModel.processBitmapOcr(it)
                        }
                    },
                    enabled = !isOcrRunning,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier.fillMaxWidth().testTag("run_screen_ocr_button")
                ) {
                    if (isOcrRunning) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(imageVector = Icons.Default.CropFree, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مسح الشاشة واستخراج النصوص وترجمتها (ML Kit + Gemini Vision)")
                }

                // OCR Results Block List
                if (ocrBlocks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "النصوص المكتشفة والمترجمة في مكانها:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF60A5FA)
                    )

                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ocrBlocks.forEach { block ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0x660F172A),
                                border = BorderStroke(1.dp, GlassBorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "الأصل: ${block.text}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "الترجمة: ${block.translatedText}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = AccentGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun createSampleScreenBitmap(title: String, body: String): Bitmap {
    val width = 800
    val height = 450
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background
    val bgPaint = Paint().apply {
        color = AndroidColor.parseColor("#0F172A")
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Title
    val titlePaint = Paint().apply {
        color = AndroidColor.parseColor("#38BDF8")
        textSize = 34f
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText(title, 40f, 60f, titlePaint)

    // Divider
    val linePaint = Paint().apply {
        color = AndroidColor.parseColor("#334155")
        strokeWidth = 2f
    }
    canvas.drawLine(40f, 80f, (width - 40).toFloat(), 80f, linePaint)

    // Body Text
    val bodyPaint = Paint().apply {
        color = AndroidColor.parseColor("#F8FAFC")
        textSize = 26f
        isAntiAlias = true
    }

    var y = 130f
    body.lines().forEach { line ->
        canvas.drawText(line, 40f, y, bodyPaint)
        y += 40f
    }

    return bitmap
}

