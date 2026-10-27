package com.mylittleworld.launcher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mylittleworld.launcher.data.AppDiscoveryRepository
import com.mylittleworld.launcher.data.SettingsDataStore
import com.mylittleworld.launcher.model.AppCategory
import com.mylittleworld.launcher.model.CatPoseState
import com.mylittleworld.launcher.model.DogPoseState
import com.mylittleworld.launcher.model.FurnitureHotspotId
import com.mylittleworld.launcher.model.GirlPoseState
import com.mylittleworld.launcher.model.HouseThemePreset
import com.mylittleworld.launcher.model.InstalledApp
import com.mylittleworld.launcher.model.LauncherSettings
import com.mylittleworld.launcher.model.RoomId
import com.mylittleworld.launcher.model.TvChannelCategory
import com.mylittleworld.launcher.model.WorkroomSubCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppDiscoveryRepository(application.applicationContext)
    private val settingsDataStore = SettingsDataStore(application.applicationContext)

    val settings: StateFlow<LauncherSettings> = settingsDataStore.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LauncherSettings()
    )

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    private val _currentRoom = MutableStateFlow(RoomId.BEDROOM)
    val currentRoom: StateFlow<RoomId> = _currentRoom.asStateFlow()

    private val _girlPose = MutableStateFlow(GirlPoseState.LAPTOP_ON_BED)
    val girlPose: StateFlow<GirlPoseState> = _girlPose.asStateFlow()

    private val _catPose = MutableStateFlow(CatPoseState.SLEEP)
    val catPose: StateFlow<CatPoseState> = _catPose.asStateFlow()

    private val _dogPose = MutableStateFlow(DogPoseState.SIT)
    val dogPose: StateFlow<DogPoseState> = _dogPose.asStateFlow()

    // Physical Room-Walking Choreography State
    private val _isWalkingBetweenRooms = MutableStateFlow(false)
    val isWalkingBetweenRooms: StateFlow<Boolean> = _isWalkingBetweenRooms.asStateFlow()

    private val _walkProgress = MutableStateFlow(0f)
    val walkProgress: StateFlow<Float> = _walkProgress.asStateFlow()

    private val _walkingDestination = MutableStateFlow<RoomId?>(null)
    val walkingDestination: StateFlow<RoomId?> = _walkingDestination.asStateFlow()

    private var walkJob: Job? = null

    init {
        refreshInstalledApps()
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            _installedApps.value = appRepository.loadInstalledApps()
        }
    }

    /**
     * Navigates to a target room.
     * - If instantFromMap == true OR Master Animation is OFF: switches immediately (0ms delay).
     * - If Master Animation is ON and navigating via the bottom bar: the girl physically walks
     *   toward the doorway before entering the connected room and assuming its default activity pose.
     */
    fun navigateToRoom(targetRoom: RoomId, instantFromMap: Boolean = false) {
        if (targetRoom == _currentRoom.value && !_isWalkingBetweenRooms.value) return

        walkJob?.cancel()
        val animEnabled = settings.value.masterAnimationEnabled

        if (!animEnabled || instantFromMap) {
            _isWalkingBetweenRooms.value = false
            _walkProgress.value = 0f
            _walkingDestination.value = null
            _currentRoom.value = targetRoom
            _girlPose.value = targetRoom.defaultGirlPose
            return
        }

        // Physical walking sequence when Master Animation is ON
        walkJob = viewModelScope.launch {
            _walkingDestination.value = targetRoom
            _isWalkingBetweenRooms.value = true
            _girlPose.value = GirlPoseState.WALKING

            val steps = 8
            for (step in 1..steps) {
                _walkProgress.value = step / steps.toFloat()
                delay(32L)
            }

            _currentRoom.value = targetRoom
            _girlPose.value = targetRoom.defaultGirlPose
            _isWalkingBetweenRooms.value = false
            _walkProgress.value = 0f
            _walkingDestination.value = null
        }
    }

    fun cycleGirlPose() {
        val poses = GirlPoseState.entries
        val nextIndex = (_girlPose.value.ordinal + 1) % poses.size
        _girlPose.value = poses[nextIndex]
    }

    fun cycleCatPose() {
        val poses = CatPoseState.entries
        val nextIndex = (_catPose.value.ordinal + 1) % poses.size
        _catPose.value = poses[nextIndex]
    }

    fun cycleDogPose() {
        val poses = DogPoseState.entries
        val nextIndex = (_dogPose.value.ordinal + 1) % poses.size
        _dogPose.value = poses[nextIndex]
    }

    fun launchApp(app: InstalledApp) {
        if (appRepository.launchApp(app)) {
            viewModelScope.launch {
                settingsDataStore.recordRecentLaunch(app.packageName)
            }
        }
    }

    /**
     * Resolves apps for any FurnitureHotspotId (Bedroom hotspots, Kitchen Cabinet, Bathroom Cabinet, Entrance Mailbox).
     */
    fun getAppsForHotspot(hotspot: FurnitureHotspotId, currentSettings: LauncherSettings): List<InstalledApp> {
        val all = _installedApps.value
        val customSet = currentSettings.customCollectionPackages[hotspot.name]
        if (customSet != null) {
            return all.filter { it.packageName in customSet }
        }

        val matches = all.filter { app ->
            hotspot.defaultPackageHints.any { hint ->
                app.packageName.contains(hint, ignoreCase = true)
            } || when (hotspot) {
                FurnitureHotspotId.LAPTOP_ON_BED -> app.categoryHint == AppCategory.AI_AND_RESEARCH
                FurnitureHotspotId.STUDY_DESK -> app.categoryHint == AppCategory.NOTES_AND_PLANNING || app.categoryHint == AppCategory.WORK_AND_FILES
                FurnitureHotspotId.BEDSIDE_TABLE -> app.categoryHint == AppCategory.WELLNESS_AND_HEALTH
                FurnitureHotspotId.WARDROBE_CLOSET -> app.categoryHint == AppCategory.CREATIVE_TOOLS
                FurnitureHotspotId.KITCHEN_CABINET -> app.categoryHint == AppCategory.FOOD_AND_SHOPPING
                FurnitureHotspotId.BATHROOM_CABINET -> app.categoryHint == AppCategory.WELLNESS_AND_HEALTH
                FurnitureHotspotId.ENTRANCE_MAILBOX -> app.categoryHint == AppCategory.COMMUNICATION_AND_TRAVEL
                FurnitureHotspotId.GREEN_PORTAL -> false
            }
        }
        return if (matches.isNotEmpty()) matches else all.take(9)
    }

    /**
     * Resolves apps for each Workroom Workbench shelf category.
     */
    fun getAppsForWorkroomCategory(
        subCategory: WorkroomSubCategory,
        currentSettings: LauncherSettings
    ): List<InstalledApp> {
        val all = _installedApps.value
        val customSet = currentSettings.customCollectionPackages[subCategory.id]
        if (customSet != null) {
            return all.filter { it.packageName in customSet }
        }

        val matches = all.filter { app ->
            subCategory.packageHints.any { hint ->
                app.packageName.contains(hint, ignoreCase = true)
            } || when (subCategory) {
                WorkroomSubCategory.AI_AND_RESEARCH -> app.categoryHint == AppCategory.AI_AND_RESEARCH
                WorkroomSubCategory.WORK_AND_FILES -> app.categoryHint == AppCategory.WORK_AND_FILES
                WorkroomSubCategory.CREATIVE_TOOLS -> app.categoryHint == AppCategory.CREATIVE_TOOLS
                WorkroomSubCategory.NOTES_AND_PLANNING -> app.categoryHint == AppCategory.NOTES_AND_PLANNING
            }
        }
        return if (matches.isNotEmpty()) matches else all.take(6)
    }

    /**
     * Resolves apps for each Entertainment Room Smart TV Channel.
     */
    fun getAppsForTvChannel(
        channel: TvChannelCategory,
        currentSettings: LauncherSettings
    ): List<InstalledApp> {
        val all = _installedApps.value
        val customSet = currentSettings.customCollectionPackages[channel.id]
        if (customSet != null) {
            return all.filter { it.packageName in customSet }
        }

        if (channel == TvChannelCategory.MY_LIST) {
            val favs = all.filter { it.packageName in currentSettings.favoritePackages }
            return if (favs.isNotEmpty()) favs else all.filter { it.categoryHint == AppCategory.ENTERTAINMENT_TV }.take(8)
        }

        val matches = all.filter { app ->
            channel.packageHints.any { hint ->
                app.packageName.contains(hint, ignoreCase = true)
            } || app.categoryHint == AppCategory.ENTERTAINMENT_TV
        }
        return if (matches.isNotEmpty()) matches else all.take(8)
    }

    fun toggleHotspotApp(hotspot: FurnitureHotspotId, app: InstalledApp) {
        val currentSettings = settings.value
        val seed = getAppsForHotspot(hotspot, currentSettings).map { it.packageName }.toSet()
        viewModelScope.launch {
            settingsDataStore.toggleCollectionPackage(hotspot.name, app.packageName, seed)
        }
    }

    fun toggleWorkroomSubCategoryApp(subCategory: WorkroomSubCategory, app: InstalledApp) {
        val currentSettings = settings.value
        val seed = getAppsForWorkroomCategory(subCategory, currentSettings).map { it.packageName }.toSet()
        viewModelScope.launch {
            settingsDataStore.toggleCollectionPackage(subCategory.id, app.packageName, seed)
        }
    }

    fun toggleTvChannelApp(channel: TvChannelCategory, app: InstalledApp) {
        val currentSettings = settings.value
        val seed = getAppsForTvChannel(channel, currentSettings).map { it.packageName }.toSet()
        viewModelScope.launch {
            settingsDataStore.toggleCollectionPackage(channel.id, app.packageName, seed)
        }
    }

    fun toggleFavorite(app: InstalledApp) {
        viewModelScope.launch {
            settingsDataStore.toggleFavoritePackage(app.packageName)
        }
    }

    fun setMasterAnimation(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setMasterAnimation(enabled) }
    }

    fun setShowGirl(visible: Boolean) {
        viewModelScope.launch { settingsDataStore.setShowGirlCharacter(visible) }
    }

    fun setShowPets(visible: Boolean) {
        viewModelScope.launch { settingsDataStore.setShowPets(visible) }
    }

    fun setFocusMode(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setFocusMode(enabled) }
    }

    fun setDefaultRoom(room: RoomId) {
        viewModelScope.launch { settingsDataStore.setDefaultRoom(room) }
    }

    fun setThemePreset(preset: HouseThemePreset) {
        viewModelScope.launch { settingsDataStore.setThemePreset(preset) }
    }

    fun updateCustomNote(note: String) {
        viewModelScope.launch { settingsDataStore.setCustomNoteText(note) }
    }

    fun updateWeatherText(weather: String) {
        viewModelScope.launch { settingsDataStore.setWeatherStatusText(weather) }
    }

    fun updateDailyTasks(tasks: List<String>) {
        viewModelScope.launch { settingsDataStore.updateDailyTasks(tasks) }
    }
}
