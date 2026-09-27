package com.flappybirdy.game.engine

/**
 * A pair of trees (one hanging from the canopy above, one growing from the
 * ground) with a gap the bird must fly through. Conceptually identical to
 * Flappy Bird's pipes, just re-skinned as nature obstacles.
 */
class Obstacle(
    var x: Float,
    val gapCenterY: Float,
    val gapHeight: Float,
    var scored: Boolean = false
) {
    fun topBottom(): Float = gapCenterY - gapHeight / 2f
    fun bottomTop(): Float = gapCenterY + gapHeight / 2f
}
