package ui.components

import androidx.annotation.DrawableRes
import com.example.beep.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

enum class Platform(@DrawableRes val iconRes: Int) {
    NONE(0),
    TELEGRAM(R.drawable.ic_telegram),
    SIGNAL(R.drawable.ic_signal),
}

@Composable
fun Avatar(
    initials: String,
    platform: Platform = Platform.NONE,
    backgroundColor: Color? = null
) {

    val avatarColor = backgroundColor ?: remember(initials) {
        val colors = listOf(
            Color(0xFF6366F1), // Indigo
            Color(0xFFEC4899), // Pink
            Color(0xFF8B5CF6), // Purple
            Color(0xFF10B981), // Green
            Color(0xFFF59E0B), // Amber
            Color(0xFF3B82F6), // Blue
            Color(0xFFEF4444), // Red
            Color(0xFF06B6D4), // Cyan
            Color(0xFF84CC16), // Lime
            Color(0xFFF97316)  // Orange
        )
        colors[initials.hashCode().absoluteValue % colors.size]
    }

    Box(
        modifier = Modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = avatarColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (platform != Platform.NONE && platform.iconRes != 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = platform.iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}
