package com.flappybirdy.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flappybirdy.game.R
import com.flappybirdy.game.engine.GameEngine
import com.flappybirdy.game.engine.GameStore
import com.flappybirdy.game.engine.SoundManager
import com.flappybirdy.game.model.BirdSkin
import kotlinx.coroutines.isActive

@Composable
fun GameScreen(
    birdSkin: BirdSkin,
    gameStore: GameStore,
    soundManager: SoundManager,
    onGameOver: (score: Int, isNewHighScore: Boolean) -> Unit
) {
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var engine by remember { mutableStateOf<GameEngine?>(null) }
    var elapsedTime by remember { mutableFloatStateOf(0f) }
    var frameScore by remember { mutableStateOf(0) }
    var hasFlappedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(boxSize) {
        if (boxSize.width > 0 && boxSize.height > 0 && engine == null) {
            val groundY = boxSize.height * 0.86f
            val newEngine = GameEngine(
                worldWidth = boxSize.width.toFloat(),
                worldHeight = boxSize.height.toFloat(),
                groundY = groundY
            )
            newEngine.setOnScore { soundManager.playScore() }
            newEngine.setOnCrash { soundManager.playCollision() }
            engine = newEngine
        }
    }

    LaunchedEffect(engine) {
        val eng = engine ?: return@LaunchedEffect
        var lastFrame = -1L
        while (isActive && !eng.isGameOver) {
            var dt = 0f
            withFrameNanosCompat { now ->
                dt = if (lastFrame < 0L) 0f else ((now - lastFrame) / 1_000_000_000f).coerceIn(0f, 0.05f)
                lastFrame = now
            }
            eng.update(dt)
            elapsedTime += dt
            frameScore = eng.score
        }
        val isNewHigh = gameStore.submitScore(eng.score)
        onGameOver(eng.score, isNewHigh)
    }

    val skyTop = colorResource(R.color.sky_top)
    val skyBottom = colorResource(R.color.sky_bottom)
    val trunk = colorResource(R.color.trunk_brown)
    val trunkShade = colorResource(R.color.trunk_shade)
    val canopyColors = listOf(
        colorResource(R.color.forest_light),
        colorResource(R.color.forest_mid),
        colorResource(R.color.forest_deep),
        colorResource(R.color.forest_mid)
    )
    val grass = colorResource(R.color.ground_green)
    val darkGrass = colorResource(R.color.ground_dark)
    val soil = colorResource(R.color.soil_brown)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { boxSize = it }
            .pointerInput(engine) {
                detectTapGestures(onPress = {
                    hasFlappedOnce = true
                    engine?.flap()
                    soundManager.playFlap()
                })
            }
    ) {
        val eng = engine
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (eng == null) return@Canvas
            val w = size.width
            val h = size.height
            val groundY = h * 0.86f

            drawSky(w, h, groundY, eng.groundScrollX, skyTop, skyBottom)
            for (obstacle in eng.obstacles) {
                drawTreeObstacle(obstacle, eng.obstacleWidthPx(), groundY, trunk, trunkShade, canopyColors)
            }
            drawGround(w, groundY, h, eng.groundScrollX, grass, darkGrass, soil)

            val rotation = velocityToRotation(eng.birdVelocity, h * 1.1f)
            val wing = if (eng.hasStarted) wingPhaseAt(elapsedTime) else wingPhaseAt(elapsedTime * 0.4f)
            drawBird(Offset(eng.birdX, eng.birdY), eng.birdRadius, birdSkin, rotation, wing)
        }

        Text(
            text = "$frameScore",
            color = Color.White,
            fontSize = 56.sp,
            fontWeight = FontWeight.Black,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.35f),
                    offset = Offset(0f, 4f),
                    blurRadius = 6f
                )
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        )

        if (!hasFlappedOnce) {
            Text(
                text = "Tap anywhere to flap!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            )
        }
    }
}

/**
 * withFrameNanos requires an active MonotonicFrameClock, which the Compose
 * runtime always provides while composing — this thin wrapper just keeps the
 * import list in [GameScreen] tidy.
 */
private suspend inline fun withFrameNanosCompat(crossinline onFrame: (Long) -> Unit) {
    androidx.compose.runtime.withFrameNanos { onFrame(it) }
}
