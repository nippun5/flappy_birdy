package com.flappybirdy.game.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.flappybirdy.game.engine.Obstacle
import com.flappybirdy.game.model.BirdSkin
import kotlin.math.sin

/** Sky gradient, drifting clouds, distant hills. Purely decorative. */
fun DrawScope.drawSky(width: Float, height: Float, groundY: Float, scrollX: Float, topColor: Color, bottomColor: Color) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, bottomColor),
            startY = 0f,
            endY = groundY
        ),
        size = Size(width, groundY)
    )

    // Drifting clouds (three simple overlapping ellipse clusters)
    val cloudColor = Color.White.copy(alpha = 0.85f)
    val cloudOffset = (scrollX * 0.15f).mod(width + 200f)
    val cloudYs = listOf(height * 0.16f, height * 0.28f, height * 0.10f)
    for (i in 0..2) {
        val baseX = (i * width / 2.2f - cloudOffset).mod(width + 200f) - 100f
        val cy = cloudYs[i]
        val scale = if (i == 1) 0.7f else 1f
        drawCloud(baseX, cy, 60f * scale)
    }

    // Distant rolling hills just above the ground line
    val hillColor = Color(0xFF81C784).copy(alpha = 0.55f)
    val hillOffset = (scrollX * 0.35f).mod(width)
    val hillPath = Path().apply {
        var x = -hillOffset
        moveTo(x, groundY)
        while (x < width + 120f) {
            quadraticBezierTo(x + 60f, groundY - 55f, x + 120f, groundY)
            x += 120f
        }
        lineTo(width + 40f, groundY)
        close()
    }
    drawPath(hillPath, color = hillColor)
}

private fun DrawScope.drawCloud(cx: Float, cy: Float, r: Float) {
    val c = Color.White.copy(alpha = 0.9f)
    drawCircle(c, radius = r * 0.6f, center = Offset(cx, cy))
    drawCircle(c, radius = r * 0.8f, center = Offset(cx + r * 0.7f, cy + r * 0.1f))
    drawCircle(c, radius = r * 0.55f, center = Offset(cx - r * 0.6f, cy + r * 0.15f))
    drawCircle(c, radius = r * 0.5f, center = Offset(cx + r * 0.2f, cy - r * 0.35f))
}

/** Scrolling grass ground strip. */
fun DrawScope.drawGround(width: Float, groundY: Float, totalHeight: Float, scrollX: Float, grassColor: Color, darkGrassColor: Color, dirtColor: Color) {
    drawRect(
        color = dirtColor,
        topLeft = Offset(0f, groundY),
        size = Size(width, totalHeight - groundY)
    )
    drawRect(
        color = grassColor,
        topLeft = Offset(0f, groundY),
        size = Size(width, (totalHeight - groundY) * 0.32f)
    )
    // scrolling grass tufts
    val tuftSpacing = 46f
    val offset = scrollX.mod(tuftSpacing)
    var x = -offset
    while (x < width + tuftSpacing) {
        drawRect(
            color = darkGrassColor,
            topLeft = Offset(x, groundY - 6f),
            size = Size(10f, 14f)
        )
        x += tuftSpacing
    }
}

/**
 * A tree-pair obstacle: canopy of overlapping circles capping a trunk, one
 * hanging from above and one growing from the ground, framing the gap.
 */
fun DrawScope.drawTreeObstacle(
    obstacle: Obstacle,
    width: Float,
    groundY: Float,
    trunkColor: Color,
    trunkShadeColor: Color,
    canopyColors: List<Color>
) {
    val x = obstacle.x
    val topEnd = obstacle.topBottom()
    val bottomStart = obstacle.bottomTop()
    val trunkInset = width * 0.22f

    // Top hanging tree
    drawTrunk(x + trunkInset, 0f, width - 2 * trunkInset, topEnd - canopyRadius(width) * 0.5f, trunkColor, trunkShadeColor)
    drawCanopyCluster(x + width / 2f, topEnd - canopyRadius(width) * 0.35f, canopyRadius(width), canopyColors, hangingDown = true)

    // Bottom growing tree
    drawTrunk(x + trunkInset, bottomStart + canopyRadius(width) * 0.5f, width - 2 * trunkInset, groundY - (bottomStart + canopyRadius(width) * 0.5f), trunkColor, trunkShadeColor)
    drawCanopyCluster(x + width / 2f, bottomStart + canopyRadius(width) * 0.35f, canopyRadius(width), canopyColors, hangingDown = false)
}

private fun canopyRadius(obstacleWidth: Float) = obstacleWidth * 0.78f

