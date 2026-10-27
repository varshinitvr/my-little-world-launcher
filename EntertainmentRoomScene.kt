package com.mylittleworld.launcher.ui.rooms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mylittleworld.launcher.model.CatPoseState
import com.mylittleworld.launcher.model.DogPoseState
import com.mylittleworld.launcher.model.GirlPoseState
import com.mylittleworld.launcher.model.HouseThemePreset
import com.mylittleworld.launcher.model.InstalledApp
import com.mylittleworld.launcher.model.TvChannelCategory
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawGoldenDog
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawProtagonistGirl
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawTuxedoCat
import com.mylittleworld.launcher.ui.theme.SitcomPalette

/**
 * ROOM 3: ENTERTAINMENT ROOM — REAL TELEVISION & REMOTE CONTROL
 *
 * Matches the middle-left panel of the reference image:
 * - Physical Widescreen Television Bezel mounted over the living room console.
 * - Real Smart-TV Channel Bar:
 *   - Live TV
 *   - Streaming
 *   - Movies
 *   - Music
 *   - My List
 * - Surfaces YouTube, YouTube Music, Google TV, VLC, Letterboxd, Hotstar, JioCinema, SonyLIV,
 *   Netflix, Spotify, and any other installed entertainment apps.
 * - Interactive TV Remote Control Widget on the right side (CH+, CH-, Left/Right highlight, OK/Launch, Standby).
 * - Girl sits on the red sofa with popcorn pointing the remote; Cat & Dog relax by the sofa.
 */
