package com.mylittleworld.launcher.ui.rooms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mylittleworld.launcher.model.FurnitureHotspotId
import com.mylittleworld.launcher.model.HouseThemePreset
import com.mylittleworld.launcher.model.InstalledApp
import com.mylittleworld.launcher.model.LauncherSettings
import com.mylittleworld.launcher.model.RoomId
import com.mylittleworld.launcher.ui.theme.SitcomPalette

/**
 * Interactive 3-Story Cutaway Dollhouse Floor-Plan Map Modal (matching bottom-right of reference mockup).
 * Lets the user jump directly to any of the 6 rooms without waiting for character walking.
 */
@Composable
fun FloorPlanMapModal(
    currentRoom: RoomId,
    onSelectRoom: (RoomId) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = SitcomPalette.CharcoalGrey,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, SitcomPalette.InkBlack, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Room Map — 3-Story House Cutaway",
                            color = SitcomPalette.TextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Tap any room in the house to jump directly",
                            color = SitcomPalette.TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Map",
                            tint = SitcomPalette.TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pitched Cartoon House Roof Canvas above the 3 floors
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                ) {
                    val roofPath = Path().apply {
                        moveTo(0f, size.height)
                        lineTo(size.width * 0.25f, 4f)
                        lineTo(size.width * 0.50f, size.height * 0.65f)
                        lineTo(size.width * 0.75f, 4f)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path = roofPath, color = SitcomPalette.WoodDark)
                    drawPath(path = roofPath, color = SitcomPalette.InkBlack, style = Stroke(width = 5f))
                }

                // 3 Floors x 2 Wings Architectural Cutaway Grid
                val floors = listOf(
                    "Top Floor (Sanctuary & Studio)" to listOf(RoomId.BEDROOM, RoomId.WORKROOM),
                    "Middle Floor (Living & Pantry)" to listOf(RoomId.ENTERTAINMENT, RoomId.KITCHEN),
                    "Ground Floor (Wellness & Entry)" to listOf(RoomId.BATHROOM, RoomId.ENTRANCE)
                )

                floors.forEach { (floorLabel, roomsOnFloor) ->
                    Text(
                        text = floorLabel.uppercase(),
                        color = SitcomPalette.WarmYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        roomsOnFloor.forEach { room ->
                            val isSelected = room == currentRoom
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(74.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) SitcomPalette.DustyBlueWall else SitcomPalette.InkBlack
                                    )
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.5.dp,
                                        color = if (isSelected) SitcomPalette.TealAccent else SitcomPalette.WoodWarm,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        onSelectRoom(room)
                                        onDismiss()
                                    }
                                    .padding(10.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            color = SitcomPalette.CharcoalPanel,
                                            shape = CircleShape,
                                            modifier = Modifier.border(1.5.dp, SitcomPalette.InkBlack, CircleShape)
                                        ) {
                                            Text(
                                                text = room.title,
                                                color = SitcomPalette.TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                            )
                                        }
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(9.dp)
                                                    .background(SitcomPalette.PortalNeonGreen, CircleShape)
                                            )
                                        }
                                    }
                                    Text(
                                        text = room.subtitle,
                                        color = SitcomPalette.TextSecondary,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

/**
 * Floating Furniture App Panel Modal — Opens when tapping a Bedroom furniture hotspot
 * (Laptop, Study Desk, Bedside Table, Wardrobe).
 */
@Composable
fun FurnitureAppPanelModal(
    hotspot: FurnitureHotspotId,
    assignedApps: List<InstalledApp>,
    allApps: List<InstalledApp>,
    favoritePackages: Set<String>,
    onLaunchApp: (InstalledApp) -> Unit,
    onToggleAssignApp: (InstalledApp) -> Unit,
    onToggleFavorite: (InstalledApp) -> Unit,
    onDismiss: () -> Unit
) {
    var isEditingCollection by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = SitcomPalette.CharcoalPanel,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(470.dp)
                .border(3.dp, SitcomPalette.InkBlack, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = hotspot.title,
                            color = hotspot.accentColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = hotspot.description,
                            color = SitcomPalette.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = { isEditingCollection = !isEditingCollection }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Customize Furniture Apps",
                            tint = if (isEditingCollection) SitcomPalette.WarmYellow else SitcomPalette.TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Panel",
                            tint = SitcomPalette.TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val displayedApps = if (isEditingCollection) allApps else assignedApps
                val assignedPkgSet = remember(assignedApps) { assignedApps.map { it.packageName }.toSet() }

                if (displayedApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No matching apps assigned yet.",
                                color = SitcomPalette.TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { isEditingCollection = true },
                                colors = ButtonDefaults.buttonColors(containerColor = hotspot.accentColor)
                            ) {
                                Text("Add Installed Apps", color = SitcomPalette.InkBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(displayedApps, key = { "${hotspot.name}_${it.packageName}" }) { app ->
                            val isAssigned = app.packageName in assignedPkgSet
                            val isFav = app.packageName in favoritePackages
                            AppGridTile(
                                app = app,
                                isEditing = isEditingCollection,
                                isAssigned = isAssigned,
                                isFavorite = isFav,
                                accentColor = hotspot.accentColor,
                                onClick = {
                                    if (isEditingCollection) {
                                        onToggleAssignApp(app)
                                    } else {
                                        onLaunchApp(app)
                                        onDismiss()
                                    }
                                },
                                onLongOrStarClick = { onToggleFavorite(app) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Complete Settings, Theme, Default Room & Widgets Customization Modal.
 */
@Composable
fun LauncherSettingsModal(
    settings: LauncherSettings,
    onToggleAnimation: (Boolean) -> Unit,
    onToggleGirl: (Boolean) -> Unit,
    onTogglePets: (Boolean) -> Unit,
    onToggleFocusMode: (Boolean) -> Unit,
    onSelectDefaultRoom: (RoomId) -> Unit,
    onSelectThemePreset: (HouseThemePreset) -> Unit,
    onUpdateNote: (String) -> Unit,
    onUpdateWeather: (String) -> Unit,
    onUpdateTasks: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var noteDraft by remember(settings.customNoteText) { mutableStateOf(settings.customNoteText) }
    var weatherDraft by remember(settings.weatherStatusText) { mutableStateOf(settings.weatherStatusText) }
    var tasksDraft by remember(settings.dailyTasks) { mutableStateOf(settings.dailyTasks.joinToString("
")) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = SitcomPalette.CharcoalGrey,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(540.dp)
                .border(3.dp, SitcomPalette.InkBlack, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "House, Animation & Widget Settings",
                        color = SitcomPalette.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SitcomPalette.TextPrimary)
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        SettingSwitchRow(
                            title = "Master Animation (ON / OFF)",
                            subtitle = "Stops all character, pet, cabinet door & room transition animations when OFF",
                            checked = settings.masterAnimationEnabled,
                            onCheckedChange = onToggleAnimation
                        )
                        SettingSwitchRow(
                            title = "Show Protagonist Girl",
                            subtitle = "Display your cartoon character inside every room",
                            checked = settings.showGirlCharacter,
                            onCheckedChange = onToggleGirl
                        )
                        SettingSwitchRow(
                            title = "Show Cat & Dog Companions",
                            subtitle = "Display the tuxedo cat and golden dog in the house",
                            checked = settings.showPets,
                            onCheckedChange = onTogglePets
                        )
                        SettingSwitchRow(
                            title = "Focus Mode",
                            subtitle = "Highlights study/work tasks and dims visual distractions",
                            checked = settings.focusModeEnabled,
                            onCheckedChange = onToggleFocusMode
                        )
                    }

                    // Theme Preset Selector
                    item {
                        Text(
                            text = "Room Lighting & Theme Preset",
                            color = SitcomPalette.WarmYellow,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HouseThemePreset.entries.forEach { preset ->
                                val selected = preset == settings.themePreset
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) SitcomPalette.TealAccent else SitcomPalette.InkBlack)
                                        .clickable { onSelectThemePreset(preset) }
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = preset.label.substringBefore(" ("),
                                        color = if (selected) SitcomPalette.InkBlack else SitcomPalette.TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Default Home Room Selector
                    item {
                        Text(
                            text = "Default Home Screen Room",
                            color = SitcomPalette.WarmYellow,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RoomId.entries.forEach { room ->
                                val selected = room == settings.defaultRoom
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) SitcomPalette.WarmOrange else SitcomPalette.InkBlack)
                                        .clickable { onSelectDefaultRoom(room) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = room.title.take(5),
                                        color = if (selected) SitcomPalette.InkBlack else SitcomPalette.TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Customizable Sticky Note, Weather & Tasks Widgets
                    item {
                        OutlinedTextField(
                            value = noteDraft,
                            onValueChange = {
                                noteDraft = it
                                onUpdateNote(it)
                            },
                            label = { Text("Bedroom Sticky Note") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = weatherDraft,
                            onValueChange = {
                                weatherDraft = it
                                onUpdateWeather(it)
                            },
                            label = { Text("Top-Left Weather Pill Text") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tasksDraft,
                            onValueChange = {
                                tasksDraft = it
                                onUpdateTasks(it.lines())
                            },
                            label = { Text("Quick Tasks Widget (one per line)") },
                            minLines = 2,
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = SitcomPalette.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = SitcomPalette.TextSecondary, fontSize = 10.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
