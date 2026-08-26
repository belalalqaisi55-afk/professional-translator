package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveSubtitleLine
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassInputBackground
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.viewmodel.TranslationViewModel

@Composable
fun MovieSubtitleScreen(
    viewModel: TranslationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val subtitlesList by viewModel.subtitlesList.collectAsState()
    val isTranslating by viewModel.isTranslatingSubtitles.collectAsState()
    val subtitleProgress by viewModel.subtitleProgress.collectAsState()

    var sampleSubtitleInput by remember {
        mutableStateOf(
            """1
00:00:01,200 --> 00:00:04,500
The universe is not bound by what we understand.

2
00:00:05,100 --> 00:00:08,800
Every star tells a story of an ancient journey across time.

3
00:00:09,200 --> 00:00:12,400
Are you ready to discover what lies beyond the horizon?""".trimIndent()
        )
    }

    var isLiveAudioSubtitlingActive by remember { mutableStateOf(false) }
    var liveAudioLines by remember {
        mutableStateOf(
            listOf(
                LiveSubtitleLine(
                    originalText = "Live Dialogue: 'Commander, the shields are holding at 40 percent.'",
                    translatedText = "[ترجمة صوتية فورية]: 'أيها القائد، الدروع صامدة عند 40 بالمئة.'"
                ),
                LiveSubtitleLine(
                    originalText = "'Prepare the quantum hyperdrive for jump sequence.'",
                    translatedText = "[ترجمة صوتية فورية]: 'جهز محرك الدفع الكمي لتسلسل القفز الفضائي.'"
                )
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("movie_subtitle_screen")
    ) {
        // Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x2606B6D4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = AccentCyan)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ترجمة الأفلام والفيديوهات المباشرة",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "التعرف على الصوت (STT) + ترجمة ملفات SRT/VTT بالسياق السينمائي",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Audio Recognition / Movie Listener Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isLiveAudioSubtitlingActive) Color(0x3306B6D4) else GlassCardBackground,
            border = BorderStroke(1.dp, if (isLiveAudioSubtitlingActive) AccentCyan else GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "التقاط وترجمة صوت الفيديو المباشر (Live Audio STT)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (isLiveAudioSubtitlingActive) "جاري الاستماع لصوت الفيلم وترجمته فورياً..." else "الاستماع لصوت مكبر الهاتف وترجمته كشريط سينمائي عائم",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (isLiveAudioSubtitlingActive) AccentCyan else Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        isLiveAudioSubtitlingActive = !isLiveAudioSubtitlingActive
                        if (isLiveAudioSubtitlingActive) {
                            Toast.makeText(context, "تم تفعيل شريط الترجمة السينمائي المباشر", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLiveAudioSubtitlingActive) AccentOrange else Color(0xFF0284C7)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isLiveAudioSubtitlingActive) Icons.Default.Pause else Icons.Default.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLiveAudioSubtitlingActive) "إيقاف التعرف الصوتي المباشر" else "بدء التقاط صوت الفيديو والترجمة الفورية",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (isLiveAudioSubtitlingActive) {
                    Spacer(modifier = Modifier.height(12.dp))
                    // Live Subtitle Overlay Simulation Bar
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xCC090D16),
                        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE CINEMA OVERLAY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AccentCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            liveAudioLines.forEach { line ->
                                Text(
                                    text = line.translatedText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = Color(0xFFFDE047),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                )
                                Text(
                                    text = line.originalText,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subtitle File (.SRT / .VTT) Batch Translator
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x26A855F7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Subtitles, contentDescription = null, tint = AccentPurple)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "مترجم ملفات الترجمة (SRT / VTT)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "ترجمة نصوص وملفات الترجمة مع الحفاظ الدقيق على التوقيت والترميز",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = sampleSubtitleInput,
                    onValueChange = { sampleSubtitleInput = it },
                    label = { Text("محتوى ملف الترجمة (SRT)", color = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFE2E8F0),
                        focusedContainerColor = GlassInputBackground,
                        unfocusedContainerColor = GlassInputBackground,
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = GlassBorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (isTranslating) {
                    LinearProgressIndicator(
                        progress = {
                            if (subtitleProgress.second > 0) subtitleProgress.first.toFloat() / subtitleProgress.second else 0f
                        },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFA855F7)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "جاري معالجة الأسطر: ${subtitleProgress.first} من ${subtitleProgress.second} بواسطة Gemini...",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Button(
                    onClick = {
                        viewModel.loadAndTranslateSubtitles(sampleSubtitleInput)
                    },
                    enabled = !isTranslating && sampleSubtitleInput.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    modifier = Modifier.fillMaxWidth().testTag("translate_subtitles_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ترجمة كامل ملف الترجمة بالسياق السينمائي")
                }

                // Subtitle Line List
                if (subtitlesList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "الأسطر المترجمة:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFA855F7)
                    )

                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        subtitlesList.forEach { line ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0x660F172A),
                                border = BorderStroke(1.dp, GlassBorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = line.originalText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = line.translatedText.ifEmpty { "جاري الترجمة..." },
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF60A5FA)
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

