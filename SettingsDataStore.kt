package com.mylittleworld.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mylittleworld.launcher.model.FurnitureHotspotId
import com.mylittleworld.launcher.model.HouseThemePreset
import com.mylittleworld.launcher.model.LauncherSettings
import com.mylittleworld.launcher.model.RoomId
import com.mylittleworld.launcher.model.TvChannelCategory
import com.mylittleworld.launcher.model.WorkroomSubCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "my_little_world_complete_prefs")

/**
 * Persists all launcher preferences, custom app collections, tasks, widgets,
 * and the Master Animation switch across restarts using Jetpack DataStore.
 */
class SettingsDataStore(private val context: Context) {

    companion object {
        private val KEY_MASTER_ANIMATION = booleanPreferencesKey("master_animation_enabled")
        private val KEY_SHOW_GIRL = booleanPreferencesKey("show_girl_character")
        private val KEY_SHOW_PETS = booleanPreferencesKey("show_pets")
        private val KEY_FOCUS_MODE = booleanPreferencesKey("focus_mode_enabled")
        private val KEY_DEFAULT_ROOM = stringPreferencesKey("default_room")
        private val KEY_THEME_PRESET = stringPreferencesKey("theme_preset")
        private val KEY_CUSTOM_NOTE = stringPreferencesKey("custom_note_text")
        private val KEY_WEATHER_TEXT = stringPreferencesKey("weather_status_text")
        private val KEY_DAILY_TASKS = stringPreferencesKey("daily_tasks_pipe_separated")
        private val KEY_FAVORITE_PACKAGES = stringSetPreferencesKey("favorite_packages")
        private val KEY_RECENT_PACKAGES = stringPreferencesKey("recent_packages_csv")

        private fun collectionKey(collectionId: String) =
            stringSetPreferencesKey("collection_pkgs_$collectionId")
    }

    private val allCollectionKeys: List<String> by lazy {
        FurnitureHotspotId.entries.map { it.name } +
            WorkroomSubCategory.entries.map { it.id } +
            TvChannelCategory.entries.map { it.id }
    }

    val settingsFlow: Flow<LauncherSettings> = context.dataStore.data.map { prefs ->
        val recentList = prefs[KEY_RECENT_PACKAGES]
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        val tasksList = prefs[KEY_DAILY_TASKS]
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?: listOf(
                "Finish UI design review",
                "Evening walk with dog",
                "10 min skincare & water"
            )

        val customCollections = mutableMapOf<String, Set<String>>()
        for (cid in allCollectionKeys) {
            prefs[collectionKey(cid)]?.let { assignedSet ->
                customCollections[cid] = assignedSet
            }
        }

        val themePreset = HouseThemePreset.entries.find {
            it.name == prefs[KEY_THEME_PRESET]
        } ?: HouseThemePreset.COZY_NIGHT

        LauncherSettings(
            masterAnimationEnabled = prefs[KEY_MASTER_ANIMATION] ?: true,
            showGirlCharacter = prefs[KEY_SHOW_GIRL] ?: true,
            showPets = prefs[KEY_SHOW_PETS] ?: true,
            focusModeEnabled = prefs[KEY_FOCUS_MODE] ?: false,
            defaultRoom = RoomId.fromName(prefs[KEY_DEFAULT_ROOM]),
            themePreset = themePreset,
            customNoteText = prefs[KEY_CUSTOM_NOTE] ?: "A better me lives here. ♥",
            weatherStatusText = prefs[KEY_WEATHER_TEXT] ?: "26°C • Clear Night",
            dailyTasks = tasksList,
            favoritePackages = prefs[KEY_FAVORITE_PACKAGES] ?: emptySet(),
            recentPackages = recentList,
            customCollectionPackages = customCollections
        )
    }

    suspend fun setMasterAnimation(enabled: Boolean) {
        context.dataStore.edit { it[KEY_MASTER_ANIMATION] = enabled }
    }

    suspend fun setShowGirlCharacter(visible: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_GIRL] = visible }
    }

    suspend fun setShowPets(visible: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_PETS] = visible }
    }

    suspend fun setFocusMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_FOCUS_MODE] = enabled }
    }

    suspend fun setDefaultRoom(roomId: RoomId) {
        context.dataStore.edit { it[KEY_DEFAULT_ROOM] = roomId.name }
    }

    suspend fun setThemePreset(preset: HouseThemePreset) {
        context.dataStore.edit { it[KEY_THEME_PRESET] = preset.name }
    }

    suspend fun setCustomNoteText(note: String) {
        context.dataStore.edit { it[KEY_CUSTOM_NOTE] = note }
    }

    suspend fun setWeatherStatusText(weather: String) {
        context.dataStore.edit { it[KEY_WEATHER_TEXT] = weather }
    }

    suspend fun updateDailyTasks(tasks: List<String>) {
        context.dataStore.edit {
            it[KEY_DAILY_TASKS] = tasks.filter { t -> t.isNotBlank() }.joinToString("|")
        }
    }

    suspend fun toggleFavoritePackage(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITE_PACKAGES]?.toMutableSet() ?: mutableSetOf()
            if (!current.add(packageName)) {
                current.remove(packageName)
            }
            prefs[KEY_FAVORITE_PACKAGES] = current
        }
    }

    suspend fun recordRecentLaunch(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_RECENT_PACKAGES]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toMutableList() ?: mutableListOf()
            current.remove(packageName)
            current.add(0, packageName)
            prefs[KEY_RECENT_PACKAGES] = current.take(16).joinToString(",")
        }
    }

    suspend fun toggleCollectionPackage(
        collectionId: String,
        packageName: String,
        defaultSeed: Set<String>
    ) {
        context.dataStore.edit { prefs ->
            val key = collectionKey(collectionId)
            val current = (prefs[key] ?: defaultSeed).toMutableSet()
            if (!current.add(packageName)) {
                current.remove(packageName)
            }
            prefs[key] = current
        }
    }
}
