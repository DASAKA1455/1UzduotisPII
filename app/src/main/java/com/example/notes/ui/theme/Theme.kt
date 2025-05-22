package com.example.notes.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
private val LightColors = lightColorScheme(
    primary = Color(0xFFB3D9FF),
    onPrimary = Color.White,
    secondary = Color(0xFFFADADD),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF333333),
    surface = Color(0xFFFADADD),
    onSurface = Color(0xFF333333),
)
@Composable
fun NotesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}