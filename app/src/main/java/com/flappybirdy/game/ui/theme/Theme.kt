package com.flappybirdy.game.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ForestDeep = Color(0xFF1B5E20)
val ForestMid = Color(0xFF2E7D32)
val ForestLight = Color(0xFF66BB6A)
val SkyTop = Color(0xFF29B6F6)
val SkyBottom = Color(0xFFB3E5FC)
val Amber = Color(0xFFFFC107)
val CreamText = Color(0xFFFFFDE7)

private val FlappyLightScheme = lightColorScheme(
    primary = ForestMid,
    onPrimary = Color.White,
    secondary = SkyTop,
    onSecondary = Color.White,
    tertiary = Amber,
    background = SkyBottom,
    surface = Color(0xFFF1F8E9)
)

private val FlappyDarkScheme = darkColorScheme(
    primary = ForestLight,
    onPrimary = Color.Black,
    secondary = SkyTop,
    tertiary = Amber,
    background = Color(0xFF0D2B12),
    surface = Color(0xFF123018)
)

@Composable
fun FlappyBirdyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FlappyDarkScheme else FlappyLightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
