package com.flappybirdy.game.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flappybirdy.game.R
import com.flappybirdy.game.model.BirdSkin

@Composable
fun MainMenuScreen(
    selectedSkin: BirdSkin,
    highScore: Int,
    onPlay: () -> Unit,
    onChooseBird: () -> Unit
) {
    val skyTop = colorResource(R.color.sky_top)
    val skyBottom = colorResource(R.color.sky_bottom)
    val trunk = colorResource(R.color.trunk_brown)
    val trunkShade = colorResource(R.color.trunk_shade)
    val canopyColors = listOf(
        colorResource(R.color.forest_light),
        colorResource(R.color.forest_mid),
        colorResource(R.color.forest_deep)
    )
    val grass = colorResource(R.color.ground_green)
    val darkGrass = colorResource(R.color.ground_dark)
    val soil = colorResource(R.color.soil_brown)

    val infiniteTransition = rememberInfiniteTransition(label = "menu_bob")
    val bob by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    val wingFlap by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(260, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val groundY = h * 0.86f
            drawSky(w, h, groundY, scrollX = 0f, topColor = skyTop, bottomColor = skyBottom)
            // A couple of static decorative trees flanking the scene
            drawDecorTree(
                baseX = w * 0.12f,
                groundY = groundY,
                trunkHeight = h * 0.22f,
                trunkWidth = w * 0.05f,
                trunkColor = trunk,
                trunkShadeColor = trunkShade,
                canopyColors = canopyColors
            )
            drawDecorTree(
                baseX = w * 0.90f,
                groundY = groundY,
                trunkHeight = h * 0.26f,
                trunkWidth = w * 0.055f,
                trunkColor = trunk,
                trunkShadeColor = trunkShade,
                canopyColors = canopyColors
            )
            drawGround(w, groundY, h, scrollX = 0f, grassColor = grass, darkGrassColor = darkGrass, dirtColor = soil)

            val birdCenter = Offset(w * 0.5f, h * 0.36f + bob * h * 0.02f)
            drawBird(
                center = birdCenter,
                radius = w * 0.11f,
                skin = selectedSkin,
                rotationDegrees = bob * 6f,
                wingPhase = wingFlap
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Flappy Birdy",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(Color.Black.copy(alpha = 0.4f), Offset(0f, 5f), 8f)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Best: $highScore",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(Color.Black.copy(alpha = 0.4f), Offset(0f, 3f), 5f)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onPlay,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.forest_mid),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(58.dp)
            ) {
                Text("PLAY", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onChooseBird,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(52.dp)
            ) {
                Text("Choose Your Bird: ${selectedSkin.displayName}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
