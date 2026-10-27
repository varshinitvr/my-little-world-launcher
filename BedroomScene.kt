package com.mylittleworld.launcher.ui.rooms

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.DoorSliding
import androidx.compose.material.icons.filled.Light
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mylittleworld.launcher.model.CatPoseState
import com.mylittleworld.launcher.model.DogPoseState
import com.mylittleworld.launcher.model.FurnitureHotspotId
import com.mylittleworld.launcher.model.GirlPoseState
import com.mylittleworld.launcher.model.HouseThemePreset
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawGoldenDog
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawProtagonistGirl
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawTuxedoCat
import com.mylittleworld.launcher.ui.characters.CompanionTouchTarget
import com.mylittleworld.launcher.ui.theme.SitcomPalette

/**
 * ROOM 1: THE BEDROOM — MAIN HOME SCREEN
 *
 * Includes the bed, study desk, stickered laptop, bedside table & lamp, messy wardrobe,
 * "SAME GIRL DIFFERENT PLANS" poster, plants, clutter, glowing green sci-fi portal,
 * and independent animated Girl, Cat, and Dog layers.
 */
@Composable
fun BedroomScene(
    masterAnimationEnabled: Boolean,
    themePreset: HouseThemePreset,
    showGirl: Boolean,
    showPets: Boolean,
    girlPose: GirlPoseState,
    catPose: CatPoseState,
    dogPose: DogPoseState,
    isWalkingToRoom: Boolean,
    walkProgress: Float,
    customNoteText: String,
    onHotspotClick: (FurnitureHotspotId) -> Unit,
    onGirlClick: () -> Unit,
    onCatClick: () -> Unit,
    onDogClick: () -> Unit,
    onNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val portalAngle: Float
    val lampPulse: Float
    if (masterAnimationEnabled) {
        val infinite = rememberInfiniteTransition(label = "BedroomAmbient")
        val animAngle by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "PortalSpin"
        )
        val animPulse by infinite.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "LampGlow"
        )
        portalAngle = animAngle
        lampPulse = animPulse
    } else {
        portalAngle = 0f
        lampPulse = 1.0f
    }

    val targetXFraction = when {
        isWalkingToRoom -> 0.48f + (walkProgress * 0.42f)
        girlPose == GirlPoseState.WORK_AT_DESK -> 0.22f
        girlPose == GirlPoseState.WALKING -> 0.65f
        else -> 0.48f
    }

    val girlXFraction by animateFloatAsState(
        targetValue = targetXFraction,
        animationSpec = if (masterAnimationEnabled) tween(450) else tween(0),
        label = "GirlBedroomX"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBedroomBackground(themePreset)
            drawBedroomPostersAndPortal(portalAngle = portalAngle, lampPulse = lampPulse)
            drawStudyDeskAndBookshelf()
            drawWardrobeAndClutter()
            drawBedsideTableAndLamp(lampPulse = lampPulse)
            drawBedAndBedding()

            if (showGirl) {
                drawProtagonistGirl(
                    pose = if (isWalkingToRoom) GirlPoseState.WALKING else girlPose,
                    centerX = size.width * girlXFraction,
                    centerY = size.height * 0.49f,
                    breathPulse = lampPulse,
                    walkPhase = walkProgress * 3f,
                    facingRight = true
                )
            }
            if (showPets) {
                val catX = if (catPose == CatPoseState.FOLLOW) size.width * (girlXFraction + 0.08f) else size.width * 0.57f
                val dogX = if (dogPose == DogPoseState.FOLLOW) size.width * (girlXFraction + 0.14f) else size.width * 0.79f
                drawTuxedoCat(
                    pose = catPose,
                    centerX = catX.coerceIn(size.width * 0.15f, size.width * 0.88f),
                    centerY = size.height * 0.59f,
                    pulse = lampPulse
                )
                drawGoldenDog(
                    pose = dogPose,
                    centerX = dogX.coerceIn(size.width * 0.20f, size.width * 0.90f),
                    centerY = size.height * 0.74f,
                    pulse = lampPulse
                )
            }
        }

        // Interactive Furniture Hotspots
        FurnitureHotspotBox(
            title = "Study Desk",
            subtitle = "Notes & Docs",
            icon = Icons.Default.DesktopWindows,
            accentColor = FurnitureHotspotId.STUDY_DESK.accentColor,
            xOffset = w * 0.09f,
            yOffset = h * 0.44f,
            width = w * 0.19f,
            height = h * 0.21f,
            onClick = { onHotspotClick(FurnitureHotspotId.STUDY_DESK) }
        )

        FurnitureHotspotBox(
            title = "Laptop",
            subtitle = "AI & Research",
            icon = Icons.Default.Computer,
            accentColor = FurnitureHotspotId.LAPTOP_ON_BED.accentColor,
            xOffset = w * 0.33f,
            yOffset = h * 0.48f,
            width = w * 0.22f,
            height = h * 0.20f,
            onClick = { onHotspotClick(FurnitureHotspotId.LAPTOP_ON_BED) }
        )

        FurnitureHotspotBox(
            title = "Portal",
            subtitle = "Room Map",
            icon = Icons.Default.Light,
            accentColor = FurnitureHotspotId.GREEN_PORTAL.accentColor,
            xOffset = w * 0.62f,
            yOffset = h * 0.14f,
            width = w * 0.15f,
            height = h * 0.24f,
            onClick = { onHotspotClick(FurnitureHotspotId.GREEN_PORTAL) }
        )

        FurnitureHotspotBox(
            title = "Wardrobe",
            subtitle = "Style & Media",
            icon = Icons.Default.DoorSliding,
            accentColor = FurnitureHotspotId.WARDROBE_CLOSET.accentColor,
            xOffset = w * 0.78f,
            yOffset = h * 0.20f,
            width = w * 0.19f,
            height = h * 0.44f,
            onClick = { onHotspotClick(FurnitureHotspotId.WARDROBE_CLOSET) }
        )

        FurnitureHotspotBox(
            title = "Bedside",
            subtitle = "Clock & Sleep",
            icon = Icons.Default.Light,
            accentColor = FurnitureHotspotId.BEDSIDE_TABLE.accentColor,
            xOffset = w * 0.67f,
            yOffset = h * 0.44f,
            width = w * 0.12f,
            height = h * 0.18f,
            onClick = { onHotspotClick(FurnitureHotspotId.BEDSIDE_TABLE) }
        )

        // Character & Pet Touch Targets
        if (showGirl) {
            CompanionTouchTarget(
                title = "Me (${girlPose.label})",
                subtitle = girlPose.speechBubble,
                isPet = false,
                xOffset = w * (girlXFraction - 0.08f).coerceIn(0.08f, 0.80f),
                yOffset = h * 0.33f,
                width = w * 0.17f,
                height = h * 0.22f,
                onClick = onGirlClick
            )
        }

        if (showPets) {
            CompanionTouchTarget(
                title = "Cat (${catPose.label})",
                subtitle = catPose.reactionText,
                isPet = true,
                xOffset = w * 0.51f,
                yOffset = h * 0.55f,
                width = w * 0.14f,
                height = h * 0.15f,
                onClick = onCatClick
            )
            CompanionTouchTarget(
                title = "Dog (${dogPose.label})",
                subtitle = dogPose.reactionText,
                isPet = true,
                xOffset = w * 0.71f,
                yOffset = h * 0.65f,
                width = w * 0.18f,
                height = h * 0.18f,
                onClick = onDogClick
            )
        }

        // Cozy Sticky Note Card ("A better me lives here. ♥")
        Surface(
            onClick = onNoteClick,
            color = SitcomPalette.CreamNote,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 88.dp, bottom = 90.dp)
                .border(2.5.dp, SitcomPalette.InkBlack, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                Text(
                    text = customNoteText,
                    color = SitcomPalette.InkBlack,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap to customize note & widgets • ${girlPose.label}",
                    color = SitcomPalette.WoodDark,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

private fun DrawScope.drawBedroomBackground(themePreset: HouseThemePreset) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.68f

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
        brush = Brush.verticalGradient(
            colors = listOf(SitcomPalette.WoodWarm, SitcomPalette.WoodDark),
            startY = floorY,
            endY = h
        ),
        topLeft = Offset(0f, floorY),
        size = Size(w, h - floorY)
    )

    drawRect(
        color = SitcomPalette.WoodDark,
        topLeft = Offset(0f, floorY - 14f),
        size = Size(w, 14f)
    )
    drawLine(color = SitcomPalette.InkBlack, start = Offset(0f, floorY - 14f), end = Offset(w, floorY - 14f), strokeWidth = 5f)
    drawLine(color = SitcomPalette.InkBlack, start = Offset(0f, floorY), end = Offset(w, floorY), strokeWidth = 5f)

    for (i in 1..7) {
        val xTop = w * (i / 8f)
        val xBottom = w * ((i - 3.5f) * 0.22f + 0.5f)
        drawLine(
            color = SitcomPalette.InkBlack.copy(alpha = 0.45f),
            start = Offset(xTop, floorY),
            end = Offset(xBottom, h),
            strokeWidth = 3f
        )
    }

    // Terracotta Rug
    val rugTopLeft = Offset(w * 0.25f, floorY + h * 0.04f)
    val rugSize = Size(w * 0.64f, h * 0.22f)
    drawRoundRect(
        color = SitcomPalette.RugTerracotta,
        topLeft = rugTopLeft,
        size = rugSize,
        cornerRadius = CornerRadius(28f, 28f)
    )
    drawRoundRect(
        color = SitcomPalette.WarmYellow.copy(alpha = 0.7f),
        topLeft = Offset(rugTopLeft.x + 16f, rugTopLeft.y + 12f),
        size = Size(rugSize.width - 32f, rugSize.height - 24f),
        cornerRadius = CornerRadius(20f, 20f),
        style = Stroke(width = 4f)
    )
    drawRoundRect(
        color = SitcomPalette.InkBlack,
        topLeft = rugTopLeft,
        size = rugSize,
        cornerRadius = CornerRadius(28f, 28f),
        style = Stroke(width = 5f)
    )

    // Night Window with City Skyline
    val winLeft = w * 0.24f
    val winTop = h * 0.08f
    val winW = w * 0.16f
    val winH = h * 0.34f
    drawRoundRect(
        color = SitcomPalette.NightSkyDeep,
        topLeft = Offset(winLeft, winTop),
        size = Size(winW, winH),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawCircle(
        color = SitcomPalette.LampGlowYellow,
        radius = winW * 0.11f,
        center = Offset(winLeft + winW * 0.68f, winTop + winH * 0.24f)
    )
    val buildingWidths = listOf(0.22f, 0.28f, 0.24f, 0.26f)
    val buildingHeights = listOf(0.42f, 0.55f, 0.36f, 0.48f)
    var curX = winLeft + 6f
    for (idx in buildingWidths.indices) {
        val bw = (winW - 12f) * buildingWidths[idx]
        val bh = winH * buildingHeights[idx]
        val by = winTop + winH - bh
        drawRect(color = Color(0xFF1E2942), topLeft = Offset(curX, by), size = Size(bw, bh))
        drawRect(color = SitcomPalette.WarmYellow, topLeft = Offset(curX + bw * 0.25f, by + bh * 0.2f), size = Size(bw * 0.2f, 6f))
        drawRect(color = SitcomPalette.WarmOrange, topLeft = Offset(curX + bw * 0.55f, by + bh * 0.45f), size = Size(bw * 0.2f, 6f))
        curX += bw
    }
    drawRoundRect(
        color = SitcomPalette.InkBlack,
        topLeft = Offset(winLeft, winTop),
        size = Size(winW, winH),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 7f)
    )
    drawRoundRect(
        color = SitcomPalette.WarmOrange,
        topLeft = Offset(winLeft - 14f, winTop - 6f),
        size = Size(22f, winH + 16f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = SitcomPalette.WarmOrange,
        topLeft = Offset(winLeft + winW - 8f, winTop - 6f),
        size = Size(22f, winH + 16f),
        cornerRadius = CornerRadius(10f, 10f)
    )
}

private fun DrawScope.drawBedroomPostersAndPortal(portalAngle: Float, lampPulse: Float) {
    val w = size.width
    val h = size.height

    val posterLeft = w * 0.47f
    val posterTop = h * 0.14f
    val posterW = w * 0.11f
    val posterH = h * 0.22f
    drawRoundRect(
        color = SitcomPalette.CreamNote,
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
    for (line in 0..3) {
        drawLine(
            color = SitcomPalette.InkBlack,
            start = Offset(posterLeft + posterW * 0.18f, posterTop + posterH * (0.24f + line * 0.14f)),
            end = Offset(posterLeft + posterW * 0.82f, posterTop + posterH * (0.24f + line * 0.14f)),
            strokeWidth = 4f
        )
    }

    val portalCenter = Offset(w * 0.69f, h * 0.26f)
    val portalRadius = h * 0.11f * lampPulse
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                SitcomPalette.PortalLimeCore,
                SitcomPalette.PortalNeonGreen,
                Color(0xFF1B5E20).copy(alpha = 0.6f),
                Color.Transparent
            ),
            center = portalCenter,
            radius = portalRadius * 1.35f
        ),
        radius = portalRadius * 1.35f,
        center = portalCenter
    )
    drawCircle(color = SitcomPalette.PortalNeonGreen, radius = portalRadius, center = portalCenter)
    drawCircle(color = SitcomPalette.InkBlack, radius = portalRadius, center = portalCenter, style = Stroke(width = 5f))

    rotate(degrees = portalAngle, pivot = portalCenter) {
        drawArc(
            color = SitcomPalette.PortalLimeCore,
            startAngle = 15f,
            sweepAngle = 210f,
            useCenter = false,
            topLeft = Offset(portalCenter.x - portalRadius * 0.7f, portalCenter.y - portalRadius * 0.7f),
            size = Size(portalRadius * 1.4f, portalRadius * 1.4f),
            style = Stroke(width = 6f)
        )
        drawArc(
            color = SitcomPalette.InkBlack,
            startAngle = 140f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(portalCenter.x - portalRadius * 0.42f, portalCenter.y - portalRadius * 0.42f),
            size = Size(portalRadius * 0.84f, portalRadius * 0.84f),
            style = Stroke(width = 4.5f)
        )
    }
}

