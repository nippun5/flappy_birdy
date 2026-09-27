# Flappy Birdy 🐦🌲

A native Android Flappy-Bird-style game built with **Kotlin + Jetpack Compose**.
Pick a bird, tap to flap, and dodge a scrolling forest of trees for as long as
you can. No external art or audio assets — every visual (bird, trees, sky,
clouds, ground) is drawn procedurally on a Compose `Canvas`, and sound effects
are generated on-device with `ToneGenerator`. That keeps the APK tiny and
means there's nothing to license or attribute.

## Tech stack
- **Language:** Kotlin 2.1.20
- **UI:** Jetpack Compose (Material 3), single-activity, no navigation library
- **compileSdk / targetSdk:** 36 (Android 16) · **minSdk:** 26 (Android 8.0)
- **Android Gradle Plugin:** 8.13.0 · **Gradle:** 8.13
- **Compose BOM:** 2025.08.00

These versions were current as of this project's creation. If Android Studio
prompts you to upgrade AGP/Gradle/Kotlin when you open the project, that's
safe to accept.

## How to open and run
1. Unzip the project.
2. Open the folder in **Android Studio** (Otter or newer recommended) via
   *File → Open*. Android Studio will generate the Gradle wrapper jar
   automatically on first sync (this project ships `gradle-wrapper.properties`
   but not the binary wrapper jar, since it can't be produced without network
   access to Gradle's distribution server).
3. Let Gradle sync — it will download the AGP, Kotlin, and Compose
   dependencies from Google's and Maven Central's repositories.
4. Press **Run ▶** with an emulator or physical device (API 26+).

> **Note:** This project was authored and reviewed in a sandboxed environment
> without access to the Android SDK or Google's Maven repository, so it has
> **not** been compiled end-to-end. The code has been written carefully and
> cross-checked, but please let Android Studio's first sync/build surface any
> small fixes it wants (e.g. a version bump it suggests automatically).

## Project structure
```
app/src/main/java/com/flappybirdy/game/
├── MainActivity.kt          # Single activity; screen-state machine
├── model/
│   ├── BirdSkin.kt           # 5 selectable bird color palettes
│   └── Screen.kt              # Menu / BirdSelect / Playing / GameOver
├── engine/
│   ├── GameEngine.kt          # Physics: gravity, flap, collisions, scoring
│   ├── Obstacle.kt             # A tree-pair gap obstacle
│   ├── GameStore.kt            # SharedPreferences: high score, chosen bird
│   └── SoundManager.kt         # ToneGenerator-based sound effects
└── ui/
    ├── GameRenderer.kt         # Canvas drawing helpers (sky, trees, bird)
    ├── GameScreen.kt           # Game loop + input + HUD
    ├── MainMenuScreen.kt
    ├── BirdSelectScreen.kt
    ├── GameOverScreen.kt
    └── theme/Theme.kt          # Material 3 green/blue theme
```

## Gameplay notes
- Tap anywhere to flap. Gravity pulls the bird down constantly, matching the
  classic Flappy Bird feel: one clean upward impulse per tap, no double-jump.
- The gap size is constant (as in the original game); scroll speed ramps up
  *very slightly* over the first ~25 points and then holds, to keep long runs
  interesting without changing the core difficulty curve.
- High score and your chosen bird are saved locally and persist between
  sessions.

## Customizing
- **Bird designs:** `model/BirdSkin.kt` — add a new `enum` entry with its own
  color set and it will automatically appear in the bird-select screen.
- **Palette:** `res/values/colors.xml` is the single source of truth for the
  green/blue nature theme used everywhere (menus, obstacles, ground, icon).
- **Difficulty:** tunable constants live at the top of `engine/GameEngine.kt`.
