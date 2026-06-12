package com.zarki.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import kotlinx.coroutines.launch

/**
 * Animated launch screen: "Zarki" scales/fades in, with "Khattak"
 * elegantly fading in at the bottom.
 */
@Composable
fun SplashScreen() {
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.82f) }
    val subAlpha = remember { Animatable(0f) }
    val signAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { titleAlpha.animateTo(1f, tween(700)) }
        launch { titleScale.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
        launch {
            subAlpha.animateTo(1f, tween(700, delayMillis = 350))
        }
        launch {
            signAlpha.animateTo(1f, tween(800, delayMillis = 650))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF14101F),
                        Color(0xFF0B0B12),
                        Color(0xFF000000),
                    ),
                ),
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = "Zarki",
                style = TextStyle(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFB39DFF), Color(0xFF8B6DFF), Color(0xFF22D3EE)),
                    ),
                ),
                modifier = Modifier.graphicsLayer(
                    alpha = titleAlpha.value,
                    scaleX = titleScale.value,
                    scaleY = titleScale.value,
                ),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "M A N G A   R E A D E R",
                color = Color(0xFF8A8AA0),
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(subAlpha.value),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .alpha(signAlpha.value),
        ) {
            Text(
                text = "— crafted by —",
                color = Color(0xFF6A6A80),
                fontSize = 10.sp,
                letterSpacing = 3.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Khattak",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 8.sp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFE6D9FF), Color(0xFF9C86FF)),
                    ),
                ),
            )
        }
    }
}
