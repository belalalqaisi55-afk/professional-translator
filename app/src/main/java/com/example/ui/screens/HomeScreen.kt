package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ContextModeChipRow
import com.example.ui.components.LanguageSelectorBar
import com.example.ui.components.MicPulseWave
import com.example.ui.components.ToneSelectorRow
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassHeroGradientEnd
import com.example.ui.theme.GlassHeroGradientStart
import com.example.ui.theme.GlassInputBackground
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.viewmodel.TranslationViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: TranslationViewModel,
    onNavigateToScreenTranslate: () -> Unit,
    onNavigateToMovieSubtitles: () -> Unit,
    onNavigateToChatOverlay: () -> Unit,
    onNavigateToVisualAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val sourceLang by viewModel.sourceLanguage.collectAsState()
    val targetLang by viewModel.targetLanguage.collectAsState()
    val contextMode by viewModel.contextMode.collectAsState()
    val selectedTone by viewModel.selectedTone.collectAsState()

    val inputText by viewModel.inputText.collectAsState()
    val currentResult by viewModel.currentResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isListening by viewModel.speechManager.isListening.collectAsState()
    val recognizedText by viewModel.speechManager.recognizedText.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val selectedVoice by viewModel.ttsManager.selectedVoice.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()

    // Sync recognized speech into input text
    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotBlank()) {
            viewModel.setInputText(recognizedText)
            viewModel.translateCurrentText(category = "SPEECH")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
            .testTag("home_screen_container")
    ) {
        // Frosted Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OmniTrans",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x263B82F6),
                        border = BorderStroke(1.dp, Color(0x4D60A5FA))
                    ) {
                        Text(
                            text = "PRO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF60A5FA),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "AI TRANSLATION ENGINE V2.4",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Online / Offline Indicator Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (userPrefs.isOfflineModeEnabled) Color(0x33F59E0B) else Color(0x3310B981),
                border = BorderStroke(1.dp, if (userPrefs.isOfflineModeEnabled) Color(0xFFF59E0B) else Color(0xFF10B981)),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        val newMode = !userPrefs.isOfflineModeEnabled
                        viewModel.toggleOfflineMode(newMode)
                        val msg = if (newMode) "تم تفعيل الترجمة بدون إنترنت (Offline Mode) ⚡" else "تم تفعيل وضع السحابة AI (Online Mode) ☁️"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    .testTag("network_mode_toggle_header")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (userPrefs.isOfflineModeEnabled) Color(0xFFF59E0B) else AccentGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (userPrefs.isOfflineModeEnabled) "Offline" else "Online",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (userPrefs.isOfflineModeEnabled) Color(0xFFFCD34D) else Color(0xFF6EE7B7)
                    )
                }
            }
        }

        // Offline Mode Notice Banner (Animated)
        AnimatedVisibility(visible = userPrefs.isOfflineModeEnabled) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0x33F59E0B),
                border = BorderStroke(1.dp, Color(0x66F59E0B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⚡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "وضع عدم الاتصال مفعل: تتم الترجمة باستخدام النماذج والقواميس المحلية (EN, ES, FR, AR)",
                        fontSize = 11.sp,
                        color = Color(0xFFFDE68A),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Frosted Hero Glass Active Card
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0x66161D2B),
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(GlassHeroGradientStart, GlassHeroGradientEnd)
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GLOBAL OVERLAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = Color(0xFF93C5FD)
                            )
                            Text(
                                text = "Translation Active",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        // Switch style pill
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF2563EB),
                            modifier = Modifier
                                .width(50.dp)
                                .height(26.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterEnd,
                                modifier = Modifier.padding(horizontal = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x33000000),
                        border = BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x333B82F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .border(2.dp, Color(0xFF60A5FA), RoundedCornerShape(2.dp))
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Gemini Flash 2.5 is monitoring active context & OCR...",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }

        // Language Bar
        LanguageSelectorBar(
            sourceLang = sourceLang,
            targetLang = targetLang,
            onSourceLangSelected = { viewModel.setSourceLanguage(it) },
            onTargetLangSelected = { viewModel.setTargetLanguage(it) },
            onSwap = { viewModel.swapLanguages() }
        )

        // Context Mode Selector
        Text(
            text = "سياق الترجمة الذكية:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp).padding(top = 4.dp)
        )
        ContextModeChipRow(
            selectedMode = contextMode,
            onModeSelected = { viewModel.setContextMode(it) }
        )

        // Tone Selector
        ToneSelectorRow(
            selectedTone = selectedTone,
            onToneSelected = { viewModel.setSelectedTone(it) }
        )

        // Input Frosted Card
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("input_text_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "النص المصدر (${sourceLang.nativeName})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF60A5FA)
                    )

                    if (inputText.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.setInputText("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Text",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.setInputText(it) },
                    placeholder = {
                        Text(
                            text = "اكتب أو تحدث، أو الصق نصوص الأفلام والمحادثات هنا...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("source_text_input")
                )

                // Audio / Voice Input & Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Voice Mic Button with Glass Sheen
                    Box(contentAlignment = Alignment.Center) {
                        MicPulseWave(isListening = isListening)
                        IconButton(
                            onClick = {
                                if (isListening) {
                                    viewModel.speechManager.stopListening()
                                } else {
                                    viewModel.speechManager.startListening(
                                        languageCode = if (sourceLang.code == "auto") "en-US" else sourceLang.ttsLocale
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isListening) AccentOrange else Color(0x333B82F6)
                                )
                                .border(
                                    1.dp,
                                    if (isListening) Color(0xFFF97316) else GlassBorderGlow,
                                    CircleShape
                                )
                                .testTag("mic_input_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Speech Recognition",
                                tint = if (isListening) Color.White else Color(0xFF93C5FD)
                            )
                        }
                    }

                    if (isListening) {
                        Text(
                            text = "جاري الاستماع...",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AccentOrange,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Translate Action Button with Glass Blue Gradient
                    Button(
                        onClick = { viewModel.translateCurrentText() },
                        enabled = inputText.isNotBlank() && !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        ),
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("translate_action_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ترجمة ذكية",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Result Card with Frosted Output Sheen
        AnimatedVisibility(
            visible = currentResult != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            currentResult?.let { result ->
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0x99131924),
                    border = BorderStroke(1.dp, GlassBorderGlow),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("translation_result_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = targetLang.flag,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = "الترجمة الفورية (${targetLang.nativeName})",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF60A5FA)
                                )
                            }

                            // Actions: TTS, Copy, Share
                            Row {
                                // Gemini TTS Button
                                IconButton(
                                    onClick = {
                                        if (isSpeaking) {
                                            viewModel.stopSpeaking()
                                        } else {
                                            viewModel.speakText(result.translatedText, targetLang.code)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x331E293B))
                                        .border(1.dp, GlassBorderSubtle, CircleShape)
                                        .testTag("tts_play_button")
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                        contentDescription = "Gemini TTS",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Copy Button
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Translation", result.translatedText))
                                        Toast.makeText(context, "تم نسخ الترجمة", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x331E293B))
                                        .border(1.dp, GlassBorderSubtle, CircleShape)
                                        .testTag("copy_translation_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Translation",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Share Button
                                IconButton(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, result.translatedText)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة الترجمة"))
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x331E293B))
                                        .border(1.dp, GlassBorderSubtle, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Translated Output Text
                        Text(
                            text = result.translatedText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 19.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 28.sp
                            ),
                            color = Color(0xFFF8FAFC)
                        )

                        // Pronunciation if available
                        if (!result.pronunciation.isNullOrBlank()) {
                            Text(
                                text = "🗣️ ${result.pronunciation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }

                        // Cultural / Context Explanation Box
                        if (!result.explanation.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0x4D0F172A),
                                border = BorderStroke(1.dp, GlassBorderSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "💡", fontSize = 14.sp, modifier = Modifier.padding(end = 6.dp))
                                    Text(
                                        text = result.explanation,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }

                        // Alternative formulations chips
                        if (result.alternatives.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "صياغات واقتراحات بديلة:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                result.alternatives.forEach { alt ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0x331E293B),
                                        border = BorderStroke(1.dp, GlassBorderSubtle),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Alternative", alt))
                                                Toast.makeText(context, "تم نسخ الصياغة", Toast.LENGTH_SHORT).show()
                                            }
                                    ) {
                                        Text(
                                            text = alt,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = Color(0xFFE2E8F0),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Access 2x2 Grid Hub with Frosted Glass Tiles & Glowing Badges
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "أنظمة الترجمة المتطورة المدعومة:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickFeatureCard(
                title = "ترجمة الشاشة والألعاب",
                subtitle = "Screen OCR • Visual",
                icon = "🔍",
                badgeBg = Color(0x2606B6D4),
                iconColor = AccentCyan,
                onClick = onNavigateToScreenTranslate,
                modifier = Modifier.weight(1f)
            )
            QuickFeatureCard(
                title = "ترجمة الأفلام والفيديو",
                subtitle = "Movie STT • Subtitles",
                icon = "🎬",
                badgeBg = Color(0x26F97316),
                iconColor = AccentOrange,
                onClick = onNavigateToMovieSubtitles,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickFeatureCard(
                title = "ترجمة الشات والمحادثات",
                subtitle = "Chat Layer • Instant",
                icon = "💬",
                badgeBg = Color(0x26A855F7),
                iconColor = AccentPurple,
                onClick = onNavigateToChatOverlay,
                modifier = Modifier.weight(1f)
            )
            QuickFeatureCard(
                title = "الترجمة البصرية والصور",
                subtitle = "Live Audio • Visual AI",
                icon = "🎙️",
                badgeBg = Color(0x2622C55E),
                iconColor = AccentGreen,
                onClick = onNavigateToVisualAi,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickFeatureCard(
    title: String,
    subtitle: String,
    icon: String,
    badgeBg: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0x800F172A),
        border = BorderStroke(1.dp, GlassBorderSubtle),
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = subtitle.uppercase(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF64748B),
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

