package com.mylittleworld.launcher.ui.rooms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mylittleworld.launcher.model.InstalledApp
import com.mylittleworld.launcher.ui.theme.SitcomPalette

/**
 * Top room breadcrumb pill matching the reference mockups:
 * e.g., "← Entertainment Room – TV (Live Channels)", "← Kitchen – App Cabinet", etc.
 */
@Composable
fun RoomBreadcrumbPill(
    title: String,
    onBackToBedroom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onBackToBedroom,
        color = SitcomPalette.CharcoalPanel,
        shape = CircleShape,
        modifier = modifier
            .border(2.dp, SitcomPalette.InkBlack, CircleShape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back to Bedroom",
                tint = SitcomPalette.TextPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = SitcomPalette.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

/**
 * Floating interactive furniture hotspot pill & bounding box.
 */
@Composable
fun FurnitureHotspotBox(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
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
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .background(SitcomPalette.CharcoalPanel, CircleShape)
                .border(2.dp, SitcomPalette.InkBlack, CircleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(accentColor, CircleShape)
                    .border(1.dp, SitcomPalette.InkBlack, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = SitcomPalette.InkBlack,
                    modifier = Modifier.size(11.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column {
                Text(
                    text = title,
                    color = SitcomPalette.TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 11.sp
                )
                Text(
                    text = subtitle,
                    color = accentColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 9.sp
                )
            }
        }
    }
}

/**
 * Reusable App Icon Tile with real Android icon bitmap, favorite star toggle, and edit selection state.
 */
@Composable
fun AppGridTile(
    app: InstalledApp,
    isEditing: Boolean = false,
    isAssigned: Boolean = true,
    isFavorite: Boolean = false,
    accentColor: Color = SitcomPalette.TealAccent,
    onClick: () -> Unit,
    onLongOrStarClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isEditing && isAssigned) accentColor.copy(alpha = 0.22f)
                else Color.Transparent
            )
            .border(
                width = if (isEditing && isAssigned) 2.dp else 0.dp,
                color = if (isEditing && isAssigned) accentColor else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            if (app.iconBitmap != null) {
                Image(
                    bitmap = app.iconBitmap.asImageBitmap(),
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SitcomPalette.DustyBlueWall, RoundedCornerShape(12.dp))
                        .border(2.dp, SitcomPalette.InkBlack, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.label.take(2).uppercase(),
                        color = SitcomPalette.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (onLongOrStarClick != null) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) SitcomPalette.WarmYellow else SitcomPalette.TextSecondary.copy(alpha = 0.45f),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onLongOrStarClick() }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.label,
            color = SitcomPalette.TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
