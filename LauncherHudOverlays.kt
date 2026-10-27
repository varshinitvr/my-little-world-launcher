package com.mylittleworld.launcher.ui.hud

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mylittleworld.launcher.model.RoomId
import com.mylittleworld.launcher.ui.theme.SitcomPalette
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Renders the clean, readable HUD controls layered on top of all 6 rooms:
 * - Top-Left: Customizable Time, Date, Weather & expandable Quick Tasks Widget
 * - Top-Right: Prominent Master Animation [ON/OFF] Switch
 * - Left Vertical Dock: Home, Map, Search, Focus, Settings
 * - Bottom Horizontal Bar: Bedroom, Workroom, Entertainment, Kitchen, Bathroom, Entrance
 */
@Composable
fun LauncherHudOverlays(
    currentRoom: RoomId,
    masterAnimationEnabled: Boolean,
    focusModeEnabled: Boolean,
    weatherStatusText: String,
    dailyTasks: List<String>,
    isWalkingTransition: Boolean,
    walkingDestination: RoomId?,
    onToggleMasterAnimation: (Boolean) -> Unit,
    onSelectRoom: (RoomId) -> Unit,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFocusToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeText by remember { mutableStateOf("") }
    var currentDateText by remember { mutableStateOf("") }
    var showTasksDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeText = timeFormat.format(now)
            currentDateText = dateFormat.format(now)
            delay(15_000L)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. TOP-LEFT LIVE TIME, DATE, WEATHER & TASKS WIDGET
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 88.dp, top = 12.dp)
        ) {
            Surface(
                onClick = { showTasksDropdown = !showTasksDropdown },
                color = SitcomPalette.CharcoalPanel,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(2.dp, SitcomPalette.InkBlack, RoundedCornerShape(20.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentTimeText,
                            color = SitcomPalette.TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = currentDateText,
                            color = SitcomPalette.TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = weatherStatusText,
                            color = SitcomPalette.WarmYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${dailyTasks.size} Tasks • Tap to view",
                            color = SitcomPalette.TealAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showTasksDropdown || focusModeEnabled) {
                Surface(
                    color = SitcomPalette.CharcoalPanel,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .width(240.dp)
                        .border(2.dp, SitcomPalette.TealAccent, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (focusModeEnabled) "FOCUS MODE • TODAY'S TASKS" else "TODAY'S TASKS",
                            color = SitcomPalette.TealAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        dailyTasks.forEach { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SitcomPalette.PortalNeonGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = task,
                                    color = SitcomPalette.TextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Walking Between Connected Rooms Status Banner (when character physically walks to next room)
        if (isWalkingTransition && walkingDestination != null) {
            Surface(
                color = SitcomPalette.CharcoalPanel,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .border(2.dp, SitcomPalette.PortalNeonGreen, CircleShape)
            ) {
                Text(
                    text = "Walking to ${walkingDestination.title}... (Use Map for instant jump)",
                    color = SitcomPalette.PortalNeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        // 2. TOP-RIGHT MASTER ANIMATION [ON / OFF] PILL
        Surface(
            color = SitcomPalette.CharcoalPanel,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 18.dp, top = 12.dp)
                .border(2.dp, SitcomPalette.InkBlack, CircleShape)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Animation",
                    color = SitcomPalette.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (masterAnimationEnabled) "[ON]" else "[OFF]",
                    color = if (masterAnimationEnabled) SitcomPalette.PortalNeonGreen else SitcomPalette.WarmOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = masterAnimationEnabled,
                    onCheckedChange = onToggleMasterAnimation,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2ECC71),
                        uncheckedThumbColor = SitcomPalette.TextSecondary,
                        uncheckedTrackColor = SitcomPalette.InkBlack
                    )
                )
            }
        }

        // 3. LEFT VERTICAL NAVIGATION DOCK (Home, Map, Search, Focus, Settings)
        Surface(
            color = SitcomPalette.CharcoalPanel,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp)
                .border(2.5.dp, SitcomPalette.InkBlack, RoundedCornerShape(28.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp)
            ) {
                SideDockItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isActive = currentRoom == RoomId.BEDROOM,
                    onClick = onHomeClick
                )
                SideDockItem(
                    icon = Icons.Default.Map,
                    label = "Map",
                    isActive = false,
                    onClick = onMapClick
                )
                SideDockItem(
                    icon = Icons.Default.Search,
                    label = "Search",
                    isActive = false,
                    onClick = onSearchClick
                )
                SideDockItem(
                    icon = Icons.Default.CenterFocusStrong,
                    label = "Focus",
                    isActive = focusModeEnabled,
                    onClick = onFocusToggle
                )
                SideDockItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isActive = false,
                    onClick = onSettingsClick
                )
            }
        }

        // 4. BOTTOM SIX-ROOM NAVIGATION BAR
        Surface(
            color = SitcomPalette.CharcoalPanel,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 88.dp, end = 18.dp, bottom = 12.dp)
                .fillMaxWidth()
                .height(66.dp)
                .border(2.5.dp, SitcomPalette.InkBlack, RoundedCornerShape(28.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                RoomId.entries.forEach { room ->
                    val isSelected = room == currentRoom
                    BottomRoomTab(
                        room = room,
                        icon = roomIcon(room),
                        isSelected = isSelected,
                        onClick = { onSelectRoom(room) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SideDockItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) SitcomPalette.TealAccent else SitcomPalette.TextPrimary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isActive) SitcomPalette.TealAccent else SitcomPalette.TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}

@Composable
private fun BottomRoomTab(
    room: RoomId,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) SitcomPalette.TealAccent.copy(alpha = 0.22f)
                else Color.Transparent
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) SitcomPalette.TealAccent else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = room.title,
                tint = if (isSelected) SitcomPalette.TealAccent else SitcomPalette.TextPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = room.title,
                color = if (isSelected) SitcomPalette.TealAccent else SitcomPalette.TextSecondary,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

private fun roomIcon(room: RoomId): ImageVector = when (room) {
    RoomId.BEDROOM -> Icons.Default.Bed
    RoomId.WORKROOM -> Icons.Default.DesktopWindows
    RoomId.ENTERTAINMENT -> Icons.Default.Tv
    RoomId.KITCHEN -> Icons.Default.Kitchen
    RoomId.BATHROOM -> Icons.Default.Bathtub
    RoomId.ENTRANCE -> Icons.Default.DoorFront
}
