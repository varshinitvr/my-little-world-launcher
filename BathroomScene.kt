package com.mylittleworld.launcher.ui.rooms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.mylittleworld.launcher.model.FurnitureHotspotId
import com.mylittleworld.launcher.model.GirlPoseState
import com.mylittleworld.launcher.model.HouseThemePreset
import com.mylittleworld.launcher.model.InstalledApp
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawProtagonistGirl
import com.mylittleworld.launcher.ui.theme.SitcomPalette

/**
 * ROOM 5: BATHROOM — MIRROR, SINK & WELLNESS CABINET
 *
 * Matches the bottom-left panel of the reference mockup:
 * - Left Wing: Arched Illuminated Bathroom Mirror, Ceramic Sink, Soap & Lotion Bottles,
 *   Hanging Pothos Plants, and the Girl getting ready (tapping the mirror cycles her routine:
 *   Brushing Teeth -> Washing Face -> Skincare Glow -> Hydration Check).
 * - Right Wing: Opening Wooden Wellness Cabinet revealing NoiseFit, cult.fit, Health, Clock,
 *   Weather, Period Tracker, Skincare, Routine, Water, Meditation, Self Care, and Notes shortcuts.
 */
@Composable
fun BathroomScene(
    masterAnimationEnabled: Boolean,
    themePreset: HouseThemePreset,
    showGirl: Boolean,
    wellnessApps: List<InstalledApp>,
    allApps: List<InstalledApp>,
    favoritePackages: Set<String>,
    onToggleWellnessApp: (InstalledApp) -> Unit,
    onLaunchApp: (InstalledApp) -> Unit,
    onToggleFavorite: (InstalledApp) -> Unit,
    onBackToBedroom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCabinetOpen by remember { mutableStateOf(true) }
    var isEditingCabinet by remember { mutableStateOf(false) }
    var mirrorRoutineStep by remember { mutableIntStateOf(0) }
    var glassesOfWater by remember { mutableIntStateOf(5) }

    val routineTitles = listOf(
        "Brushing Teeth & Morning Prep",
        "Washing Face & Refreshing",
        "Applying Vitamin C Skincare",
        "Mirror Affirmation: You've Got This!"
    )

    val doorOpenFraction by animateFloatAsState(
        targetValue = if (isCabinetOpen) 1f else 0f,
        animationSpec = if (masterAnimationEnabled) tween(480) else tween(0),
        label = "BathroomCabinetDoors"
    )

    val assignedSet = remember(wellnessApps) { wellnessApps.map { it.packageName }.toSet() }
    val displayedApps = if (isEditingCabinet) allApps else wellnessApps

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        // LAYER 1, 2 & 3: Bathroom Teal Tiles, Hanging Plants, Arched Mirror, Sink & Girl Getting Ready
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBathroomEnvironment(themePreset)

            if (showGirl) {
                drawProtagonistGirl(
                    pose = GirlPoseState.GETTING_READY_MIRROR,
                    centerX = size.width * 0.16f,
                    centerY = size.height * 0.58f,
                    breathPulse = 1.0f,
                    facingRight = true
                )
            }
        }

        // Top Breadcrumb Pill
        RoomBreadcrumbPill(
            title = "Bathroom – Wellness Cabinet",
            onBackToBedroom = onBackToBedroom,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 88.dp, top = 74.dp)
        )

        // Interactive Mirror Routine & Hydration Tap Zone on Left
        Surface(
            onClick = {
                mirrorRoutineStep = (mirrorRoutineStep + 1) % routineTitles.size
            },
            color = SitcomPalette.CharcoalPanel,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 88.dp, bottom = 92.dp)
                .border(2.dp, SitcomPalette.TealAccent, RoundedCornerShape(16.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = routineTitles[mirrorRoutineStep],
                        color = SitcomPalette.TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Tap mirror to switch routine • Water: $glassesOfWater/8 glasses",
                        color = SitcomPalette.TealAccent,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Surface(
                    onClick = { glassesOfWater = (glassesOfWater + 1).coerceAtMost(12) },
                    color = SitcomPalette.TealAccent,
                    shape = CircleShape
                ) {
                    Text(
                        text = "+1 💧",
                        color = SitcomPalette.InkBlack,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // LAYER 4: Physical Wooden Wellness Cabinet on the Right Wing
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 28.dp, top = 112.dp)
                .width(w * 0.44f)
                .height(h * 0.58f)
        ) {
            Surface(
                color = Color(0xFF425957),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .border(5.dp, SitcomPalette.WoodWarm, RoundedCornerShape(18.dp))
            ) {
                if (doorOpenFraction < 0.35f) {
                    // Closed Mirrored Cabinet Doors
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF5A7D7C))
                            .clickable { isCabinetOpen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap to Open Wellness Cabinet",
                            color = SitcomPalette.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isEditingCabinet) "Customize Wellness Apps" else "Wellness, Fitness & Routine",
                                color = SitcomPalette.LampGlowYellow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { isEditingCabinet = !isEditingCabinet },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Customize Wellness Cabinet",
                                        tint = if (isEditingCabinet) SitcomPalette.WarmYellow else SitcomPalette.TextSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { isCabinetOpen = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = SitcomPalette.TealAccent),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Close", color = SitcomPalette.InkBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                displayedApps,
                                key = { "bathroom_${it.packageName}" }
                            ) { app ->
                                AppGridTile(
                                    app = app,
                                    isEditing = isEditingCabinet,
                                    isAssigned = app.packageName in assignedSet,
                                    isFavorite = app.packageName in favoritePackages,
                                    accentColor = FurnitureHotspotId.BATHROOM_CABINET.accentColor,
                                    onClick = {
                                        if (isEditingCabinet) {
                                            onToggleWellnessApp(app)
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
    }
}

private fun DrawScope.drawBathroomEnvironment(themePreset: HouseThemePreset) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.74f

    // Teal Bathroom Tile Wall
    drawRect(
        brush = Brush.verticalGradient(
            colors = SitcomPalette.wallGradientFor(
                themePreset,
                Color(0xFF284B4E),
                SitcomPalette.BathroomTileTeal
            ),
            startY = 0f,
            endY = floorY
        ),
        size = Size(w, floorY)
    )
    drawRect(color = SitcomPalette.WoodDark, topLeft = Offset(0f, floorY), size = Size(w, h - floorY))

    // Subtle Bathroom Tile Grid Lines
    for (tx in 1..10) {
        drawLine(
            color = SitcomPalette.InkBlack.copy(alpha = 0.22f),
            start = Offset(w * (tx / 11f), 0f),
            end = Offset(w * (tx / 11f), floorY),
            strokeWidth = 2f
        )
    }

    // Illuminated Arched Vanity Mirror on Left
    val mirLeft = w * 0.24f
    val mirTop = h * 0.22f
    val mirW = w * 0.16f
    val mirH = h * 0.38f
    drawRoundRect(
        color = SitcomPalette.LampGlowYellow,
        topLeft = Offset(mirLeft - 6f, mirTop - 6f),
        size = Size(mirW + 12f, mirH + 12f),
        cornerRadius = CornerRadius(80f, 80f),
        style = Stroke(width = 6f)
    )
    drawRoundRect(
        color = Color(0xFF639FAB),
        topLeft = Offset(mirLeft, mirTop),
        size = Size(mirW, mirH),
        cornerRadius = CornerRadius(76f, 76f)
    )
    drawRoundRect(
        color = SitcomPalette.InkBlack,
        topLeft = Offset(mirLeft, mirTop),
        size = Size(mirW, mirH),
        cornerRadius = CornerRadius(76f, 76f),
        style = Stroke(width = 5f)
    )

    // Ceramic Washbasin & Counter under Mirror
    val sinkLeft = w * 0.20f
    val sinkTop = h * 0.64f
    drawRoundRect(
        color = Color(0xFFF0F4F8),
        topLeft = Offset(sinkLeft, sinkTop),
        size = Size(w * 0.24f, 28f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = SitcomPalette.InkBlack,
        topLeft = Offset(sinkLeft, sinkTop),
        size = Size(w * 0.24f, 28f),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 4.5f)
    )

    // Hanging Green Bathroom Plants
    for (p in 0..4) {
        drawCircle(
            color = SitcomPalette.MutedGreen,
            radius = 12f,
            center = Offset(w * 0.22f + p * 22f, h * 0.16f + (p % 2) * 10f)
        )
    }
}
