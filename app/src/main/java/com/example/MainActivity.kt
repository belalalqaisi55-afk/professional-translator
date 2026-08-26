package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AuthSettingsScreen
import com.example.ui.screens.ChatOverlayScreen
import com.example.ui.screens.HistoryPhrasebookScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MovieSubtitleScreen
import com.example.ui.screens.ScreenTranslateScreen
import com.example.ui.screens.VisualAiScreen
import com.example.ui.theme.OmniTranslateTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.OverlayViewModel
import com.example.ui.viewmodel.TranslationViewModel

enum class AppDestination(
    val route: String,
    val titleAr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
    SCREEN("screen", "الشاشة", Icons.Filled.Layers, Icons.Outlined.Layers),
    CINEMA("cinema", "الأفلام", Icons.Filled.Movie, Icons.Outlined.Movie),
    CHAT("chat", "المحادثات", Icons.Filled.Chat, Icons.Outlined.Chat),
    VISUAL("visual", "بصري", Icons.Filled.Palette, Icons.Outlined.Palette),
    HISTORY("history", "السجل", Icons.Filled.History, Icons.Outlined.History),
    SETTINGS("settings", "الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {

    private val translationViewModel: TranslationViewModel by viewModels()
    private val overlayViewModel: OverlayViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userPrefs by translationViewModel.userPreferences.collectAsStateWithLifecycle()
            OmniTranslateTheme(
                themeMode = userPrefs.themeMode,
                fontSizeScale = userPrefs.fontSizeScale
            ) {
                MainAppContent(
                    translationViewModel = translationViewModel,
                    overlayViewModel = overlayViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun MainAppContent(
    translationViewModel: TranslationViewModel,
    overlayViewModel: OverlayViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }

    // Runtime Permission Request for Microphone & Notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                color = com.example.ui.theme.GlassCardBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.GlassBorderSubtle),
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBar(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    AppDestination.values().forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.titleAr,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = destination.titleAr,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.example.ui.theme.BrandPrimaryDark,
                                selectedTextColor = com.example.ui.theme.BrandPrimaryDark,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                indicatorColor = androidx.compose.ui.graphics.Color(0x333B82F6)
                            ),
                            modifier = Modifier.testTag("nav_item_${destination.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "destination_crossfade"
            ) { target ->
                when (target) {
                    AppDestination.HOME -> HomeScreen(
                        viewModel = translationViewModel,
                        onNavigateToScreenTranslate = { currentDestination = AppDestination.SCREEN },
                        onNavigateToMovieSubtitles = { currentDestination = AppDestination.CINEMA },
                        onNavigateToChatOverlay = { currentDestination = AppDestination.CHAT },
                        onNavigateToVisualAi = { currentDestination = AppDestination.VISUAL }
                    )
                    AppDestination.SCREEN -> ScreenTranslateScreen(
                        translationViewModel = translationViewModel,
                        overlayViewModel = overlayViewModel
                    )
                    AppDestination.CINEMA -> MovieSubtitleScreen(
                        viewModel = translationViewModel
                    )
                    AppDestination.CHAT -> ChatOverlayScreen(
                        viewModel = translationViewModel
                    )
                    AppDestination.VISUAL -> VisualAiScreen(
                        viewModel = translationViewModel
                    )
                    AppDestination.HISTORY -> HistoryPhrasebookScreen(
                        translationViewModel = translationViewModel,
                        authViewModel = authViewModel
                    )
                    AppDestination.SETTINGS -> AuthSettingsScreen(
                        authViewModel = authViewModel,
                        translationViewModel = translationViewModel
                    )
                }
            }
        }
    }
}
