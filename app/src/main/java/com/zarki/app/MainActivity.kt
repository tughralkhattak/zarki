package com.zarki.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zarki.app.ui.ZarkiApp
import com.zarki.app.ui.theme.ZarkiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val settings by ZarkiApplication.instance.settings.state.collectAsStateWithLifecycle()
            ZarkiTheme(theme = settings.theme) {
                ZarkiApp()
            }
        }
    }
}
