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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.mylittleworld.launcher.model.DogPoseState
import com.mylittleworld.launcher.model.FurnitureHotspotId
import com.mylittleworld.launcher.model.GirlPoseState
import com.mylittleworld.launcher.model.HouseThemePreset
import com.mylittleworld.launcher.model.InstalledApp
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawGoldenDog
import com.mylittleworld.launcher.ui.characters.CharacterRigRenderer.drawProtagonistGirl
import com.mylittleworld.launcher.ui.theme.SitcomPalette

/**
 * ROOM 4: KITCHEN — OPENING APP CABINET
 *
 * Matches the center panel of the user's reference mockup:
 * - Physical sage-green Kitchen Cabinet mounted above the counter.
 * - Tapping the cabinet animates its double doors swinging open (respecting Master Animation ON/OFF)
 *   to reveal internal wooden shelves holding Swiggy, Blinkit, Zepto, Zomato, Amazon, Flipkart,
 *   BigBasket, Instamart, Myntra, and other installed shopping/food-delivery apps.
 * - Includes spice jars on top of the cabinet, a warm pendant light, a refrigerator covered in
 *   sticky notes ("GOOD FOOD GOOD MOOD") and magnets, the Girl reaching for the cabinet handle,
 *   and the Golden Dog looking up eagerly.
 */
