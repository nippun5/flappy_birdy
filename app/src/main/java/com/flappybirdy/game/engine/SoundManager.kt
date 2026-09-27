package com.flappybirdy.game.engine

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Lightweight sound effects generated on-device with [ToneGenerator], so the
 * game needs no bundled audio files at all.
 */
class SoundManager {

    private var toneGenerator: ToneGenerator? = null
    var enabled: Boolean = true

    private fun generator(): ToneGenerator? {
        if (toneGenerator == null) {
            toneGenerator = runCatching {
                ToneGenerator(AudioManager.STREAM_MUSIC, 70)
            }.getOrNull()
        }
        return toneGenerator
    }

    fun playFlap() {
        if (!enabled) return
        generator()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 40)
    }

    fun playScore() {
        if (!enabled) return
        generator()?.startTone(ToneGenerator.TONE_PROP_ACK, 90)
    }

    fun playCollision() {
        if (!enabled) return
        generator()?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 220)
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
