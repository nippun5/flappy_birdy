package com.flappybirdy.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flappybirdy.game.R
import com.flappybirdy.game.model.BirdSkin

@Composable
fun GameOverScreen(
    score: Int,
    highScore: Int,
    isNewHighScore: Boolean,
    birdSkin: BirdSkin,
    onRetry: () -> Unit,
    onMenu: () -> Unit
) {
    val skyTop = colorResource(R.color.sky_top)
    val skyBottom = colorResource(R.color.sky_bottom)
    val grass = colorResource(R.color.ground_green)
    val darkGrass = colorResource(R.color.ground_dark)
    val soil = colorResource(R.color.soil_brown)
    val forestMid = colorResource(R.color.forest_mid)
    val forestDeep = colorResource(R.color.forest_deep)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val groundY = size.height * 0.86f
            drawSky(size.width, size.height, groundY, scrollX = 0f, topColor = skyTop, bottomColor = skyBottom)
            drawGround(size.width, groundY, size.height, scrollX = 0f, grassColor = grass, darkGrassColor = darkGrass, dirtColor = soil)

            drawBird(
                center = Offset(size.width * 0.5f, groundY - size.width * 0.16f),
                radius = size.width * 0.11f,
                skin = birdSkin,
                rotationDegrees = 92f,
                wingPhase = 0f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "GAME OVER",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            if (isNewHighScore) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "New Best!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.amber_accent)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                modifier = Modifier.fillMaxWidth(0.78f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ScoreColumn(label = "SCORE", value = score, color = forestDeep)
                    ScoreColumn(label = "BEST", value = highScore, color = forestDeep)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = forestMid,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(56.dp)
            ) {
                Text("PLAY AGAIN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onMenu,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(52.dp)
            ) {
                Text("MAIN MENU", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ScoreColumn(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = color.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "$value", fontSize = 32.sp, fontWeight = FontWeight.Black, color = color)
    }
    Spacer(modifier = Modifier.width(24.dp))
}
