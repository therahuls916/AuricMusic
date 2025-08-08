// In MainActivity.kt

package com.rahul.auricmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rahul.auricmusic.player.MediaControllerManager
import com.rahul.auricmusic.ui.screens.MainAppScreen
import com.rahul.auricmusic.ui.screens.SplashScreen
import com.rahul.auricmusic.ui.theme.AuricMusicTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // As soon as the app starts, begin the connection to the media service.
        MediaControllerManager.connect(this)

        var keepSystemSplashOn by mutableStateOf(true)
        installSplashScreen().setKeepOnScreenCondition { keepSystemSplashOn }

        enableEdgeToEdge()

        setContent {
            AuricMusicTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash_screen"
                ) {
                    composable("splash_screen") {
                        keepSystemSplashOn = false
                        SplashScreen(
                            onAnimationEnd = {
                                navController.navigate("main_app") {
                                    popUpTo("splash_screen") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main_app") {
                        MainAppScreen()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaControllerManager.release()
    }
}