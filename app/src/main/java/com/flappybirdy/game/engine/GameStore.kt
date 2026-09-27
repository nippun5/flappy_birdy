package com.flappybirdy.game.engine

import android.content.Context
import android.content.SharedPreferences

/** Small wrapper around SharedPreferences for the two things we persist. */
class GameStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var highScore: Int
        get() = prefs.getInt(KEY_HIGH_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_HIGH_SCORE, value).apply()

    var selectedBirdId: Int
        get() = prefs.getInt(KEY_SELECTED_BIRD, 0)
        set(value) = prefs.edit().putInt(KEY_SELECTED_BIRD, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    /** Returns true (and updates storage) only if [score] beats the previous best. */
    fun submitScore(score: Int): Boolean {
        return if (score > highScore) {
            highScore = score
            true
        } else {
            false
        }
    }

    companion object {
        private const val PREFS_NAME = "flappy_birdy_prefs"
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_SELECTED_BIRD = "selected_bird"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
    }
}
