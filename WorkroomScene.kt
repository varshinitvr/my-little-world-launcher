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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.mylittleworld.launcher.model.CatPoseState
import com.mylittleworld.launcher.model.GirlPoseState
import com.mylittleworld.launcher.model.HouseThemePreset
import com.mylittleworld.launcher.model.InstalledApp
import com.mylittleworld.launcher.model.WorkroomSubCategory
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawProtagonistGirl
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawTuxedoCat
import com.mylittleworld.launcher.ui.characters.CompanionTouchTarget
import com.mylittleworld.launcher.ui.theme.SitcomPalette

/**
 * ROOM 2: WORKROOM — WORKBENCH & SORTED APPS
 *
 * Matches the middle-right panel of the user's reference mockup:
 * - Left Side: Illustrated Workbench with Dual Monitors, "IDEAS SHOOTS EDIT REPEAT" poster,
 *   green alien wall decal, shelves, Girl typing at the desk, and Cat perched nearby.
 * - Right Side: Floating "Work Apps" Organized Dock with the 4 required categories:
 *   1. AI & Research (ChatGPT, Claude, Gemini)
 *   2. Work & Files (Google Drive, Chrome, Microsoft Teams, File Manager, Downloads, Scanner)
 *   3. Creative Tools / Editing (Design, image creation, video editing)
 *   4. Notes & Planning (Notion, Calendar, Keep, Notes)
 * - Includes category customization so you can assign/unassign any installed app.
 */
