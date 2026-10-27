package com.mylittleworld.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.mylittleworld.launcher.model.FurnitureHotspotId
import com.mylittleworld.launcher.model.RoomId
import com.mylittleworld.launcher.ui.hud.LauncherHudOverlays
import com.mylittleworld.launcher.ui.rooms.BathroomScene
import com.mylittleworld.launcher.ui.rooms.BedroomScene
import com.mylittleworld.launcher.ui.rooms.EntertainmentRoomScene
import com.mylittleworld.launcher.ui.rooms.EntranceHallwayScene
import com.mylittleworld.launcher.ui.rooms.FloorPlanMapModal
import com.mylittleworld.launcher.ui.rooms.FurnitureAppPanelModal
import com.mylittleworld.launcher.ui.rooms.KitchenRoomScene
import com.mylittleworld.launcher.ui.rooms.LauncherSettingsModal
import com.mylittleworld.launcher.ui.rooms.WorkroomScene
import com.mylittleworld.launcher.ui.search.UniversalSearchSheet
import com.mylittleworld.launcher.ui.theme.MyLittleWorldTheme
import com.mylittleworld.launcher.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Default Android Home Launcher behavior: pressing Back on the home screen
        // returns to the user's Default Room (Bedroom) rather than exiting the launcher.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val homeRoom = viewModel.settings.value.defaultRoom
                if (viewModel.currentRoom.value != homeRoom) {
                    viewModel.navigateToRoom(homeRoom, instantFromMap = true)
                }
            }
        })

        setContent {
            MyLittleWorldTheme {
                MyLittleWorldCompleteApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh installed apps list in case the user installed or uninstalled an app
        viewModel.refreshInstalledApps()
    }
}

