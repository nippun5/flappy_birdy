package com.flappybirdy.game.engine

import kotlin.math.min
import kotlin.random.Random

/**
 * Drives all gameplay math for one round. The composable layer only calls
 * [flap], [update] and reads the exposed state — no Compose types leak in
 * here, which keeps the physics easy to reason about (and testable).
 *
 * Tuning mirrors the classic Flappy Bird feel: constant gravity, a single
 * upward impulse per tap, a fixed gap size, and an obstacle field that
 * scrolls at a near-constant speed. A very small speed ramp is layered on
 * top purely to keep long sessions interesting.
 */
class GameEngine(
    private val worldWidth: Float,
    private val worldHeight: Float,
    private val groundY: Float
) {
    // Bird state
    var birdX: Float = worldWidth * 0.28f
        private set
    var birdY: Float = worldHeight * 0.42f
        private set
    var birdVelocity: Float = 0f
        private set
    val birdRadius: Float = worldWidth * 0.052f

    // World state
    val obstacles: MutableList<Obstacle> = mutableListOf()
    var score: Int = 0
        private set
    var isGameOver: Boolean = false
        private set
    var hasStarted: Boolean = false
        private set
    var groundScrollX: Float = 0f
        private set

    // Tunables (scaled to screen size so play feel is consistent on any device)
    private val gravity = worldHeight * 2.35f
    private val flapVelocity = -worldHeight * 0.62f
    private val maxFallVelocity = worldHeight * 1.1f
    private val baseScrollSpeed = worldWidth * 0.30f
    private val obstacleWidth = worldWidth * 0.16f
    private val gapHeight = worldHeight * 0.24f
    private val obstacleSpacing = worldWidth * 0.62f
    private val obstacleAreaTop = worldHeight * 0.08f
    private val obstacleAreaBottom = groundY - worldHeight * 0.08f

    private var onScoreListener: (() -> Unit)? = null
    private var onCrashListener: (() -> Unit)? = null

    init {
        seedObstacles()
    }

    fun setOnScore(listener: () -> Unit) {
        onScoreListener = listener
    }

    fun setOnCrash(listener: () -> Unit) {
        onCrashListener = listener
    }

    private fun currentScrollSpeed(): Float {
        val ramp = 1f + min(score, 25) * 0.012f
        return baseScrollSpeed * ramp
    }

    private fun seedObstacles() {
        obstacles.clear()
        var x = worldWidth * 1.15f
        repeat(4) {
            obstacles.add(Obstacle(x, randomGapCenter(), gapHeight))
            x += obstacleSpacing
        }
    }

    private fun randomGapCenter(): Float {
        val minCenter = obstacleAreaTop + gapHeight / 2f
        val maxCenter = obstacleAreaBottom - gapHeight / 2f
        return Random.nextFloat() * (maxCenter - minCenter) + minCenter
    }

    /** Flap the wings: cancel downward speed and give a fresh upward impulse. */
    fun flap() {
        if (isGameOver) return
        hasStarted = true
        birdVelocity = flapVelocity
    }

    /** Advance the simulation by [deltaSeconds]. Safe to call every frame. */
    fun update(deltaSeconds: Float) {
        if (isGameOver) return
        val dt = min(deltaSeconds, 0.032f) // clamp so a slow frame can't tunnel through a tree

        val scrollSpeed = if (hasStarted) currentScrollSpeed() else 0f
        groundScrollX = (groundScrollX + scrollSpeed * dt).mod(worldWidth)

        if (!hasStarted) {
            // gentle idle bob before the first tap
            birdY += kotlin.math.sin(groundScrollX * 0.02f) * 0.15f
            return
        }

        birdVelocity = min(birdVelocity + gravity * dt, maxFallVelocity)
        birdY += birdVelocity * dt

        if (birdY - birdRadius < 0f) {
            birdY = birdRadius
            birdVelocity = 0f
        }

        for (obstacle in obstacles) {
            obstacle.x -= scrollSpeed * dt
        }

        val recycled = obstacles.filter { it.x + obstacleWidth < -worldWidth * 0.1f }
        if (recycled.isNotEmpty()) {
            obstacles.removeAll(recycled)
            val rightmostX = obstacles.maxOfOrNull { it.x } ?: (worldWidth * 1.15f)
            repeat(recycled.size) {
                val newX = (obstacles.maxOfOrNull { o -> o.x } ?: rightmostX) + obstacleSpacing
                obstacles.add(Obstacle(newX, randomGapCenter(), gapHeight))
            }
        }

        for (obstacle in obstacles) {
            if (!obstacle.scored && obstacle.x + obstacleWidth < birdX) {
                obstacle.scored = true
                score += 1
                onScoreListener?.invoke()
            }
        }

        if (checkCollisions()) {
            crash()
        }
    }

    private fun checkCollisions(): Boolean {
        if (birdY + birdRadius >= groundY) {
            birdY = groundY - birdRadius
            return true
        }
        for (obstacle in obstacles) {
            if (birdX + birdRadius < obstacle.x || birdX - birdRadius > obstacle.x + obstacleWidth) {
                continue
            }
            val hitsTop = birdY - birdRadius < obstacle.topBottom()
            val hitsBottom = birdY + birdRadius > obstacle.bottomTop()
            if (hitsTop || hitsBottom) {
                return true
            }
        }
        return false
    }

    private fun crash() {
        isGameOver = true
        onCrashListener?.invoke()
    }

    fun obstacleWidthPx(): Float = obstacleWidth

    fun reset() {
        birdY = worldHeight * 0.42f
        birdVelocity = 0f
        score = 0
        isGameOver = false
        hasStarted = false
        groundScrollX = 0f
        seedObstacles()
    }
}
