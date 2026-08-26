package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.GlassSurfaceSubtle
import com.example.ui.viewmodel.TranslationViewModel

@Composable
fun VisualAiScreen(
    viewModel: TranslationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val visualResult by viewModel.visualResult.collectAsState()
    val isGenerating by viewModel.isGeneratingImage.collectAsState()

    var imagePrompt by remember {
        mutableStateOf("صورة توضيحية لمدينة مستقبلية ذكية مع لافتات إرشادية مترجمة بالعربية")
    }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(inputStream)
                selectedBitmap = bmp
                Toast.makeText(context, "تم اختيار الصورة للترجمة البصرية والتعديل", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "فشل قراءة الصورة", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("visual_ai_screen")
    ) {
        // Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x26F97316)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = AccentOrange)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "الترجمة البصرية وتوليد الصور بالذكاء الاصطناعي",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "مدعوم بنموذج gemini-3.1-flash-image-preview",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Image Prompt Input Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = GlassCardBackground,
            border = BorderStroke(1.dp, GlassBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "وصف المشهد أو التعديل البصري المطلوب:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF60A5FA)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = imagePrompt,
                    onValueChange = { imagePrompt = it },
                    placeholder = { Text("مثلاً: ترجم اللافتات الإنجليزية في هذه الصورة واستبدلها بالعربية...", color = Color(0xFF64748B)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFE2E8F0),
                        focusedContainerColor = GlassInputBackground,
                        unfocusedContainerColor = GlassInputBackground,
                        focusedBorderColor = Color(0xFFFB923C),
                        unfocusedBorderColor = GlassBorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons: Pick image or Generate from prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GlassBorderSubtle),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إرفاق صورة", fontSize = 12.sp, color = Color(0xFFE2E8F0))
                    }

                    Button(
                        onClick = {
                            viewModel.generateOrEditVisualScene(imagePrompt, selectedBitmap)
                        },
                        enabled = !isGenerating && imagePrompt.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        modifier = Modifier.weight(1.4f).testTag("generate_visual_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (selectedBitmap != null) "تعديل وترجمة الصورة" else "توليد المشهد", fontSize = 12.sp)
                    }
                }

                // Selected Bitmap Preview
                if (selectedBitmap != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "الصورة المختارة للتعديل:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                    ) {
                        Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Generated Visual AI Result Card
        AnimatedVisibility(visible = visualResult != null) {
            visualResult?.let { res ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = GlassCardBackground,
                    border = BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "النتيجة البصرية للذكاء الاصطناعي:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Visual Bitmap Display
                        val displayBmp = res.editedBitmap ?: res.originalBitmap
                        displayBmp?.let { bmp ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, AccentOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "AI Generated Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Translated Labels & Overlay Notes
                        if (res.translatedDescription.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0x660F172A),
                                border = BorderStroke(1.dp, GlassBorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "الترجمة والتحليل البصري:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF60A5FA)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = res.translatedDescription,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFF1F5F9)
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

