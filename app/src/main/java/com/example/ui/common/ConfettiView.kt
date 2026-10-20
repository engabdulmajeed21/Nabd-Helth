package com.example.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val speedY: Float,
    val speedX: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiCelebration(modifier: Modifier = Modifier) {
    val particles = remember {
        val colors = listOf(
            Color(0xFF2563EB),
            Color(0xFF10B981),
            Color(0xFFF59E0B),
            Color(0xFFEF4444),
            Color(0xFF8B5CF6),
            Color(0xFFEC4899)
        )
        List(60) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * -0.5f,
                speedY = Random.nextFloat() * 0.8f + 0.4f,
                speedX = (Random.nextFloat() - 0.5f) * 0.3f,
                size = Random.nextFloat() * 14f + 8f,
                color = colors.random()
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2800, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val currentY = (p.initialY + progress.value * p.speedY) * height
            val currentX = (p.initialX + progress.value * p.speedX) * width

            if (currentY in 0f..height) {
                drawRect(
                    color = p.color.copy(alpha = (1f - progress.value).coerceIn(0.2f, 1f)),
                    topLeft = Offset(currentX, currentY),
                    size = Size(p.size, p.size * 0.7f)
                )
            }
        }
    }
}