@Composable
fun WorkroomScene(
    themePreset: HouseThemePreset,
    showGirl: Boolean,
    showPets: Boolean,
    girlPose: GirlPoseState,
    catPose: CatPoseState,
    allApps: List<InstalledApp>,
    favoritePackages: Set<String>,
    getAppsForSubCategory: (WorkroomSubCategory) -> List<InstalledApp>,
    onToggleAppInSubCategory: (WorkroomSubCategory, InstalledApp) -> Unit,
    onLaunchApp: (InstalledApp) -> Unit,
    onToggleFavorite: (InstalledApp) -> Unit,
    onGirlClick: () -> Unit,
    onCatClick: () -> Unit,
    onBackToBedroom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingCategory by remember { mutableStateOf<WorkroomSubCategory?>(null) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        // LAYER 1, 2 & 3: Workroom Workbench, Dual Monitors, Posters & Characters
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawWorkroomWorkbenchScene(themePreset)

            if (showGirl) {
                drawProtagonistGirl(
                    pose = GirlPoseState.WORK_AT_DESK,
                    centerX = size.width * 0.28f,
                    centerY = size.height * 0.56f,
                    breathPulse = 1.0f,
                    facingRight = true
                )
            }
            if (showPets) {
                drawTuxedoCat(
                    pose = catPose,
                    centerX = size.width * 0.16f,
                    centerY = size.height * 0.52f,
                    pulse = 1.0f
                )
            }
        }

        // Top Breadcrumb Pill
        RoomBreadcrumbPill(
            title = "Workroom – Sorted Apps",
            onBackToBedroom = onBackToBedroom,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 88.dp, top = 74.dp)
        )

        // Character & Cat Touch Zones on Workbench
        if (showGirl) {
            CompanionTouchTarget(
                title = "Working at Desk",
                subtitle = girlPose.speechBubble,
                isPet = false,
                xOffset = w * 0.20f,
                yOffset = h * 0.42f,
                width = w * 0.16f,
                height = h * 0.26f,
                onClick = onGirlClick
            )
        }
        if (showPets) {
            CompanionTouchTarget(
                title = "Desk Cat",
                subtitle = catPose.reactionText,
                isPet = true,
                xOffset = w * 0.11f,
                yOffset = h * 0.44f,
                width = w * 0.12f,
                height = h * 0.14f,
                onClick = onCatClick
            )
        }

        // LAYER 4: Organized "Work Apps" Floating Panel on the Right Wing (matching reference image)
        Surface(
            color = SitcomPalette.CharcoalPanel,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp, top = 68.dp, bottom = 86.dp)
                .width(w * 0.48f)
                .fillMaxHeight()
                .border(3.dp, SitcomPalette.InkBlack, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Work Apps",
                            color = SitcomPalette.TextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (editingCategory != null)
                                "Tap apps below to add/remove from ${editingCategory!!.title}"
                            else
                                "Tap any app to launch • Tap ✏️ on a shelf to customize",
                            color = SitcomPalette.TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(WorkroomSubCategory.entries) { subCat ->
                        val isEditingThis = editingCategory == subCat
                        val assignedApps = getAppsForSubCategory(subCat)
                        val assignedSet = remember(assignedApps) { assignedApps.map { it.packageName }.toSet() }
                        val rowApps = if (isEditingThis) allApps else assignedApps

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SitcomPalette.InkBlack.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                                .border(1.5.dp, subCat.accentColor.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = subCat.title,
                                        color = subCat.accentColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = subCat.subtitle,
                                        color = SitcomPalette.TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        editingCategory = if (isEditingThis) null else subCat
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Customize Category",
                                        tint = if (isEditingThis) SitcomPalette.WarmYellow else SitcomPalette.TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                items(rowApps, key = { "${subCat.id}_${it.packageName}" }) { app ->
                                    AppGridTile(
                                        app = app,
                                        isEditing = isEditingThis,
                                        isAssigned = app.packageName in assignedSet,
                                        isFavorite = app.packageName in favoritePackages,
                                        accentColor = subCat.accentColor,
                                        onClick = {
                                            if (isEditingThis) {
                                                onToggleAppInSubCategory(subCat, app)
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
}

private fun DrawScope.drawWorkroomWorkbenchScene(themePreset: HouseThemePreset) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.72f

    // Wall & Floor
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
    drawRect(
        color = SitcomPalette.WoodDark,
        topLeft = Offset(0f, floorY),
        size = Size(w, h - floorY)
    )

    // "IDEAS SHOOTS EDIT REPEAT" Yellow Wall Poster
    val posterLeft = w * 0.25f
    val posterTop = h * 0.14f
    val posterW = w * 0.10f
    val posterH = h * 0.22f
    drawRoundRect(
        color = SitcomPalette.WarmYellow,
        topLeft = Offset(posterLeft, posterTop),
        size = Size(posterW, posterH),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = SitcomPalette.InkBlack,
        topLeft = Offset(posterLeft, posterTop),
        size = Size(posterW, posterH),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 4.5f)
    )
    for (r in 0..3) {
        drawLine(
            color = SitcomPalette.InkBlack,
            start = Offset(posterLeft + 14f, posterTop + 22f + r * 18f),
            end = Offset(posterLeft + posterW - 14f, posterTop + 22f + r * 18f),
            strokeWidth = 4.5f
        )
    }

    // Green Sci-Fi Alien Decal on Left Wall
    val alienCenter = Offset(w * 0.17f, h * 0.28f)
    drawCircle(color = SitcomPalette.PortalNeonGreen, radius = 22f, center = alienCenter)
    drawCircle(color = SitcomPalette.InkBlack, radius = 22f, center = alienCenter, style = Stroke(width = 4f))
    drawCircle(color = SitcomPalette.InkBlack, radius = 5f, center = Offset(alienCenter.x - 7f, alienCenter.y))
    drawCircle(color = SitcomPalette.InkBlack, radius = 5f, center = Offset(alienCenter.x + 7f, alienCenter.y))

    // Wide Workbench Desk & Dual Widescreen Monitors
    val deskLeft = w * 0.10f
    val deskTop = h * 0.56f
    val deskW = w * 0.38f

    // Monitor 1 (Left) & Monitor 2 (Right)
    for (m in 0..1) {
        val mw = deskW * 0.40f
        val mh = h * 0.15f
        val mx = deskLeft + 24f + m * (mw + 18f)
        val my = deskTop - mh - 12f
        drawRoundRect(
            color = SitcomPalette.CharcoalGrey,
            topLeft = Offset(mx, my),
            size = Size(mw, mh),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = if (m == 0) SitcomPalette.TealAccent.copy(alpha = 0.35f) else SitcomPalette.WarmOrange.copy(alpha = 0.35f),
            topLeft = Offset(mx + 6f, my + 6f),
            size = Size(mw - 12f, mh - 12f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = SitcomPalette.InkBlack,
            topLeft = Offset(mx, my),
            size = Size(mw, mh),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(width = 4.5f)
        )
    }

    // Desk Surface
    drawRoundRect(
        color = SitcomPalette.WoodWarm,
        topLeft = Offset(deskLeft, deskTop),
        size = Size(deskW, 22f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = SitcomPalette.InkBlack,
        topLeft = Offset(deskLeft, deskTop),
        size = Size(deskW, 22f),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 4.5f)
    )
}
