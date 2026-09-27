package com.flappybirdy.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.flappybirdy.game.engine.GameStore
import com.flappybirdy.game.engine.SoundManager
import com.flappybirdy.game.model.BirdSkin
import com.flappybirdy.game.model.Screen
import com.flappybirdy.game.ui.BirdSelectScreen
import com.flappybirdy.game.ui.GameOverScreen
import com.flappybirdy.game.ui.GameScreen
import com.flappybirdy.game.ui.MainMenuScreen
import com.flappybirdy.game.ui.theme.FlappyBirdyTheme

class MainActivity : ComponentActivity() {

    private lateinit var gameStore: GameStore
    private val soundManager = SoundManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameStore = GameStore(applicationContext)
        soundManager.enabled = gameStore.soundEnabled

        // Immersive, edge-to-edge canvas: this is a game, so the sky/ground art
        // should fill the whole screen with system bars tucked away until swiped.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            FlappyBirdyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FlappyBirdyApp(gameStore = gameStore, soundManager = soundManager)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
private fun FlappyBirdyApp(gameStore: GameStore, soundManager: SoundManager) {
    var screen by remember { mutableStateOf(Screen.MENU) }
    var selectedSkin by remember { mutableStateOf(BirdSkin.fromId(gameStore.selectedBirdId)) }
    var lastScore by remember { mutableIntStateOf(0) }
    var lastIsNewHigh by remember { mutableStateOf(false) }
    var highScore by remember { mutableIntStateOf(gameStore.highScore) }

    BackHandler(enabled = screen != Screen.MENU) {
        screen = Screen.MENU
    }

    when (screen) {
        Screen.MENU -> MainMenuScreen(
            selectedSkin = selectedSkin,
            highScore = highScore,
            onPlay = { screen = Screen.PLAYING },
            onChooseBird = { screen = Screen.BIRD_SELECT }
        )

        Screen.BIRD_SELECT -> BirdSelectScreen(
            selectedSkin = selectedSkin,
            onSelect = { skin ->
                selectedSkin = skin
                gameStore.selectedBirdId = skin.id
            },
            onConfirm = { screen = Screen.MENU },
            onBack = { screen = Screen.MENU }
        )

        Screen.PLAYING -> GameScreen(
            birdSkin = selectedSkin,
            gameStore = gameStore,
            soundManager = soundManager,
            onGameOver = { score, isNewHigh ->
                lastScore = score
                lastIsNewHigh = isNewHigh
                highScore = gameStore.highScore
                screen = Screen.GAME_OVER
            }
        )

        Screen.GAME_OVER -> GameOverScreen(
            score = lastScore,
            highScore = highScore,
            isNewHighScore = lastIsNewHigh,
            birdSkin = selectedSkin,
            onRetry = { screen = Screen.PLAYING },
            onMenu = { screen = Screen.MENU }
        )
    }
}
