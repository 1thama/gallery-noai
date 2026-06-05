package com.tama.gallerynoai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.tama.gallerynoai.data.model.MediaItem
import com.tama.gallerynoai.data.repository.MediaRepository
import com.tama.gallerynoai.data.settings.SettingsManager
import com.tama.gallerynoai.ui.navigation.NavRoutes
import com.tama.gallerynoai.ui.screens.*
import com.tama.gallerynoai.ui.theme.GalleryTheme
import com.tama.gallerynoai.ui.viewmodel.GalleryViewModel
import com.tama.gallerynoai.ui.viewmodel.GalleryViewModelFactory
import com.tama.gallerynoai.ui.viewmodel.SettingsViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            galleryViewModel.loadMedia()
        }
    }

    private val restoreLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            galleryViewModel.loadMedia()
            galleryViewModel.loadTrashedMedia()
        }
    }

    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    private val commonLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            galleryViewModel.onMoveConfirmed()
            galleryViewModel.loadMedia()
            galleryViewModel.clearSelection()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        val settingsManager = SettingsManager(this)
        val database = com.tama.gallerynoai.data.local.db.MediaDatabase.getDatabase(this)
        val repository = MediaRepository(this, database)
        val factory = GalleryViewModelFactory(repository, settingsManager)
        
        galleryViewModel = ViewModelProvider(this, factory)[GalleryViewModel::class.java]
        settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        
        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()
            val fontFamily by settingsViewModel.fontFamily.collectAsStateWithLifecycle()
            val accentColorInt by settingsViewModel.accentColor.collectAsStateWithLifecycle()
            val secondaryColorInt by settingsViewModel.secondaryColor.collectAsStateWithLifecycle()
            val tertiaryColorInt by settingsViewModel.tertiaryColor.collectAsStateWithLifecycle()
            
            val darkTheme = when (themeMode) {
                "Light" -> false
                "Dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            GalleryTheme(
                darkTheme = darkTheme,
                dynamicColor = true,
                fontFamilyName = fontFamily,
                accentColor = Color(accentColorInt),
                secondaryColor = Color(secondaryColorInt),
                tertiaryColor = Color(tertiaryColorInt)
            ) {
                val navController = rememberNavController()
                
                // Handle incoming intent
                LaunchedEffect(intent) {
                    handleIntent(intent, navController)
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                val showBottomBar = currentDestination?.route in listOf(
                    NavRoutes.PHOTOS, 
                    NavRoutes.SEARCH, 
                    NavRoutes.ALBUMS, 
                    NavRoutes.SETTINGS,
                    NavRoutes.ALBUM_DETAIL,
                    NavRoutes.TRASH,
                    NavRoutes.QUICK_ACCESS
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(
                                navController = navController, 
                                currentDestination = currentDestination,
                                galleryViewModel = galleryViewModel,
                                onTabReselected = { screen ->
                                    if (navController.currentDestination?.route != screen.route) {
                                        // Clear selection and search when popping back to root
                                        galleryViewModel.setSelectionMode(false)
                                        galleryViewModel.setAlbumSelectionMode(false)
                                        galleryViewModel.onSearchQueryChange("")
                                        
                                        // If we are in a sub-route (like search or album_detail), pop back to the tab's root
                                        navController.popBackStack(screen.route, inclusive = false)
                                    } else {
                                        // If we are already at the root, trigger scroll to top
                                        when (screen) {
                                            Screen.Photos -> galleryViewModel.triggerScrollToTop(screen.route)
                                            Screen.Search -> galleryViewModel.triggerScrollToTop(screen.route)
                                            Screen.Albums -> galleryViewModel.triggerScrollToTop(screen.route)
                                            Screen.Settings -> settingsViewModel.triggerScrollToTop()
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    com.tama.gallerynoai.ui.navigation.AppNavHost(
                        navController = navController,
                        galleryViewModel = galleryViewModel,
                        settingsViewModel = settingsViewModel,
                        commonLauncher = commonLauncher,
                        restoreLauncher = restoreLauncher,
                        modifier = androidx.compose.ui.Modifier.padding(innerPadding),
                        activity = this
                    )
                }
            }
        }

        checkAndRequestPermissions()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIntent(intent: Intent?, navController: androidx.navigation.NavHostController) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val uri = intent.data ?: return
            val mimeType = intent.type ?: contentResolver.getType(uri) ?: "image/*"
            val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
            navController.navigate(com.tama.gallerynoai.ui.navigation.NavRoutes.externalDetail(encodedUri, mimeType)) {
                popUpTo(com.tama.gallerynoai.ui.navigation.NavRoutes.PHOTOS) { inclusive = false }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasAllPermissions()) {
            galleryViewModel.loadMedia(silent = true)
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = getRequiredPermissions()

        if (hasAllPermissions()) {
            galleryViewModel.loadMedia()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun hasAllPermissions(): Boolean {
        return getRequiredPermissions().all { 
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Photos : Screen("photos", "Photos", Icons.Default.Photo)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Albums : Screen("albums", "Albums", Icons.Default.Collections)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun BottomNavigationBar(
    navController: androidx.navigation.NavHostController, 
    currentDestination: androidx.navigation.NavDestination?,
    galleryViewModel: GalleryViewModel,
    onTabReselected: (Screen) -> Unit
) {
    val items = listOf(Screen.Photos, Screen.Albums, Screen.Settings)
    val previousRoute = remember(currentDestination) {
        navController.previousBackStackEntry?.destination?.route
    }

    val isSearchOwnedByAlbums = currentDestination?.route == NavRoutes.SEARCH && 
        previousRoute in listOf(
            NavRoutes.ALBUMS, 
            NavRoutes.ALBUM_DETAIL, 
            NavRoutes.QUICK_ACCESS
        )

    NavigationBar {
        items.forEach { screen ->
            val isSelected = when (screen) {
                Screen.Photos -> (currentDestination?.route in listOf(NavRoutes.PHOTOS, NavRoutes.DETAIL)) || 
                                (currentDestination?.route == NavRoutes.SEARCH && !isSearchOwnedByAlbums)
                Screen.Search -> currentDestination?.route == NavRoutes.SEARCH
                Screen.Albums -> (currentDestination?.route in listOf(
                    NavRoutes.ALBUMS,
                    NavRoutes.ALBUM_DETAIL,
                    NavRoutes.TRASH,
                    NavRoutes.ALBUM_ITEM_DETAIL,
                    NavRoutes.QUICK_ACCESS,
                    NavRoutes.QUICK_ACCESS_DETAIL
                )) || (currentDestination?.route == NavRoutes.SEARCH && isSearchOwnedByAlbums)
                Screen.Settings -> currentDestination?.route == NavRoutes.SETTINGS
            }
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.label) },
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        onTabReselected(screen)
                    } else {
                        // Clear selection when moving to a new tab
                        galleryViewModel.setSelectionMode(false)
                        galleryViewModel.setAlbumSelectionMode(false)
                        galleryViewModel.onSearchQueryChange("")

                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

