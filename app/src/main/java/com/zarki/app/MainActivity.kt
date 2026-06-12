package com.zarki.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.tween
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zarki.app.ui.ZarkiApp
import com.zarki.app.ui.splash.SplashScreen
import com.zarki.app.ui.theme.ZarkiTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val settings by ZarkiApplication.instance.settings.state.collectAsStateWithLifecycle()
            var showSplash by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                delay(2000)
                showSplash = false
            }
            ZarkiTheme(theme = settings.theme) {
                ZarkiApp()
                AnimatedVisibility(
                    visible = showSplash,
                    enter = fadeIn(),
                    exit = fadeOut(tween(500)),
                ) {
                    SplashScreen()
                }
            }
        }
    }
}
