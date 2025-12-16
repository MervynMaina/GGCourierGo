package com.mervyn.ggcouriergo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun GGCourierLogo(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.10f

        // Outer "G"
        drawArc(
            color = color,
            startAngle = 45f,
            sweepAngle = 280f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Inner "G" / Arrow head path
        drawArc(
            color = color,
            startAngle = 45f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(size.width * 0.25f, size.height * 0.25f),
            size = size * 0.5f,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Horizontal bar for the G
        drawLine(
            color = color,
            start = Offset(size.width * 0.55f, size.height * 0.5f),
            end = Offset(size.width * 0.9f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}