@Composable
fun EntertainmentRoomScene(
    themePreset: HouseThemePreset,
    showGirl: Boolean,
    showPets: Boolean,
    girlPose: GirlPoseState,
    catPose: CatPoseState,
    dogPose: DogPoseState,
    allApps: List<InstalledApp>,
    favoritePackages: Set<String>,
    getAppsForTvChannel: (TvChannelCategory) -> List<InstalledApp>,
    onToggleAppInTvChannel: (TvChannelCategory, InstalledApp) -> Unit,
    onLaunchApp: (InstalledApp) -> Unit,
    onToggleFavorite: (InstalledApp) -> Unit,
    onGirlClick: () -> Unit,
    onBackToBedroom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeChannel by remember { mutableStateOf(TvChannelCategory.LIVE_TV) }
    var tvPowerOn by remember { mutableStateOf(true) }
    var isEditingChannel by remember { mutableStateOf(false) }
    var highlightedAppIndex by remember { mutableIntStateOf(0) }

    val channelApps = getAppsForTvChannel(activeChannel)
    val displayedApps = if (isEditingChannel) allApps else channelApps
    val assignedPkgSet = remember(channelApps) { channelApps.map { it.packageName }.toSet() }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        // LAYER 1, 2 & 3: Living Room Wall, Sci-Fi Mad Scientist Poster, Sofa, Girl with Popcorn, Cat & Dog
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawEntertainmentLivingRoom(themePreset)

            if (showGirl) {
                drawProtagonistGirl(
                    pose = GirlPoseState.WATCH_TV_COUCH,
                    centerX = size.width * 0.16f,
                    centerY = size.height * 0.60f,
                    breathPulse = 1.0f,
                    facingRight = true
                )
            }
            if (showPets) {
                drawTuxedoCat(
                    pose = catPose,
                    centerX = size.width * 0.24f,
                    centerY = size.height * 0.72f,
                    pulse = 1.0f
                )
                drawGoldenDog(
                    pose = dogPose,
                    centerX = size.width * 0.42f,
                    centerY = size.height * 0.76f,
                    pulse = 1.0f
                )
            }
        }

        // Top Breadcrumb Pill
        RoomBreadcrumbPill(
            title = "Entertainment Room – TV (${activeChannel.title})",
            onBackToBedroom = onBackToBedroom,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 88.dp, top = 74.dp)
        )

        // LAYER 4A: Physical Widescreen Smart Television Frame & Channel UI
        Surface(
            color = Color(0xFF0D111A),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 150.dp, end = 110.dp, top = 122.dp)
                .fillMaxWidth()
                .height(h * 0.56f)
                .border(6.dp, SitcomPalette.InkBlack, RoundedCornerShape(18.dp))
        ) {
            if (!tvPowerOn) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF07090E))
                        .clickable { tvPowerOn = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TV Standby • Tap Remote Power or Screen to Turn On",
                        color = SitcomPalette.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF10192B), Color(0xFF0A0F1A))
                            )
                        )
                        .padding(14.dp)
                ) {
                    // Smart TV Channel Top Navigation Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TvChannelCategory.entries.forEach { channel ->
                                val selected = channel == activeChannel
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (selected) channel.accentColor else Color(0xFF1C2536)
                                        )
                                        .clickable {
                                            activeChannel = channel
                                            highlightedAppIndex = 0
                                        }
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = channel.title,
                                        color = if (selected) SitcomPalette.InkBlack else SitcomPalette.TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeChannel.badgeText,
                                color = activeChannel.accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { isEditingChannel = !isEditingChannel },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Channel Apps",
                                    tint = if (isEditingChannel) SitcomPalette.WarmYellow else SitcomPalette.TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Channel App Tiles Grid inside the TV Screen
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(
                            displayedApps,
                            key = { _, app -> "${activeChannel.id}_${app.packageName}" }
                        ) { index, app ->
                            val isHighlighted = index == highlightedAppIndex && !isEditingChannel
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isHighlighted) activeChannel.accentColor.copy(alpha = 0.28f)
                                        else Color(0xFF172030)
                                    )
                                    .border(
                                        width = if (isHighlighted) 2.5.dp else 1.dp,
                                        color = if (isHighlighted) activeChannel.accentColor else SitcomPalette.InkBlack,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(4.dp)
                            ) {
                                AppGridTile(
                                    app = app,
                                    isEditing = isEditingChannel,
                                    isAssigned = app.packageName in assignedPkgSet,
                                    isFavorite = app.packageName in favoritePackages,
                                    accentColor = activeChannel.accentColor,
                                    onClick = {
                                        highlightedAppIndex = index
                                        if (isEditingChannel) {
                                            onToggleAppInTvChannel(activeChannel, app)
                                        } else {
                                            onLaunchApp(app)
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

        // LAYER 4B: Interactive Physical TV Remote Control Dock on the Right
        Surface(
            color = SitcomPalette.CharcoalPanel,
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .width(84.dp)
                .border(3.dp, SitcomPalette.InkBlack, RoundedCornerShape(26.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)
            ) {
                Text(
                    text = "REMOTE",
                    color = SitcomPalette.WarmYellow,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                // Power Button
                IconButton(
                    onClick = { tvPowerOn = !tvPowerOn },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFD32F2F), CircleShape)
                        .border(2.dp, SitcomPalette.InkBlack, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "TV Power",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Channel Up / Down cycles through Live TV, Streaming, Movies, Music, My List
                IconButton(
                    onClick = {
                        val list = TvChannelCategory.entries
                        activeChannel = list[(activeChannel.ordinal + 1) % list.size]
                        highlightedAppIndex = 0
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Next Channel", tint = SitcomPalette.TextPrimary)
                }
                Text("CH", color = SitcomPalette.TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = {
                        val list = TvChannelCategory.entries
                        activeChannel = list[(activeChannel.ordinal - 1 + list.size) % list.size]
                        highlightedAppIndex = 0
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Prev Channel", tint = SitcomPalette.TextPrimary)
                }

                // Left / Right D-Pad for selecting app tile on screen
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            if (displayedApps.isNotEmpty()) {
                                highlightedAppIndex = (highlightedAppIndex - 1 + displayedApps.size) % displayedApps.size
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Prev App", tint = SitcomPalette.TealAccent)
                    }
                    IconButton(
                        onClick = {
                            if (displayedApps.isNotEmpty()) {
                                highlightedAppIndex = (highlightedAppIndex + 1) % displayedApps.size
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next App", tint = SitcomPalette.TealAccent)
                    }
                }

                // OK / Launch Selected Channel App Button
                Surface(
                    onClick = {
                        displayedApps.getOrNull(highlightedAppIndex)?.let { onLaunchApp(it) }
                    },
                    color = SitcomPalette.TealAccent,
                    shape = CircleShape,
                    modifier = Modifier.border(2.dp, SitcomPalette.InkBlack, CircleShape)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Launch",
                            tint = SitcomPalette.InkBlack,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "OK",
                            color = SitcomPalette.InkBlack,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawEntertainmentLivingRoom(themePreset: HouseThemePreset) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.72f

    drawRect(
        brush = Brush.verticalGradient(
            colors = SitcomPalette.wallGradientFor(
                themePreset,
                SitcomPalette.DustyBlueShadow,
                SitcomPalette.DustyBlueWall
            ),
            startY = 0f,
            endY = floorY
        ),
        size = Size(w, floorY)
    )
    drawRect(color = SitcomPalette.WoodDark, topLeft = Offset(0f, floorY), size = Size(w, h - floorY))

    // Framed Sci-Fi Sitcom Portrait on Left Wall (homage to eccentric scientist art direction)
    val frameLeft = w * 0.08f
    val frameTop = h * 0.16f
    val frameW = w * 0.11f
    val frameH = h * 0.26f
    drawRoundRect(color = SitcomPalette.WoodWarm, topLeft = Offset(frameLeft, frameTop), size = Size(frameW, frameH), cornerRadius = CornerRadius(8f, 8f))
    drawRoundRect(color = Color(0xFF28354A), topLeft = Offset(frameLeft + 8f, frameTop + 8f), size = Size(frameW - 16f, frameH - 16f), cornerRadius = CornerRadius(4f, 4f))
    // Spiky blue-grey hair silhouette inside portrait
    drawCircle(color = Color(0xFF90CAF9), radius = 22f, center = Offset(frameLeft + frameW * 0.5f, frameTop + frameH * 0.42f))
    drawCircle(color = Color(0xFFE5A982), radius = 15f, center = Offset(frameLeft + frameW * 0.5f, frameTop + frameH * 0.50f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(frameLeft, frameTop), size = Size(frameW, frameH), cornerRadius = CornerRadius(8f, 8f), style = Stroke(width = 4.5f))

    // Wooden TV Console Stand under the Television
    val consoleLeft = w * 0.24f
    val consoleTop = h * 0.69f
    val consoleW = w * 0.58f
    drawRoundRect(color = SitcomPalette.WoodWarm, topLeft = Offset(consoleLeft, consoleTop), size = Size(consoleW, h * 0.14f), cornerRadius = CornerRadius(10f, 10f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(consoleLeft, consoleTop), size = Size(consoleW, h * 0.14f), cornerRadius = CornerRadius(10f, 10f), style = Stroke(width = 5f))

    // Cozy Red Living Room Sofa on Left Foreground
    val sofaLeft = w * 0.06f
    val sofaTop = h * 0.56f
    val sofaW = w * 0.24f
    val sofaH = h * 0.26f
    drawRoundRect(color = SitcomPalette.RugTerracotta, topLeft = Offset(sofaLeft, sofaTop), size = Size(sofaW, sofaH), cornerRadius = CornerRadius(24f, 24f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(sofaLeft, sofaTop), size = Size(sofaW, sofaH), cornerRadius = CornerRadius(24f, 24f), style = Stroke(width = 5.5f))
}