@Composable
fun MyLittleWorldCompleteApp(viewModel: LauncherViewModel) {
    val settings by viewModel.settings.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val currentRoom by viewModel.currentRoom.collectAsState()
    val girlPose by viewModel.girlPose.collectAsState()
    val catPose by viewModel.catPose.collectAsState()
    val dogPose by viewModel.dogPose.collectAsState()
    val isWalkingBetweenRooms by viewModel.isWalkingBetweenRooms.collectAsState()
    val walkProgress by viewModel.walkProgress.collectAsState()
    val walkingDestination by viewModel.walkingDestination.collectAsState()

    var activeBedroomHotspot by remember { mutableStateOf<FurnitureHotspotId?>(null) }
    var showMapModal by remember { mutableStateOf(false) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            // Swipe-Up anywhere on any room opens Universal Search
            .pointerInput(Unit) {
                var accumulatedDragY = 0f
                detectVerticalDragGestures(
                    onDragStart = { accumulatedDragY = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        accumulatedDragY += dragAmount
                        if (accumulatedDragY < -110f) {
                            showSearchSheet = true
                            accumulatedDragY = 0f
                        }
                    }
                )
            }
    ) {
        // Room Transition respects Master Animation ON/OFF setting
        val crossfadeDuration = if (settings.masterAnimationEnabled) 380 else 0

        Crossfade(
            targetState = currentRoom,
            animationSpec = tween(durationMillis = crossfadeDuration),
            label = "SixRoomTransition"
        ) { room ->
            when (room) {
                RoomId.BEDROOM -> {
                    BedroomScene(
                        masterAnimationEnabled = settings.masterAnimationEnabled,
                        themePreset = settings.themePreset,
                        showGirl = settings.showGirlCharacter,
                        showPets = settings.showPets,
                        girlPose = girlPose,
                        catPose = catPose,
                        dogPose = dogPose,
                        isWalkingToRoom = isWalkingBetweenRooms,
                        walkProgress = walkProgress,
                        customNoteText = settings.customNoteText,
                        onHotspotClick = { hotspot ->
                            if (hotspot == FurnitureHotspotId.GREEN_PORTAL) {
                                showMapModal = true
                            } else {
                                activeBedroomHotspot = hotspot
                            }
                        },
                        onGirlClick = { viewModel.cycleGirlPose() },
                        onCatClick = { viewModel.cycleCatPose() },
                        onDogClick = { viewModel.cycleDogPose() },
                        onNoteClick = { showSettingsModal = true }
                    )
                }

                RoomId.WORKROOM -> {
                    WorkroomScene(
                        themePreset = settings.themePreset,
                        showGirl = settings.showGirlCharacter,
                        showPets = settings.showPets,
                        girlPose = girlPose,
                        catPose = catPose,
                        allApps = installedApps,
                        favoritePackages = settings.favoritePackages,
                        getAppsForSubCategory = { subCat ->
                            viewModel.getAppsForWorkroomCategory(subCat, settings)
                        },
                        onToggleAppInSubCategory = { subCat, app ->
                            viewModel.toggleWorkroomSubCategoryApp(subCat, app)
                        },
                        onLaunchApp = { viewModel.launchApp(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onGirlClick = { viewModel.cycleGirlPose() },
                        onCatClick = { viewModel.cycleCatPose() },
                        onBackToBedroom = { viewModel.navigateToRoom(RoomId.BEDROOM) }
                    )
                }

                RoomId.ENTERTAINMENT -> {
                    EntertainmentRoomScene(
                        themePreset = settings.themePreset,
                        showGirl = settings.showGirlCharacter,
                        showPets = settings.showPets,
                        girlPose = girlPose,
                        catPose = catPose,
                        dogPose = dogPose,
                        allApps = installedApps,
                        favoritePackages = settings.favoritePackages,
                        getAppsForTvChannel = { channel ->
                            viewModel.getAppsForTvChannel(channel, settings)
                        },
                        onToggleAppInTvChannel = { channel, app ->
                            viewModel.toggleTvChannelApp(channel, app)
                        },
                        onLaunchApp = { viewModel.launchApp(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onGirlClick = { viewModel.cycleGirlPose() },
                        onBackToBedroom = { viewModel.navigateToRoom(RoomId.BEDROOM) }
                    )
                }

                RoomId.KITCHEN -> {
                    KitchenRoomScene(
                        masterAnimationEnabled = settings.masterAnimationEnabled,
                        themePreset = settings.themePreset,
                        showGirl = settings.showGirlCharacter,
                        showPets = settings.showPets,
                        girlPose = girlPose,
                        dogPose = dogPose,
                        cabinetApps = viewModel.getAppsForHotspot(FurnitureHotspotId.KITCHEN_CABINET, settings),
                        allApps = installedApps,
                        favoritePackages = settings.favoritePackages,
                        onToggleCabinetApp = { app ->
                            viewModel.toggleHotspotApp(FurnitureHotspotId.KITCHEN_CABINET, app)
                        },
                        onLaunchApp = { viewModel.launchApp(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onGirlClick = { viewModel.cycleGirlPose() },
                        onDogClick = { viewModel.cycleDogPose() },
                        onBackToBedroom = { viewModel.navigateToRoom(RoomId.BEDROOM) }
                    )
                }

                RoomId.BATHROOM -> {
                    BathroomScene(
                        masterAnimationEnabled = settings.masterAnimationEnabled,
                        themePreset = settings.themePreset,
                        showGirl = settings.showGirlCharacter,
                        wellnessApps = viewModel.getAppsForHotspot(FurnitureHotspotId.BATHROOM_CABINET, settings),
                        allApps = installedApps,
                        favoritePackages = settings.favoritePackages,
                        onToggleWellnessApp = { app ->
                            viewModel.toggleHotspotApp(FurnitureHotspotId.BATHROOM_CABINET, app)
                        },
                        onLaunchApp = { viewModel.launchApp(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onBackToBedroom = { viewModel.navigateToRoom(RoomId.BEDROOM) }
                    )
                }

                RoomId.ENTRANCE -> {
                    EntranceHallwayScene(
                        themePreset = settings.themePreset,
                        showGirl = settings.showGirlCharacter,
                        girlPose = girlPose,
                        mailboxApps = viewModel.getAppsForHotspot(FurnitureHotspotId.ENTRANCE_MAILBOX, settings),
                        allApps = installedApps,
                        favoritePackages = settings.favoritePackages,
                        onToggleMailboxApp = { app ->
                            viewModel.toggleHotspotApp(FurnitureHotspotId.ENTRANCE_MAILBOX, app)
                        },
                        onLaunchApp = { viewModel.launchApp(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onGirlClick = { viewModel.cycleGirlPose() },
                        onBackToBedroom = { viewModel.navigateToRoom(RoomId.BEDROOM) }
                    )
                }
            }
        }

        // HUD Overlay Layer (Clock/Weather/Tasks, Master Animation Pill, Left Dock, Bottom 6-Room Bar)
        LauncherHudOverlays(
            currentRoom = currentRoom,
            masterAnimationEnabled = settings.masterAnimationEnabled,
            focusModeEnabled = settings.focusModeEnabled,
            weatherStatusText = settings.weatherStatusText,
            dailyTasks = settings.dailyTasks,
            isWalkingTransition = isWalkingBetweenRooms,
            walkingDestination = walkingDestination,
            onToggleMasterAnimation = { viewModel.setMasterAnimation(it) },
            onSelectRoom = { viewModel.navigateToRoom(it, instantFromMap = false) },
            onHomeClick = { viewModel.navigateToRoom(settings.defaultRoom, instantFromMap = true) },
            onMapClick = { showMapModal = true },
            onSearchClick = { showSearchSheet = true },
            onFocusToggle = { viewModel.setFocusMode(!settings.focusModeEnabled) },
            onSettingsClick = { showSettingsModal = true }
        )

        // Interactive Bedroom Furniture App Panel Modal
        activeBedroomHotspot?.let { hotspot ->
            FurnitureAppPanelModal(
                hotspot = hotspot,
                assignedApps = viewModel.getAppsForHotspot(hotspot, settings),
                allApps = installedApps,
                favoritePackages = settings.favoritePackages,
                onLaunchApp = { viewModel.launchApp(it) },
                onToggleAssignApp = { viewModel.toggleHotspotApp(hotspot, it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onDismiss = { activeBedroomHotspot = null }
            )
        }

        // 3-Story Cutaway Dollhouse Floor-Plan Map Modal
        if (showMapModal) {
            FloorPlanMapModal(
                currentRoom = currentRoom,
                onSelectRoom = { viewModel.navigateToRoom(it, instantFromMap = true) },
                onDismiss = { showMapModal = false }
            )
        }

        // Swipe-Up Universal Search & All Apps Sheet
        if (showSearchSheet) {
            UniversalSearchSheet(
                allApps = installedApps,
                favoritePackages = settings.favoritePackages,
                recentPackages = settings.recentPackages,
                onLaunchApp = { viewModel.launchApp(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onJumpToRoom = { viewModel.navigateToRoom(it, instantFromMap = true) },
                onDismiss = { showSearchSheet = false }
            )
        }

        // Full Settings, Theme, Default Room & Widgets Modal
        if (showSettingsModal) {
            LauncherSettingsModal(
                settings = settings,
                onToggleAnimation = { viewModel.setMasterAnimation(it) },
                onToggleGirl = { viewModel.setShowGirl(it) },
                onTogglePets = { viewModel.setShowPets(it) },
                onToggleFocusMode = { viewModel.setFocusMode(it) },
                onSelectDefaultRoom = { viewModel.setDefaultRoom(it) },
                onSelectThemePreset = { viewModel.setThemePreset(it) },
                onUpdateNote = { viewModel.updateCustomNote(it) },
                onUpdateWeather = { viewModel.updateWeatherText(it) },
                onUpdateTasks = { viewModel.updateDailyTasks(it) },
                onDismiss = { showSettingsModal = false }
            )
        }
    }
}