private fun DrawScope.drawStudyDeskAndBookshelf() {
    val w = size.width
    val h = size.height
    val floorY = h * 0.68f

    val shelfLeft = w * 0.09f
    val shelfTop = h * 0.25f
    val shelfW = w * 0.13f
    drawRect(color = SitcomPalette.WoodDark, topLeft = Offset(shelfLeft, shelfTop), size = Size(shelfW, 12f))
    drawRect(color = SitcomPalette.InkBlack, topLeft = Offset(shelfLeft, shelfTop), size = Size(shelfW, 12f), style = Stroke(width = 4f))

    val deskLeft = w * 0.09f
    val deskTop = h * 0.54f
    val deskW = w * 0.20f
    val deskH = floorY - deskTop
    drawRect(color = SitcomPalette.WoodWarm, topLeft = Offset(deskLeft, deskTop), size = Size(deskW, 18f))
    drawRect(color = SitcomPalette.InkBlack, topLeft = Offset(deskLeft, deskTop), size = Size(deskW, 18f), style = Stroke(width = 4.5f))
    drawRect(color = SitcomPalette.WoodDark, topLeft = Offset(deskLeft + 8f, deskTop + 18f), size = Size(deskW * 0.42f, deskH - 18f))
    drawRect(color = SitcomPalette.InkBlack, topLeft = Offset(deskLeft + 8f, deskTop + 18f), size = Size(deskW * 0.42f, deskH - 18f), style = Stroke(width = 4f))

    val monW = deskW * 0.48f
    val monH = h * 0.09f
    val monLeft = deskLeft + deskW * 0.25f
    val monTop = deskTop - monH - 8f
    drawRoundRect(color = SitcomPalette.CharcoalGrey, topLeft = Offset(monLeft, monTop), size = Size(monW, monH), cornerRadius = CornerRadius(8f, 8f))
    drawRoundRect(color = SitcomPalette.TealAccent.copy(alpha = 0.35f), topLeft = Offset(monLeft + 6f, monTop + 6f), size = Size(monW - 12f, monH - 12f), cornerRadius = CornerRadius(4f, 4f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(monLeft, monTop), size = Size(monW, monH), cornerRadius = CornerRadius(8f, 8f), style = Stroke(width = 4.5f))
}

private fun DrawScope.drawWardrobeAndClutter() {
    val w = size.width
    val h = size.height
    val floorY = h * 0.68f
    val wardLeft = w * 0.79f
    val wardTop = h * 0.16f
    val wardW = w * 0.18f
    val wardH = floorY - wardTop

    drawRoundRect(color = SitcomPalette.WoodDark, topLeft = Offset(wardLeft, wardTop), size = Size(wardW, wardH), cornerRadius = CornerRadius(10f, 10f))
    drawRect(color = Color(0xFF2B1910), topLeft = Offset(wardLeft + 10f, wardTop + 12f), size = Size(wardW - 20f, wardH - 24f))
    val hangerColors = listOf(SitcomPalette.WarmOrange, SitcomPalette.MutedGreen, SitcomPalette.TealAccent, SitcomPalette.WarmYellow)
    for (c in hangerColors.indices) {
        val cx = wardLeft + 20f + c * ((wardW - 48f) / 4f)
        drawRoundRect(color = hangerColors[c], topLeft = Offset(cx, wardTop + 36f), size = Size(20f, wardH * 0.38f), cornerRadius = CornerRadius(6f, 6f))
        drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(cx, wardTop + 36f), size = Size(20f, wardH * 0.38f), cornerRadius = CornerRadius(6f, 6f), style = Stroke(width = 3f))
    }
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(wardLeft, wardTop), size = Size(wardW, wardH), cornerRadius = CornerRadius(10f, 10f), style = Stroke(width = 5.5f))
}

