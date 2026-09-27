package com.flappybirdy.game.model

import androidx.compose.ui.graphics.Color

/**
 * A selectable bird character. Every skin uses the same body proportions and
 * flight physics — only the color palette changes — so the game stays fair
 * regardless of which bird the player picks.
 */
enum class BirdSkin(
    val id: Int,
    val displayName: String,
    val bodyColor: Color,
    val bodyShade: Color,
    val bellyColor: Color,
    val wingColor: Color,
    val beakColor: Color
) {
    ROBIN(
        id = 0,
        displayName = "Robin Red",
        bodyColor = Color(0xFFE53935),
        bodyShade = Color(0xFFC62828),
        bellyColor = Color(0xFFFFF3E0),
        wingColor = Color(0xFFB71C1C),
        beakColor = Color(0xFFFFB300)
    ),
    SKY_JAY(
        id = 1,
        displayName = "Sky Jay",
        bodyColor = Color(0xFF29B6F6),
        bodyShade = Color(0xFF0277BD),
        bellyColor = Color(0xFFE1F5FE),
        wingColor = Color(0xFF01579B),
        beakColor = Color(0xFFFF7043)
    ),
    FOREST_FINCH(
        id = 2,
        displayName = "Forest Finch",
        bodyColor = Color(0xFF66BB6A),
        bodyShade = Color(0xFF2E7D32),
        bellyColor = Color(0xFFF1F8E9),
        wingColor = Color(0xFF1B5E20),
        beakColor = Color(0xFFFFC107)
    ),
    SUN_CANARY(
        id = 3,
        displayName = "Sun Canary",
        bodyColor = Color(0xFFFDD835),
        bodyShade = Color(0xFFF9A825),
        bellyColor = Color(0xFFFFFDE7),
        wingColor = Color(0xFFF57F17),
        beakColor = Color(0xFFFF7043)
    ),
    BERRY_BIRD(
        id = 4,
        displayName = "Berry Bird",
        bodyColor = Color(0xFFAB47BC),
        bodyShade = Color(0xFF6A1B9A),
        bellyColor = Color(0xFFF3E5F5),
        wingColor = Color(0xFF4A148C),
        beakColor = Color(0xFFFFB300)
    );

    companion object {
        fun fromId(id: Int): BirdSkin = entries.firstOrNull { it.id == id } ?: ROBIN
    }
}
