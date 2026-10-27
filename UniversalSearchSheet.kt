package com.mylittleworld.launcher.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mylittleworld.launcher.model.InstalledApp
import com.mylittleworld.launcher.model.RoomId
import com.mylittleworld.launcher.ui.rooms.AppGridTile
import com.mylittleworld.launcher.ui.theme.SitcomPalette

enum class SearchFilterTab(val title: String) {
    ALL_APPS("All Apps"),
    FAVORITES("Favorites"),
    RECENT("Recent")
}

/**
 * Universal Search & All Apps Drawer matching the bottom-right panel of the reference mockup.
 * Searches across installed apps, the 6 house rooms, and categories.
 */
@Composable
fun UniversalSearchSheet(
    allApps: List<InstalledApp>,
    favoritePackages: Set<String>,
    recentPackages: List<String>,
    onLaunchApp: (InstalledApp) -> Unit,
    onToggleFavorite: (InstalledApp) -> Unit,
    onJumpToRoom: (RoomId) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(SearchFilterTab.ALL_APPS) }

    val matchingRooms = remember(query) {
        if (query.isBlank()) RoomId.entries
        else RoomId.entries.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.subtitle.contains(query, ignoreCase = true)
        }
    }

    val filteredApps = remember(query, activeTab, allApps, favoritePackages, recentPackages) {
        val baseList = when (activeTab) {
            SearchFilterTab.ALL_APPS -> allApps
            SearchFilterTab.FAVORITES -> allApps.filter { it.packageName in favoritePackages }
            SearchFilterTab.RECENT -> {
                val mapByPkg = allApps.associateBy { it.packageName }
                recentPackages.mapNotNull { mapByPkg[it] }
            }
        }
        if (query.isBlank()) {
            baseList
        } else {
            baseList.filter {
                it.label.contains(query, ignoreCase = true) ||
                    it.categoryHint.displayName.contains(query, ignoreCase = true) ||
                    it.packageName.contains(query, ignoreCase = true)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = SitcomPalette.InkBlack.copy(alpha = 0.96f),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = SitcomPalette.TextPrimary
                        )
                    }
                    Text(
                        text = "Search & All Apps (${allApps.size} installed)",
                        color = SitcomPalette.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SitcomPalette.TextSecondary
                        )
                    },
                    placeholder = {
                        Text("Search apps, rooms, or anything...", color = SitcomPalette.TextSecondary)
                    },
                    singleLine = true,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Tabs: All Apps | Favorites | Recent + Room Quick Chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SearchFilterTab.entries.forEach { tab ->
                        val selected = tab == activeTab
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (selected) SitcomPalette.TealAccent.copy(alpha = 0.25f)
                                    else SitcomPalette.CharcoalGrey
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (selected) SitcomPalette.TealAccent else SitcomPalette.CharcoalGrey,
                                    shape = CircleShape
                                )
                                .clickable { activeTab = tab }
                                .padding(horizontal = 16.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = tab.title,
                                color = if (selected) SitcomPalette.TealAccent else SitcomPalette.TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(matchingRooms) { room ->
                            Surface(
                                onClick = {
                                    onJumpToRoom(room)
                                    onDismiss()
                                },
                                color = SitcomPalette.DustyBlueWall,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.border(1.dp, SitcomPalette.TealAccent, RoundedCornerShape(10.dp))
                            ) {
                                Text(
                                    text = room.title,
                                    color = SitcomPalette.TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 84.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps, key = { "search_${it.packageName}" }) { app ->
                        AppGridTile(
                            app = app,
                            isFavorite = app.packageName in favoritePackages,
                            onClick = {
                                onLaunchApp(app)
                                onDismiss()
                            },
                            onLongOrStarClick = { onToggleFavorite(app) }
                        )
                    }
                }
            }
        }
    }
}
