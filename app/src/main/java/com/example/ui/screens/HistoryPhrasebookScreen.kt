package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TranslationEntity
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
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.TranslationViewModel

@Composable
fun HistoryPhrasebookScreen(
    translationViewModel: TranslationViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableStateOf(0) } // 0 = All History, 1 = Favorites / Phrasebook
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val allHistory by translationViewModel.allHistory.collectAsState()
    val favorites by translationViewModel.favoriteTranslations.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val syncStatus by authViewModel.syncStatus.collectAsState()

    val currentList = if (selectedTabIndex == 0) allHistory else favorites
    val filteredList = currentList.filter { item ->
        (selectedCategoryFilter == "ALL" || item.category == selectedCategoryFilter) &&
                (searchQuery.isBlank() || item.sourceText.contains(searchQuery, ignoreCase = true) || item.translatedText.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("history_screen")
    ) {
        // Header
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
                        .background(Color(0x2638BDF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = Color(0xFF38BDF8))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "السجل والمفضلة (Room & Cloud)",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = if (currentUser != null) "مزامنة سحابية نشطة (${currentUser?.displayName ?: "مستخدم"})" else "مزامنة محلية (سجل أوفلاين)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (currentUser != null) AccentGreen else Color(0xFF94A3B8)
                    )
                }
            }

            IconButton(
                onClick = { authViewModel.syncCloudData() },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(GlassSurfaceSubtle)
                    .border(BorderStroke(1.dp, GlassBorderSubtle), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.CloudSync, contentDescription = "Sync Cloud", tint = Color(0xFF38BDF8))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: History vs Favorites
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0x660F172A),
            contentColor = Color.White,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(3.dp)
                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        .background(Color(0xFF38BDF8))
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, GlassBorderSubtle), RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        "سجل الترجمات (${allHistory.size})",
                        fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTabIndex == 0) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        "المفضلة والقواعد (${favorites.size})",
                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTabIndex == 1) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث في السجل والترجمات...", color = Color(0xFF64748B)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color(0xFFE2E8F0),
                focusedContainerColor = GlassInputBackground,
                unfocusedContainerColor = GlassInputBackground,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = GlassBorderSubtle
            ),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().testTag("history_search_field")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL" to "الكل", "TEXT" to "نصوص", "SCREEN_OCR" to "شاشة", "SPEECH" to "صوت", "CHAT" to "شات").forEach { (code, label) ->
                FilterChip(
                    selected = selectedCategoryFilter == code,
                    onClick = { selectedCategoryFilter = code },
                    label = { Text(label, fontSize = 11.sp, color = if (selectedCategoryFilter == code) Color.White else Color(0xFF94A3B8)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2563EB),
                        containerColor = Color(0x331E293B)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategoryFilter == code,
                        borderColor = if (selectedCategoryFilter == code) Color(0xFF38BDF8) else Color(0x33FFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of items
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "لا توجد نتائج مطابقة للبحث" else "لا توجد عناصر محفوظة حتى الآن",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    TranslationHistoryCard(
                        item = item,
                        onToggleFavorite = { translationViewModel.toggleFavorite(item) },
                        onDelete = { translationViewModel.deleteHistoryItem(item) },
                        onPlayTts = { translationViewModel.speakText(item.translatedText, item.targetLang) },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Translation", item.translatedText))
                            Toast.makeText(context, "تم نسخ الترجمة", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TranslationHistoryCard(
    item: TranslationEntity,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onPlayTts: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = GlassCardBackground,
        border = BorderStroke(1.dp, GlassBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x3338BDF8),
                        border = BorderStroke(1.dp, Color(0x3338BDF8))
                    ) {
                        Text(
                            text = "${item.sourceLang.uppercase()} ➔ ${item.targetLang.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x33A855F7),
                        border = BorderStroke(1.dp, Color(0x33A855F7))
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFC084FC),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPlayTts, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "TTS", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF94A3B8), modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) Color(0xFFEAB308) else Color(0xFF94A3B8),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Source Text
            Text(
                text = item.sourceText,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Translated Text
            Text(
                text = item.translatedText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            if (!item.explanation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "💡 ${item.explanation}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color(0xFF60A5FA)
                )
            }
        }
    }
}