private fun DrawScope.drawBedsideTableAndLamp(lampPulse: Float) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.68f
    val tableLeft = w * 0.68f
    val tableTop = h * 0.53f
    val tableW = w * 0.09f
    val tableH = floorY - tableTop
    val lampCenter = Offset(tableLeft + tableW * 0.5f, tableTop - h * 0.05f)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                SitcomPalette.LampGlowYellow.copy(alpha = 0.55f * lampPulse),
                SitcomPalette.WarmOrange.copy(alpha = 0.2f * lampPulse),
                Color.Transparent
            ),
            center = lampCenter,
            radius = h * 0.16f * lampPulse
        ),
        radius = h * 0.16f * lampPulse,
        center = lampCenter
    )
    drawRoundRect(color = SitcomPalette.WoodWarm, topLeft = Offset(tableLeft, tableTop), size = Size(tableW, tableH), cornerRadius = CornerRadius(8f, 8f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(tableLeft, tableTop), size = Size(tableW, tableH), cornerRadius = CornerRadius(8f, 8f), style = Stroke(width = 4.5f))

    val shadePath = Path().apply {
        moveTo(lampCenter.x - 18f, lampCenter.y - 16f)
        lineTo(lampCenter.x + 18f, lampCenter.y - 16f)
        lineTo(lampCenter.x + 28f, lampCenter.y + 14f)
        lineTo(lampCenter.x - 28f, lampCenter.y + 14f)
        close()
    }
    drawPath(path = shadePath, color = SitcomPalette.WarmYellow)
    drawPath(path = shadePath, color = SitcomPalette.InkBlack, style = Stroke(width = 4f))
}

