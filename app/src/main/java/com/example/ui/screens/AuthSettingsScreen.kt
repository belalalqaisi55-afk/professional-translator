package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppThemeMode
import com.example.data.model.FontSizeScale
import com.example.data.model.Language
import com.example.data.model.OfflinePackStatus
import com.example.data.model.ToneType
import com.example.ui.components.LanguagePickerButton
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassSurfaceSubtle
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.TranslationViewModel

@Composable
fun AuthSettingsScreen(
    authViewModel: AuthViewModel,
    translationViewModel: TranslationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val currentUser by authViewModel.currentUser.collectAsState()
    val isAuthenticating by authViewModel.isAuthenticating.collectAsState()
    val syncStatus by authViewModel.syncStatus.collectAsState()
    val selectedVoice by translationViewModel.ttsManager.selectedVoice.collectAsState()

    val userPrefs by translationViewModel.userPreferences.collectAsState()
    val offlinePacks by translationViewModel.offlinePacks.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("auth_settings_screen")
    ) {
        // Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x2638BDF8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color(0xFF38BDF8))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "الإعدادات والتخصيص والترجمة بدون إنترنت",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "تخصيص المظهر، حجم الخط، حزم اللغات بدون اتصال، وحساب Google",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 1: UI CUSTOMIZATION (Theme Mode + Font Size Scale)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x26EC4899)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.ColorLens, contentDescription = null, tint = Color(0xFFEC4899))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "تخصيص مظهر الواجهة (Theme & Styling)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "التبديل بين الوضع الداكن والفاتح وتعديل حجم الخط",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Theme Mode Selector
                Text(
                    text = "وضع المظهر (Theme Mode):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.values().forEach { mode ->
                        val isSelected = userPrefs.themeMode == mode
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else GlassSurfaceSubtle,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else GlassBorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    translationViewModel.updateThemeMode(mode)
                                    Toast.makeText(context, "تم تطبيق: ${mode.titleAr}", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("theme_mode_${mode.name}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = mode.icon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (mode == AppThemeMode.DARK) "داكن" else if (mode == AppThemeMode.LIGHT) "فاتح" else "تلقائي",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Font Size Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FormatSize, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "حجم الخط المخصص:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = userPrefs.fontSizeScale.labelAr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FontSizeScale.values().forEach { scale ->
                        val isSelected = userPrefs.fontSizeScale == scale
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else GlassSurfaceSubtle,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else GlassBorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    translationViewModel.updateFontSizeScale(scale)
                                }
                                .testTag("font_scale_${scale.name}")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "A",
                                    fontSize = scale.sampleSizeSp.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Preview Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GlassSurfaceSubtle,
                    border = BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "معاينة حجم الخط التفاعلية الحية:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "«المترجم العصبي الفوري بدقة واصطناع الذكاء الاصطناعي»",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // SECTION 2: OFFLINE TRANSLATION & DOWNLOADABLE LANGUAGE PACKS (English, Spanish, French, etc.)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
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
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x2610B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF10B981))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "الترجمة بدون إنترنت (Offline Mode)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "نماذج الذكاء الاصطناعي والقواميس المثبتة محلياً",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Main Offline Mode Toggle
                    Switch(
                        checked = userPrefs.isOfflineModeEnabled,
                        onCheckedChange = { enabled ->
                            translationViewModel.toggleOfflineMode(enabled)
                            val msg = if (enabled) "تم تفعيل وضع الترجمة بدون إنترنت ⚡" else "تم تفعيل وضع الترجمة السحابي ☁️"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF10B981)
                        ),
                        modifier = Modifier.testTag("offline_mode_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Downloadable Language Packs List
                Text(
                    text = "حزم اللغات القابلة للتنزيل (Downloadable Models):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                offlinePacks.forEach { pack ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = GlassSurfaceSubtle,
                        border = BorderStroke(
                            1.dp,
                            if (pack.status == OfflinePackStatus.DOWNLOADED) Color(0xFF10B981).copy(alpha = 0.4f) else GlassBorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("pack_item_${pack.code}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = pack.flag, fontSize = 24.sp, modifier = Modifier.padding(end = 10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${pack.nativeName} (${pack.name})",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0x33475569)
                                            ) {
                                                Text(
                                                    text = "${pack.sizeMb} MB",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF94A3B8),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = pack.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Action button per pack
                                when (pack.status) {
                                    OfflinePackStatus.DOWNLOADED -> {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("مثبت", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                                }
                                            }

                                            if (pack.code != "en" && pack.code != "ar") {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = {
                                                        translationViewModel.deleteOfflinePack(pack.code)
                                                        Toast.makeText(context, "تم حذف حزمة ${pack.name}", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                    OfflinePackStatus.DOWNLOADING -> {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(
                                                progress = { pack.progress },
                                                strokeWidth = 2.5.dp,
                                                color = Color(0xFF38BDF8),
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${(pack.progress * 100).toInt()}%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF38BDF8)
                                            )
                                        }
                                    }
                                    OfflinePackStatus.NOT_DOWNLOADED -> {
                                        Button(
                                            onClick = {
                                                translationViewModel.downloadOfflinePack(pack.code)
                                                Toast.makeText(context, "جاري تنزيل حزمة ${pack.name}...", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("تنزيل", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            if (pack.status == OfflinePackStatus.DOWNLOADING) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { pack.progress },
                                    color = Color(0xFF38BDF8),
                                    trackColor = Color(0x33475569),
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                }
            }
        }

        // SECTION 3: TRANSLATION PREFERENCES (Default Languages, Tone, Automation)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x268B5CF6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = AccentPurple)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "تفضيلات الترجمة الافتراضية (Preferences)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "تعيين اللغات والنبرة الافتراضية والخيارات التلقائية",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Default Target Language
                Text(
                    text = "لغة الهدف الافتراضية عند الفتح:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                LanguagePickerButton(
                    language = Language.findByCode(userPrefs.defaultTargetLang),
                    isSource = false,
                    onLanguageSelected = { selected ->
                        translationViewModel.updateDefaultLanguages(userPrefs.defaultSourceLang, selected.code)
                        Toast.makeText(context, "تم حفظ لغة الهدف: ${selected.nativeName}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Default Tone
                Text(
                    text = "نبرة الترجمة الافتراضية (Default Tone):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(ToneType.NATURAL, ToneType.FORMAL, ToneType.CASUAL, ToneType.SHORT_SUBTITLE).forEach { tone ->
                        val isSelected = userPrefs.defaultTone == tone
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else GlassSurfaceSubtle,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else GlassBorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    translationViewModel.updateDefaultTone(tone)
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tone.titleAr,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Automation Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "نطق صوتي تلقائي (Auto-TTS)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "تشغيل الصوت تلقائياً بعد إتمام الترجمة",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = userPrefs.isAutoTtsEnabled,
                        onCheckedChange = { translationViewModel.updateAutoTts(it) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "نسخ تلقائي للحافظة (Auto-Copy)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "نسخ النص المترجم تلقائياً فور ظهوره",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = userPrefs.isAutoCopyEnabled,
                        onCheckedChange = { translationViewModel.updateAutoCopy(it) }
                    )
                }
            }
        }

        // SECTION 4: GOOGLE SIGN-IN / FIREBASE CLOUD PROFILE
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (currentUser != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x332563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentUser?.displayName ?: "مستخدم Google",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentUser?.email ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { authViewModel.signOut() }) {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = "تسجيل خروج", tint = Color(0xFFEF4444))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GlassSurfaceSubtle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حالة المزامنة السحابية: $syncStatus",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x2638BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF38BDF8))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "تسجيل الدخول باستخدام Google",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "مزامنة سجل الترجمات والمفضلة مع السحابة (Firebase Firestore)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { authViewModel.signInWithGoogle() },
                        enabled = !isAuthenticating,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        modifier = Modifier.fillMaxWidth().testTag("google_signin_button")
                    ) {
                        if (isAuthenticating) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تسجيل الدخول عبر Google")
                    }
                }
            }
        }

        // SECTION 5: GEMINI TTS VOICE SELECTION
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x2606B6D4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = AccentCyan)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نبرة وأصوات الذكاء الاصطناعي (Gemini TTS)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "نموذج gemini-3.1-flash-tts-preview فائق الواقعية",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val voices = listOf(
                    "Kore" to "Kore (طبيعي وواضح)",
                    "Puck" to "Puck (شاب وعفوي)",
                    "Fenrir" to "Fenrir (عميق ورسمي)",
                    "Aoede" to "Aoede (هادئ وناعم)",
                    "Zephyr" to "Zephyr (سريع وحيوي)"
                )

                voices.forEach { (voiceId, label) ->
                    val isSelected = selectedVoice == voiceId
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0x4D2563EB) else GlassSurfaceSubtle,
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF38BDF8) else GlassBorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                translationViewModel.ttsManager.setVoice(voiceId)
                                translationViewModel.speakText("مرحباً، أنا المترجم الذكي الصوتي OmniTranslate", "ar")
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color(0xFF38BDF8) else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Active Gemini Models Info Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassSurfaceSubtle,
            border = BorderStroke(1.dp, GlassBorderSubtle),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "نماذج الذكاء الاصطناعي والترجمة المدمجة بالنظام:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF60A5FA)
                )
                Spacer(modifier = Modifier.height(8.dp))

                listOf(
                    "الترجمة النصية والسياقية" to "gemini-2.5-flash",
                    "الترجمة بدون إنترنت (Offline)" to "On-Device Neural Engine (EN, ES, FR, AR, DE...)",
                    "توليد النطق الصوتي الفائق" to "gemini-3.1-flash-tts-preview",
                    "الترجمة البصرية وتوليد الصور" to "gemini-3.1-flash-image-preview",
                    "التعرف على نصوص الشاشة" to "Google ML Kit Vision + Multimodal",
                    "قاعدة البيانات والمزامنة" to "Room DB + Firebase Firestore"
                ).forEach { (feature, model) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = feature, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = model, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