/** A single standalone tree growing from the ground, used as menu-screen decoration. */
fun DrawScope.drawDecorTree(
    baseX: Float,
    groundY: Float,
    trunkHeight: Float,
    trunkWidth: Float,
    trunkColor: Color,
    trunkShadeColor: Color,
    canopyColors: List<Color>
) {
    val r = trunkWidth * 1.9f
    drawTrunk(baseX - trunkWidth / 2f, groundY - trunkHeight, trunkWidth, trunkHeight, trunkColor, trunkShadeColor)
    drawCanopyCluster(baseX, groundY - trunkHeight - r * 0.3f, r, canopyColors, hangingDown = false)
}

private fun DrawScope.drawTrunk(x: Float, y: Float, w: Float, h: Float, color: Color, shade: Color) {
    if (h <= 0f) return
    drawRect(color = color, topLeft = Offset(x, y), size = Size(w, h))
    drawRect(color = shade, topLeft = Offset(x + w * 0.65f, y), size = Size(w * 0.35f, h))
}

private fun DrawScope.drawCanopyCluster(cx: Float, cy: Float, r: Float, colors: List<Color>, hangingDown: Boolean) {
    val positions = listOf(
        Offset(cx - r * 0.55f, cy) to r * 0.62f,
        Offset(cx + r * 0.55f, cy) to r * 0.62f,
        Offset(cx, cy - r * (if (hangingDown) -0.35f else 0.35f)) to r * 0.72f,
        Offset(cx, cy) to r * 0.5f
    )
    positions.forEachIndexed { index, (offset, radius) ->
        drawCircle(color = colors[index % colors.size], radius = radius, center = offset)
    }
}

/** Draws the selected bird, angled to its current vertical speed with a flapping wing. */
fun DrawScope.drawBird(
    center: Offset,
    radius: Float,
    skin: BirdSkin,
    rotationDegrees: Float,
    wingPhase: Float
) {
    rotate(degrees = rotationDegrees, pivot = center) {
        // tail
        val tailPath = Path().apply {
            moveTo(center.x - radius * 0.75f, center.y - radius * 0.1f)
            lineTo(center.x - radius * 1.35f, center.y - radius * 0.35f)
            lineTo(center.x - radius * 1.1f, center.y + radius * 0.25f)
            close()
        }
        drawPath(tailPath, color = skin.wingColor)

        // body
        drawCircle(color = skin.bodyColor, radius = radius, center = center)

        // belly
        drawCircle(
            color = skin.bellyColor,
            radius = radius * 0.6f,
            center = Offset(center.x + radius * 0.18f, center.y + radius * 0.18f)
        )

        // wing (flaps via wingPhase in [-1, 1])
        val wingLift = wingPhase * radius * 0.35f
        val wingPath = Path().apply {
            moveTo(center.x - radius * 0.1f, center.y - radius * 0.05f)
            quadraticBezierTo(
                center.x - radius * 0.1f, center.y - radius * 0.9f - wingLift,
                center.x + radius * 0.55f, center.y - radius * 0.35f - wingLift
            )
            quadraticBezierTo(
                center.x + radius * 0.3f, center.y + radius * 0.15f,
                center.x - radius * 0.1f, center.y - radius * 0.05f
            )
            close()
        }
        drawPath(wingPath, color = skin.wingColor)

        // beak
        val beakPath = Path().apply {
            moveTo(center.x + radius * 0.85f, center.y - radius * 0.12f)
            lineTo(center.x + radius * 1.45f, center.y)
            lineTo(center.x + radius * 0.85f, center.y + radius * 0.28f)
            close()
        }
        drawPath(beakPath, color = skin.beakColor)

        // eye
        drawCircle(
            color = Color.White,
            radius = radius * 0.28f,
            center = Offset(center.x + radius * 0.32f, center.y - radius * 0.32f)
        )
        drawCircle(
            color = Color(0xFF1B1B1B),
            radius = radius * 0.13f,
            center = Offset(center.x + radius * 0.4f, center.y - radius * 0.34f)
        )
    }
}

/** Maps vertical velocity to a natural-looking rotation, clamped like the original game. */
fun velocityToRotation(velocity: Float, maxFallVelocity: Float): Float {
    val normalized = (velocity / maxFallVelocity).coerceIn(-0.5f, 1f)
    return normalized * 70f
}

/** Simple oscillator used to drive the wing-flap animation while airborne. */
fun wingPhaseAt(timeSeconds: Float): Float = sin(timeSeconds * 14f)
