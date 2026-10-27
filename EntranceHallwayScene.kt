package com.mylittleworld.launcher.ui.rooms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.mylittleworld.launcher.ui.characters.CompanionTouchTarget
import com.mylittleworld.launcher.ui.theme.SitcomPalette

/**
 * ROOM 6: ENTRANCE / HALLWAY — MAILBOX & EVERYDAY ESSENTIALS
 *
 * Matches the bottom-center panel of the reference mockup:
 * - Warm wooden hallway with Front Door, Coat Hooks, Hanging Backpacks, Keys, Shoe Rack with
 *   sneakers at the bottom, and the "GOOD THINGS AHEAD" wall poster.
 * - Girl wearing her stickered backpack standing by the Hallway Wall Organizer / Mailbox.
 * - Organized Wall Mailbox Board containing Gmail, WhatsApp, Messages, Phone, Contacts,
 *   Instagram, Google Maps, Ola, Rapido, Teams, DigiYatra, and other installed communication/travel apps.
 */
@Composable
fun EntranceHallwayScene(
    themePreset: HouseThemePreset,
    showGirl: Boolean,
    girlPose: GirlPoseState,
    mailboxApps: List<InstalledApp>,
    allApps: List<InstalledApp>,
    favoritePackages: Set<String>,
    onToggleMailboxApp: (InstalledApp) -> Unit,
    onLaunchApp: (InstalledApp) -> Unit,
    onToggleFavorite: (InstalledApp) -> Unit,
    onGirlClick: () -> Unit,
    onBackToBedroom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingMailbox by remember { mutableStateOf(false) }
    val assignedSet = remember(mailboxApps) { mailboxApps.map { it.packageName }.toSet() }
    val displayedApps = if (isEditingMailbox) allApps else mailboxApps

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        // LAYER 1, 2 & 3: Hallway Wood Paneling, Coat Rack, "GOOD THINGS AHEAD" sign, Shoe Rack & Girl with Backpack
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawEntranceHallwayEnvironment(themePreset)

            if (showGirl) {
                drawProtagonistGirl(
                    pose = GirlPoseState.HALLWAY_BACKPACK,
                    centerX = size.width * 0.28f,
                    centerY = size.height * 0.54f,
                    breathPulse = 1.0f,
                    facingRight = true
                )
            }
        }

        // Top Breadcrumb Pill
        RoomBreadcrumbPill(
            title = "Entrance / Hallway – Mailbox",
            onBackToBedroom = onBackToBedroom,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 88.dp, top = 74.dp)
        )

        if (showGirl) {
            CompanionTouchTarget(
                title = "Heading Out",
                subtitle = GirlPoseState.HALLWAY_BACKPACK.speechBubble,
                isPet = false,
                xOffset = w * 0.19f,
                yOffset = h * 0.38f,
                width = w * 0.17f,
                height = h * 0.28f,
                onClick = onGirlClick
            )
        }

        // LAYER 4: Wall-Mounted Wooden Mailbox & Everyday Essentials Organizer Board
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 32.dp, top = 112.dp)
                .width(w * 0.46f)
                .height(h * 0.56f)
        ) {
            Surface(
                color = Color(0xFF2B2623),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .border(6.dp, SitcomPalette.WoodWarm, RoundedCornerShape(18.dp))
            ) {
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
                        Column {
                            Text(
                                text = if (isEditingMailbox) "Customize Mailbox & Travel Apps" else "Mailbox • Comms, Maps & Rides",
                                color = SitcomPalette.WarmYellow,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Gmail, WhatsApp, Phone, Maps, Ola, Rapido & Essentials",
                                color = SitcomPalette.TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                        IconButton(
                            onClick = { isEditingMailbox = !isEditingMailbox },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Customize Mailbox",
                                tint = if (isEditingMailbox) SitcomPalette.WarmYellow else SitcomPalette.TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            displayedApps,
                            key = { "entrance_${it.packageName}" }
                        ) { app ->
                            AppGridTile(
                                app = app,
                                isEditing = isEditingMailbox,
                                isAssigned = app.packageName in assignedSet,
                                isFavorite = app.packageName in favoritePackages,
                                accentColor = FurnitureHotspotId.ENTRANCE_MAILBOX.accentColor,
                                onClick = {
                                    if (isEditingMailbox) {
                                        onToggleMailboxApp(app)
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

private fun DrawScope.drawEntranceHallwayEnvironment(themePreset: HouseThemePreset) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.74f

    // Warm Ochre / Wood Hallway Wall
    drawRect(
        brush = Brush.verticalGradient(
            colors = SitcomPalette.wallGradientFor(
                themePreset,
                Color(0xFF4F3824),
                SitcomPalette.HallwayWarmOchre
            ),
            startY = 0f,
            endY = floorY
        ),
        size = Size(w, floorY)
    )
    drawRect(color = SitcomPalette.WoodDark, topLeft = Offset(0f, floorY), size = Size(w, h - floorY))

    // Front Door on Left Edge
    val doorLeft = w * 0.08f
    val doorTop = h * 0.14f
    val doorW = w * 0.13f
    val doorH = floorY - doorTop
    drawRoundRect(color = SitcomPalette.WoodDark, topLeft = Offset(doorLeft, doorTop), size = Size(doorW, doorH), cornerRadius = CornerRadius(8f, 8f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(doorLeft, doorTop), size = Size(doorW, doorH), cornerRadius = CornerRadius(8f, 8f), style = Stroke(width = 5f))
    // Brass Doorknob & Key Hooks
    drawCircle(color = SitcomPalette.WarmYellow, radius = 7f, center = Offset(doorLeft + doorW - 18f, doorTop + doorH * 0.52f))

    // "GOOD THINGS AHEAD" Framed Poster above Girl
    val signLeft = w * 0.25f
    val signTop = h * 0.16f
    val signW = w * 0.11f
    val signH = h * 0.18f
    drawRoundRect(color = SitcomPalette.CreamNote, topLeft = Offset(signLeft, signTop), size = Size(signW, signH), cornerRadius = CornerRadius(6f, 6f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(signLeft, signTop), size = Size(signW, signH), cornerRadius = CornerRadius(6f, 6f), style = Stroke(width = 4.5f))
    for (i in 0..2) {
        drawLine(
            color = SitcomPalette.InkBlack,
            start = Offset(signLeft + 14f, signTop + 20f + i * 16f),
            end = Offset(signLeft + signW - 14f, signTop + 20f + i * 16f),
            strokeWidth = 4.5f
        )
    }

    // Shoe Rack at Bottom Right with Sneakers
    val rackLeft = w * 0.44f
    val rackTop = floorY - 34f
    val rackW = w * 0.48f
    drawRoundRect(color = SitcomPalette.WoodWarm, topLeft = Offset(rackLeft, rackTop), size = Size(rackW, 42f), cornerRadius = CornerRadius(8f, 8f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(rackLeft, rackTop), size = Size(rackW, 42f), cornerRadius = CornerRadius(8f, 8f), style = Stroke(width = 4.5f))
    val shoeColors = listOf(Color.White, SitcomPalette.WarmOrange, SitcomPalette.TealAccent, SitcomPalette.WarmYellow, Color.White)
    for (s in shoeColors.indices) {
        val sx = rackLeft + 24f + s * 64f
        drawRoundRect(color = shoeColors[s], topLeft = Offset(sx, rackTop + 12f), size = Size(38f, 18f), cornerRadius = CornerRadius(8f, 8f))
        drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(sx, rackTop + 12f), size = Size(38f, 18f), cornerRadius = CornerRadius(8f, 8f), style = Stroke(width = 3f))
    }
}
