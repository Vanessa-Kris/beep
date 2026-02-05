package ui.model

import androidx.compose.ui.graphics.Color

enum class Platform(
    val color: Color,
    val label: String
) {
    TELEGRAM(Color(0xFF2AABEE), "Telegram"),
    SIGNAL(Color(0xFF3A76F0), "Signal"),
}
