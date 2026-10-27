package com.mylittleworld.launcher.ui.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mylittleworld.launcher.model.CatPoseState
import com.mylittleworld.launcher.model.DogPoseState
import com.mylittleworld.launcher.model.GirlPoseState
import com.mylittleworld.launcher.ui.theme.SitcomPalette
import kotlin.math.sin

/**
 * Shared 2D layered cartoon rig for the Protagonist Girl, Tuxedo Cat, and Golden Dog.
 * Maintains 100% consistent character design across all 6 rooms while supporting:
 * - Room-specific poses (Walk, Laptop on Bed, Work at Dual Monitors, Watch TV with Popcorn,
 *   Cook at Stove, Get Ready in Bathroom Towel, Hallway Backpack)
 * - Cat animations (Walk, Stretch, Sleep, Play, Follow)
 * - Dog animations (Walk, Sit, Play, Sleep, Follow)
 * - Physical walking leg-swing interpolation when walking between connected rooms.
 */
object CharacterRigRenderer {

    fun DrawScope.drawProtagonistGirl(
        pose: GirlPoseState,
        centerX: Float,
        centerY: Float,
        breathPulse: Float,
        walkPhase: Float = 0f,
        facingRight: Boolean = true
    ) {
        val dir = if (facingRight) 1f else -1f
        val walkBob = if (pose == GirlPoseState.WALKING) sin(walkPhase * 6.28f) * 8f else 0f
        val cy = centerY + walkBob

        // 1. Backpack (drawn behind torso when in Entrance / Hallway pose)
        if (pose == GirlPoseState.HALLWAY_BACKPACK || pose == GirlPoseState.WALKING) {
            val packX = if (facingRight) centerX - 42f else centerX + 8f
            drawRoundRect(
                color = Color(0xFF2E3B4E),
                topLeft = Offset(packX, cy - 2f),
                size = Size(34f, 48f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            drawRoundRect(
                color = SitcomPalette.InkBlack,
                topLeft = Offset(packX, cy - 2f),
                size = Size(34f, 48f),
                cornerRadius = CornerRadius(10f, 10f),
                style = Stroke(width = 4.5f)
            )
            // Alien sticker on backpack
            drawCircle(
                color = SitcomPalette.PortalNeonGreen,
                radius = 5.5f,
                center = Offset(packX + 17f, cy + 20f)
            )
        }

        // 2. Legs when Walking or Standing in Kitchen / Hallway / Bathroom
        if (pose == GirlPoseState.WALKING ||
            pose == GirlPoseState.COOKING_STOVE ||
            pose == GirlPoseState.HALLWAY_BACKPACK
        ) {
            val legSwing = if (pose == GirlPoseState.WALKING) sin(walkPhase * 6.28f) * 16f else 0f
            // Left Leg
            drawLine(
                color = SitcomPalette.InkBlack,
                start = Offset(centerX - 12f, cy + 42f),
                end = Offset(centerX - 12f - legSwing, cy + 90f),
                strokeWidth = 11f
            )
            // Right Leg
            drawLine(
                color = SitcomPalette.InkBlack,
                start = Offset(centerX + 12f, cy + 42f),
                end = Offset(centerX + 12f + legSwing, cy + 90f),
                strokeWidth = 11f
            )
            // White Cartoon Sneakers
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(centerX - 24f - legSwing, cy + 84f),
                size = Size(24f, 12f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(centerX + 2f + legSwing, cy + 84f),
                size = Size(24f, 12f),
                cornerRadius = CornerRadius(6f, 6f)
            )
        }

        // 3. Messy Dark Top-Bun & Fluffy Hair Silhouette
        drawCircle(
            color = SitcomPalette.InkBlack,
            radius = 25f,
            center = Offset(centerX - 8f * dir, cy - 58f)
        )
        drawCircle(
            color = SitcomPalette.InkBlack,
            radius = 36f,
            center = Offset(centerX, cy - 28f)
        )

        // 4. Warm Skin Head & Ears
        drawCircle(
            color = Color(0xFFE5A982),
            radius = 30f,
            center = Offset(centerX + 2f * dir, cy - 26f)
        )

        // 5. Exaggerated Expressive Sitcom Eyes
        val leftEyeX = centerX - 9f + (4f * dir)
        val rightEyeX = centerX + 10f + (4f * dir)
        val eyeY = cy - 28f

        if (pose == GirlPoseState.RELAXING_ON_BED) {
            // Closed happy/sleepy arcs
            drawLine(
                color = SitcomPalette.InkBlack,
                start = Offset(leftEyeX - 7f, eyeY),
                end = Offset(leftEyeX + 7f, eyeY),
                strokeWidth = 3.5f
            )
            drawLine(
                color = SitcomPalette.InkBlack,
                start = Offset(rightEyeX - 7f, eyeY),
                end = Offset(rightEyeX + 7f, eyeY),
                strokeWidth = 3.5f
            )
        } else {
            drawCircle(color = Color.White, radius = 9.5f, center = Offset(leftEyeX, eyeY))
            drawCircle(color = Color.White, radius = 9.5f, center = Offset(rightEyeX, eyeY))
            drawCircle(
                color = SitcomPalette.InkBlack,
                radius = 9.5f,
                center = Offset(leftEyeX, eyeY),
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = SitcomPalette.InkBlack,
                radius = 9.5f,
                center = Offset(rightEyeX, eyeY),
                style = Stroke(width = 3f)
            )
            // Expressive Pupils
            drawCircle(
                color = SitcomPalette.InkBlack,
                radius = 3.8f,
                center = Offset(leftEyeX + 2f * dir, eyeY)
            )
            drawCircle(
                color = SitcomPalette.InkBlack,
                radius = 3.8f,
                center = Offset(rightEyeX + 2f * dir, eyeY)
            )
        }

        // Expressive Smile / Mouth
        drawArc(
            color = SitcomPalette.InkBlack,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(centerX - 6f + 3f * dir, cy - 16f),
            size = Size(14f, 8f),
            style = Stroke(width = 3f)
        )

        // 6. Outfit / Torso (White Towel Wrap in Bathroom, Oversized Graphic Tee / Hoodie elsewhere)
        val outfitColor = when (pose) {
            GirlPoseState.GETTING_READY_MIRROR -> Color(0xFFF5F7FA) // Bathroom towel wrap
            GirlPoseState.HALLWAY_BACKPACK -> Color(0xFF1F2735) // Dark hoodie
            else -> Color(0xFF232733) // Charcoal oversized graphic tee
        }

        drawRoundRect(
            color = outfitColor,
            topLeft = Offset(centerX - 34f, cy + 2f),
            size = Size(68f, 46f * breathPulse),
            cornerRadius = CornerRadius(14f, 14f)
        )
        drawRoundRect(
            color = SitcomPalette.InkBlack,
            topLeft = Offset(centerX - 34f, cy + 2f),
            size = Size(68f, 46f * breathPulse),
            cornerRadius = CornerRadius(14f, 14f),
            style = Stroke(width = 4.5f)
        )

        // 7. Pose-Specific Props (Laptop, Popcorn Bowl & Remote, Toothbrush, Spatula)
        when (pose) {
            GirlPoseState.LAPTOP_ON_BED -> {
                val lapLeft = centerX - 86f
                val lapTop = cy - 4f
                drawRoundRect(
                    color = Color(0xFF90A4AE),
                    topLeft = Offset(lapLeft, lapTop),
                    size = Size(74f, 50f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = SitcomPalette.InkBlack,
                    topLeft = Offset(lapLeft, lapTop),
                    size = Size(74f, 50f),
                    cornerRadius = CornerRadius(8f, 8f),
                    style = Stroke(width = 4.5f)
                )
                drawCircle(
                    color = SitcomPalette.PortalNeonGreen,
                    radius = 6.5f,
                    center = Offset(lapLeft + 22f, lapTop + 22f)
                )
                drawCircle(
                    color = SitcomPalette.WarmOrange,
                    radius = 6f,
                    center = Offset(lapLeft + 50f, lapTop + 28f)
                )
            }
            GirlPoseState.WATCH_TV_COUCH -> {
                // Striped Popcorn Bowl in lap + TV Remote in outstretched hand
                drawRoundRect(
                    color = SitcomPalette.WarmYellow,
                    topLeft = Offset(centerX + 12f, cy + 16f),
                    size = Size(34f, 24f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = SitcomPalette.InkBlack,
                    topLeft = Offset(centerX + 12f, cy + 16f),
                    size = Size(34f, 24f),
                    cornerRadius = CornerRadius(8f, 8f),
                    style = Stroke(width = 3.5f)
                )
                // Outstretched arm holding TV remote toward screen
                drawLine(
                    color = Color(0xFFE5A982),
                    start = Offset(centerX + 26f, cy + 12f),
                    end = Offset(centerX + 68f, cy - 2f),
                    strokeWidth = 9f
                )
                drawRoundRect(
                    color = SitcomPalette.InkBlack,
                    topLeft = Offset(centerX + 62f, cy - 10f),
                    size = Size(24f, 10f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
            GirlPoseState.GETTING_READY_MIRROR -> {
                // Holding toothbrush / skincare brush near cheek
                drawLine(
                    color = SitcomPalette.TealAccent,
                    start = Offset(centerX + 16f, cy - 12f),
                    end = Offset(centerX + 44f, cy - 4f),
                    strokeWidth = 5f
                )
            }
            else -> Unit
        }
    }

    /**
     * Renders the Tuxedo Cat in any of its 5 states (Walk, Stretch, Sleep, Play, Follow).
     */
    fun DrawScope.drawTuxedoCat(
        pose: CatPoseState,
        centerX: Float,
        centerY: Float,
        pulse: Float
    ) {
        val stretchLift = when (pose) {
            CatPoseState.STRETCH -> -12f
            CatPoseState.PLAY -> -8f
            else -> 0f
        }

        // Curved Tail
        drawArc(
            color = SitcomPalette.InkBlack,
            startAngle = 180f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(centerX + 12f, centerY - 28f + stretchLift),
            size = Size(28f, 28f),
            style = Stroke(width = 6f)
        )

        // Cat Body (Black & White Tuxedo)
        drawRoundRect(
            color = SitcomPalette.InkBlack,
            topLeft = Offset(centerX - 28f, centerY - 12f + stretchLift),
            size = Size(56f, 28f * pulse),
            cornerRadius = CornerRadius(14f, 14f)
        )
        // White Tuxedo Chest & Paws
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(centerX - 22f, centerY + 4f),
            size = Size(36f, 12f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Cat Head & Pointed Ears
        val headX = centerX - 18f
        val headY = centerY - 10f + stretchLift
        drawCircle(
            color = SitcomPalette.InkBlack,
            radius = 16f,
            center = Offset(headX, headY)
        )
        val earsPath = Path().apply {
            moveTo(headX - 13f, headY - 6f)
            lineTo(headX - 8f, headY - 22f)
            lineTo(headX - 2f, headY - 10f)
            moveTo(headX + 2f, headY - 10f)
            lineTo(headX + 8f, headY - 22f)
            lineTo(headX + 13f, headY - 6f)
            close()
        }
        drawPath(path = earsPath, color = SitcomPalette.InkBlack)

        // Bright Yellow Sitcom Cat Eyes
        if (pose != CatPoseState.SLEEP) {
            drawCircle(color = SitcomPalette.WarmYellow, radius = 3.6f, center = Offset(headX - 5f, headY - 1f))
            drawCircle(color = SitcomPalette.WarmYellow, radius = 3.6f, center = Offset(headX + 5f, headY - 1f))
        }
    }

    /**
     * Renders the Golden Dog in any of its 5 states (Walk, Sit, Play, Sleep, Follow).
     */
    fun DrawScope.drawGoldenDog(
        pose: DogPoseState,
        centerX: Float,
        centerY: Float,
        pulse: Float
    ) {
        val playOffset = when (pose) {
            DogPoseState.PLAY -> -14f
            DogPoseState.SIT -> -6f
            else -> 0f
        }

        // Golden Dog Body
        drawRoundRect(
            color = Color(0xFFD9984A),
            topLeft = Offset(centerX - 42f, centerY - 16f + playOffset),
            size = Size(84f, 40f * pulse),
            cornerRadius = CornerRadius(20f, 20f)
        )
        drawRoundRect(
            color = SitcomPalette.InkBlack,
            topLeft = Offset(centerX - 42f, centerY - 16f + playOffset),
            size = Size(84f, 40f * pulse),
            cornerRadius = CornerRadius(20f, 20f),
            style = Stroke(width = 4.5f)
        )

        // Dog Head & Floppy Ears
        val headX = centerX - 34f
        val headY = centerY - 18f + (playOffset * 0.5f)
        drawCircle(
            color = Color(0xFFD9984A),
            radius = 22f,
            center = Offset(headX, headY)
        )
        drawCircle(
            color = SitcomPalette.InkBlack,
            radius = 22f,
            center = Offset(headX, headY),
            style = Stroke(width = 4.5f)
        )
        // Floppy Ear
        drawRoundRect(
            color = Color(0xFFB3742B),
            topLeft = Offset(headX + 6f, headY - 6f),
            size = Size(12f, 22f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Nose & Eyes
        drawCircle(color = SitcomPalette.InkBlack, radius = 4.5f, center = Offset(headX - 14f, headY + 2f))
        if (pose != DogPoseState.SLEEP) {
            drawCircle(color = SitcomPalette.InkBlack, radius = 3.5f, center = Offset(headX - 4f, headY - 4f))
        }
    }
}

/**
 * Interactive touch overlay badge for Girl, Cat, and Dog so tapping them triggers immediate pose/state animations.
 */
@Composable
fun CompanionTouchTarget(
    title: String,
    subtitle: String,
    isPet: Boolean,
    xOffset: Dp,
    yOffset: Dp,
    width: Dp,
    height: Dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .size(width = width, height = height)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color(0xD914161D), RoundedCornerShape(10.dp))
                .border(
                    width = 1.5.dp,
                    color = if (isPet) SitcomPalette.WarmYellow else SitcomPalette.PortalNeonGreen,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = if (isPet) Icons.Default.Pets else Icons.Default.SelfImprovement,
                contentDescription = title,
                tint = if (isPet) SitcomPalette.WarmYellow else SitcomPalette.PortalNeonGreen,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = title,
                    color = SitcomPalette.TextPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    color = SitcomPalette.TextSecondary,
                    fontSize = 8.sp,
                    maxLines = 1
                )
            }
        }
    }
}