@Composable
fun KitchenRoomScene(
    masterAnimationEnabled: Boolean,
    themePreset: HouseThemePreset,
    showGirl: Boolean,
    showPets: Boolean,
    girlPose: GirlPoseState,
    dogPose: DogPoseState,
    cabinetApps: List<InstalledApp>,
    allApps: List<InstalledApp>,
    favoritePackages: Set<String>,
    onToggleCabinetApp: (InstalledApp) -> Unit,
    onLaunchApp: (InstalledApp) -> Unit,
    onToggleFavorite: (InstalledApp) -> Unit,
    onGirlClick: () -> Unit,
    onDogClick: () -> Unit,
    onBackToBedroom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCabinetOpen by remember { mutableStateOf(true) }
    var isEditingCabinet by remember { mutableStateOf(false) }

    // Animated door opening fraction (0f = closed doors, 1f = wide open doors)
    val doorOpenFraction by animateFloatAsState(
        targetValue = if (isCabinetOpen) 1f else 0f,
        animationSpec = if (masterAnimationEnabled) tween(durationMillis = 520) else tween(durationMillis = 0),
        label = "KitchenCabinetDoors"
    )

    val assignedSet = remember(cabinetApps) { cabinetApps.map { it.packageName }.toSet() }
    val displayedApps = if (isEditingCabinet) allApps else cabinetApps

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        // LAYER 1, 2 & 3: Kitchen Counter, Spice Jars, Fridge with Magnets, Girl & Dog
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawKitchenEnvironment(themePreset = themePreset, doorOpenFraction = doorOpenFraction)

            if (showGirl) {
                drawProtagonistGirl(
                    pose = GirlPoseState.COOKING_STOVE,
                    centerX = size.width * 0.24f,
                    centerY = size.height * 0.55f,
                    breathPulse = 1.0f,
                    facingRight = true
                )
            }
            if (showPets) {
                drawGoldenDog(
                    pose = dogPose,
                    centerX = size.width * 0.78f,
                    centerY = size.height * 0.74f,
                    pulse = 1.0f
                )
            }
        }

        // Top Breadcrumb Pill
        RoomBreadcrumbPill(
            title = "Kitchen – App Cabinet",
            onBackToBedroom = onBackToBedroom,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 88.dp, top = 74.dp)
        )

        // LAYER 4: Physical Kitchen App Cabinet Container (Center)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 110.dp, top = 118.dp)
                .width(w * 0.42f)
                .height(h * 0.58f)
        ) {
            // Interior Cabinet Frame & Shelves
            Surface(
                color = Color(0xFF2B3D34),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .border(4.dp, SitcomPalette.InkBlack, RoundedCornerShape(16.dp))
            ) {
                if (doorOpenFraction < 0.35f) {
                    // Closed Cabinet Doors View — Tap to Open
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { isCabinetOpen = true }
                    ) {
                        // Left Closed Door
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(SitcomPalette.KitchenMintWall)
                                .border(2.dp, SitcomPalette.InkBlack),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(width = 8.dp, height = 38.dp)
                                    .background(SitcomPalette.WarmYellow, RoundedCornerShape(4.dp))
                                    .border(1.5.dp, SitcomPalette.InkBlack, RoundedCornerShape(4.dp))
                            )
                        }
                        // Right Closed Door
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(SitcomPalette.KitchenMintWall)
                                .border(2.dp, SitcomPalette.InkBlack),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .size(width = 8.dp, height = 38.dp)
                                    .background(SitcomPalette.WarmYellow, RoundedCornerShape(4.dp))
                                    .border(1.5.dp, SitcomPalette.InkBlack, RoundedCornerShape(4.dp))
                            )
                        }
                    }
                } else {
                    // Open Cabinet Shelves with Installed Food & Shopping Apps
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isEditingCabinet) "Select Food & Shopping Apps" else "Pantry & Quick Delivery",
                                color = SitcomPalette.WarmYellow,
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
                                        contentDescription = "Edit Cabinet Apps",
                                        tint = if (isEditingCabinet) SitcomPalette.WarmYellow else SitcomPalette.TextSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { isCabinetOpen = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = SitcomPalette.WarmOrange),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Close Doors", color = SitcomPalette.InkBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                displayedApps,
                                key = { "kitchen_${it.packageName}" }
                            ) { app ->
                                AppGridTile(
                                    app = app,
                                    isEditing = isEditingCabinet,
                                    isAssigned = app.packageName in assignedSet,
                                    isFavorite = app.packageName in favoritePackages,
                                    accentColor = FurnitureHotspotId.KITCHEN_CABINET.accentColor,
                                    onClick = {
                                        if (isEditingCabinet) {
                                            onToggleCabinetApp(app)
                                        } else {
                                            onLaunchApp(app)
                                            // Automatically close cabinet doors after launching if desired
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

private fun DrawScope.drawKitchenEnvironment(
    themePreset: HouseThemePreset,
    doorOpenFraction: Float
) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.74f

    // Warm Kitchen Backsplash Wall
    drawRect(
        brush = Brush.verticalGradient(
            colors = SitcomPalette.wallGradientFor(
                themePreset,
                 Color(0xFF4A4238),
                Color(0xFF6B5E4C)
            ),
            startY = 0f,
            endY = floorY
        ),
        size = Size(w, floorY)
    )
    drawRect(color = SitcomPalette.WoodDark, topLeft = Offset(0f, floorY), size = Size(w, h - floorY))

    // Warm Hanging Kitchen Lamp on Left
    val lampCenter = Offset(w * 0.22f, h * 0.22f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(SitcomPalette.LampGlowYellow.copy(alpha = 0.5f), Color.Transparent),
            center = lampCenter,
            radius = h * 0.22f
        ),
        radius = h * 0.22f,
        center = lampCenter
    )
    drawRoundRect(
        color = SitcomPalette.WarmYellow,
        topLeft = Offset(lampCenter.x - 28f, lampCenter.y - 16f),
        size = Size(56f, 26f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = SitcomPalette.InkBlack,
        topLeft = Offset(lampCenter.x - 28f, lampCenter.y - 16f),
        size = Size(56f, 26f),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 4.5f)
    )

    // Spice & Pickle Jars on Top of the Kitchen Cabinet
    val jarColors = listOf(SitcomPalette.WarmOrange, SitcomPalette.WarmYellow, SitcomPalette.TealAccent, SitcomPalette.PortalNeonGreen, SitcomPalette.RugTerracotta)
    for (j in jarColors.indices) {
        val jx = w * 0.38f + j * 38f
        val jy = h * 0.14f
        drawRoundRect(color = jarColors[j], topLeft = Offset(jx, jy), size = Size(24f, 34f), cornerRadius = CornerRadius(6f, 6f))
        drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(jx, jy), size = Size(24f, 34f), cornerRadius = CornerRadius(6f, 6f), style = Stroke(width = 3.5f))
    }

    // Open Swing Doors drawn on either side of the cabinet when doorOpenFraction > 0
    if (doorOpenFraction > 0.1f) {
        val doorW = (w * 0.07f) * doorOpenFraction
        val cabTop = h * 0.22f
        val cabH = h * 0.54f
        // Left open door wing
        drawRoundRect(
            color = SitcomPalette.KitchenMintWall,
            topLeft = Offset(w * 0.34f - doorW, cabTop),
            size = Size(doorW, cabH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = SitcomPalette.InkBlack,
            topLeft = Offset(w * 0.34f - doorW, cabTop),
            size = Size(doorW, cabH),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(width = 4.5f)
        )
        // Right open door wing
        drawRoundRect(
            color = SitcomPalette.KitchenMintWall,
            topLeft = Offset(w * 0.77f, cabTop),
            size = Size(doorW, cabH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = SitcomPalette.InkBlack,
            topLeft = Offset(w * 0.77f, cabTop),
            size = Size(doorW, cabH),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(width = 4.5f)
        )
    }

    // Right Wing Refrigerator with Magnets & Sticky Notes ("GOOD FOOD GOOD MOOD")
    val fridgeLeft = w * 0.83f
    val fridgeTop = h * 0.18f
    val fridgeW = w * 0.15f
    val fridgeH = floorY - fridgeTop
    drawRoundRect(color = Color(0xFFD6CFC2), topLeft = Offset(fridgeLeft, fridgeTop), size = Size(fridgeW, fridgeH), cornerRadius = CornerRadius(14f, 14f))
    drawRoundRect(color = SitcomPalette.InkBlack, topLeft = Offset(fridgeLeft, fridgeTop), size = Size(fridgeW, fridgeH), cornerRadius = CornerRadius(14f, 14f), style = Stroke(width = 5f))
    // Sticky note on fridge
    drawRect(color = SitcomPalette.WarmYellow, topLeft = Offset(fridgeLeft + 20f, fridgeTop + 60f), size = Size(36f, 36f))
    drawRect(color = SitcomPalette.InkBlack, topLeft = Offset(fridgeLeft + 20f, fridgeTop + 60f), size = Size(36f, 36f), style = Stroke(width = 3f))
}
