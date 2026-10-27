package com.mylittleworld.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mylittleworld.launcher.model.HouseThemePreset

/**
 * Art Direction Color Palette:
 * Exaggerated adult animated sci-fi sitcom aesthetic (thick outlines, flat cel-shaded colors,
 * dusty blue, charcoal grey, warm orange, muted green, and warm yellow).
 */
object SitcomPalette {
    val InkBlack = Color(0xFF14161D)
    val CharcoalGrey = Color(0xFF212631)
    val CharcoalPanel = Color(0xEB1B202B)
    val DustyBlueWall = Color(0xFF394F6B)
    val DustyBlueShadow = Color(0xFF28384D)
    val SunsetWall = Color(0xFF5D3A58)
    val SunsetShadow = Color(0xFF38223B)
    val CyberTealWall = Color(0xFF1F4E5B)
    val CyberTealShadow = Color(0xFF13313A)
    val NightSkyDeep = Color(0xFF131B2E)
    val MutedGreen = Color(0xFF567D65)
    val KitchenMintWall = Color(0xFF4F7768)
    val BathroomTileTeal = Color(0xFF3C6E71)
    val HallwayWarmOchre = Color(0xFF7A593C)
    val PortalNeonGreen = Color(0xFF69F0AE)
    val PortalLimeCore = Color(0xFFB9F6CA)
    val WarmOrange = Color(0xFFE87A3E)
    val WarmYellow = Color(0xFFF6C95E)
    val LampGlowYellow = Color(0xFFFFE082)
    val CreamNote = Color(0xFFF4E8D8)
    val WoodWarm = Color(0xFF8C5332)
    val WoodDark = Color(0xFF59321C)
    val RugTerracotta = Color(0xFFA84A3B)
    val TealAccent = Color(0xFF26C6DA)
    val TextPrimary = Color(0xFFF7F9FC)
    val TextSecondary = Color(0xFFB8C4D4)

    fun wallGradientFor(preset: HouseThemePreset, defaultTop: Color, defaultBottom: Color): List<Color> =
        when (preset) {
            HouseThemePreset.COZY_NIGHT -> listOf(defaultTop, defaultBottom)
            HouseThemePreset.WARM_SUNSET -> listOf(SunsetShadow, SunsetWall)
            HouseThemePreset.SCI_FI_NEON -> listOf(CyberTealShadow, CyberTealWall)
        }
}

private val SitcomColorScheme = darkColorScheme(
    primary = SitcomPalette.TealAccent,
    secondary = SitcomPalette.WarmOrange,
    tertiary = SitcomPalette.PortalNeonGreen,
    background = SitcomPalette.CharcoalGrey,
    surface = SitcomPalette.CharcoalPanel,
    onPrimary = SitcomPalette.InkBlack,
    onSecondary = SitcomPalette.InkBlack,
    onBackground = SitcomPalette.TextPrimary,
    onSurface = SitcomPalette.TextPrimary
)

@Composable
fun MyLittleWorldTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SitcomColorScheme,
        content = content
    )
}
