package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContextMode
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderGlow
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassCardBackground
import com.example.ui.theme.GlassInputBackground
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlassSurfaceSubtle
import com.example.ui.viewmodel.TranslationViewModel

data class DemoChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val translatedText: String,
    val isIncoming: Boolean,
    val lang: String
)

@Composable
fun ChatOverlayScreen(
    viewModel: TranslationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var outgoingInput by remember { mutableStateOf("") }
    var translatedOutgoing by remember { mutableStateOf("") }

    val chatMessages = remember {
        mutableListOf(
            DemoChatMessage(
                id = "1",
                senderName = "Alex (WhatsApp)",
                text = "Hey! Can you send me the project report before 5 PM?",
                translatedText = "مرحباً! هل يمكنك إرسال تقرير المشروع إليّ قبل الساعة 5 مساءً؟",
                isIncoming = true,
                lang = "en"
            ),
            DemoChatMessage(
                id = "2",
                senderName = "أنا",
                text = "نعم بالتأكيد، التقرير جاهز وسأرسله الآن عبر البريد.",
                translatedText = "Yes absolutely, the report is ready and I will send it via email now.",
                isIncoming = false,
                lang = "ar"
            ),
            DemoChatMessage(
                id = "3",
                senderName = "Kenji (Telegram)",
                text = "プロジェクトの進捗はいかがですか？",
                translatedText = "كيف يسير تقدم المشروع؟",
                isIncoming = true,
                lang = "ja"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("chat_overlay_screen")
    ) {
        // Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x2622C55E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = AccentGreen)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ترجمة المحادثات والرسائل الفورية",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "طبقة ذكية فوق تطبيقات التواصل (واتساب، تليجرام، ماسنجر)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Feature Highlight Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "كيف تعمل الترجمة الفوقية للمحادثات؟",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF60A5FA)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. قراءة الرسائل الواردة فور ظهورها على الشاشة وترجمتها بجانبها مباشرة.\n2. كتابة الرد باللغة العربية وتحويله تلقائياً للغة المستقبل بضغطة واحدة.\n3. الحفاظ على اللهجة العفوية والإيموجي والمصطلحات الدارجة.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Live Chat Simulation Container
        Text(
            text = "محاكاة الطبقة الذكية للدردشة المباشرة:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Messages List
                chatMessages.forEach { msg ->
                    val alignment = if (msg.isIncoming) Alignment.Start else Alignment.End
                    val bubbleBg = if (msg.isIncoming) Color(0x661E293B) else Color(0x4D2563EB)
                    val bubbleBorder = if (msg.isIncoming) GlassBorderSubtle else Color(0x663B82F6)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalAlignment = alignment
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = bubbleBg,
                            border = BorderStroke(1.dp, bubbleBorder),
                            modifier = Modifier.fillMaxWidth(0.88f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.senderName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.isIncoming) Color(0xFF38BDF8) else Color(0xFF93C5FD)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // AI Translated Bubble Inlay
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0x40000000),
                                    border = BorderStroke(1.dp, GlassBorderSubtle),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "✨ ترجمة Gemini الفورية:",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF60A5FA)
                                            )
                                            Text(
                                                text = msg.translatedText,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = Color(0xFFF1F5F9)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("ChatTranslation", msg.translatedText))
                                                Toast.makeText(context, "تم نسخ الترجمة", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = Color(0xFF60A5FA),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Instant Reply & Auto-Translate Composer
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0x660F172A),
                    border = BorderStroke(1.dp, GlassBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "اكتب ردك بالعربية وترجمه فوراً للغة المحادثة:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF60A5FA)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = outgoingInput,
                                onValueChange = { outgoingInput = it },
                                placeholder = { Text("اكتب رسالة...", color = Color(0xFF64748B)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = GlassInputBackground,
                                    unfocusedContainerColor = GlassInputBackground,
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = GlassBorderSubtle
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (outgoingInput.isNotBlank()) {
                                        translatedOutgoing = "Instant AI Translation: $outgoingInput"
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("TranslatedReply", translatedOutgoing))
                                        Toast.makeText(context, "تمت ترجمة الرد ونسخه للحافظة للإرسال المباشر", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Translate, contentDescription = "Translate")
                            }
                        }

                        if (translatedOutgoing.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "جاهز للإرسال: $translatedOutgoing",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = AccentGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

