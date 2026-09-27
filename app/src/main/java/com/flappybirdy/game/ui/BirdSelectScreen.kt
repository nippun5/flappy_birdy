package com.flappybirdy.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun BirdSelectScreen(
    selectedSkin: BirdSkin,
    onSelect: (BirdSkin) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val skyTop = colorResource(R.color.sky_top)
    val skyBottom = colorResource(R.color.sky_bottom)
    val grass = colorResource(R.color.ground_green)
    val darkGrass = colorResource(R.color.ground_dark)
    val soil = colorResource(R.color.soil_brown)
    val forestMid = colorResource(R.color.forest_mid)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val groundY = size.height * 0.86f
            drawSky(size.width, size.height, groundY, scrollX = 0f, topColor = skyTop, bottomColor = skyBottom)
            drawGround(size.width, groundY, size.height, scrollX = 0f, grassColor = grass, darkGrassColor = darkGrass, dirtColor = soil)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Your Bird",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = selectedSkin.displayName,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center
            ) {
                BirdSkin.entries.forEach { skin ->
                    BirdOption(
                        skin = skin,
                        isSelected = skin == selectedSkin,
                        accentColor = forestMid,
                        onClick = { onSelect(skin) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = forestMid,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(56.dp)
            ) {
                Text("USE THIS BIRD", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onBack) {
                Text("Back to menu", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun BirdOption(
    skin: BirdSkin,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(96.dp)
            .background(Color.White.copy(alpha = if (isSelected) 0.35f else 0.15f), RoundedCornerShape(20.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) accentColor else Color.White.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(56.dp)) {
                drawBird(
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.minDimension / 2.6f,
                    skin = skin,
                    rotationDegrees = 0f,
                    wingPhase = 0f
                )
            }
            Text(
                text = skin.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