private fun DrawScope.drawBedAndBedding() {
    val w = size.width
    val h = size.height
    val floorY = h * 0.68f
    val bedLeft = w * 0.28f
    val bedTop = h * 0.46f
    val bedW = w * 0.40f
    val bedH = floorY - bedTop + h * 0.06f

    drawRoundRect(color = SitcomPalette.WoodDark, topLeft = Offset(bedLeft + 12f, bedTop - h * 0.08f), size = Size(bedW * 0.55f, h * 0.12f), cornerRadius = CornerRadius(14f, 14f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(bedLeft + 12f, bedTop - h * 0.08f), size = Size(bedW * 0.55f, h * 0.12f), cornerRadius = CornerRadius(14f, 14f), style = Stroke(width = 5f))
    drawRoundRect(color = Color(0xFF3E6B89), topLeft = Offset(bedLeft, bedTop), size = Size(bedW, bedH), cornerRadius = CornerRadius(22f, 22f))
    drawRoundRect(color = SitcomPalette.WarmOrange, topLeft = Offset(bedLeft + bedW * 0.08f, bedTop + bedH * 0.25f), size = Size(bedW * 0.84f, bedH * 0.55f), cornerRadius = CornerRadius(18f, 18f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(bedLeft, bedTop), size = Size(bedW, bedH), cornerRadius = CornerRadius(22f, 22f), style = Stroke(width = 5.5f))
}
