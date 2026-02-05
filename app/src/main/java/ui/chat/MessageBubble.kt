package ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.Timestamp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.scaleIn
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import kotlin.div


data class Message(
    val text: String,
    val isMine: Boolean,
    val timestamp: String,
)

@Composable
fun MessageBubble(
    message: Message,
    meta: BubbleMeta
) {
    val shape = RoundedCornerShape(
        topStart = if (message.isMine) 20.dp else if (meta.isFirst) 20.dp else 4.dp,
        topEnd = if (!message.isMine) 20.dp else if (meta.isFirst) 20.dp else 4.dp,
        bottomStart = if (message.isMine) 20.dp else if (meta.isLast) 20.dp else 4.dp,
        bottomEnd = if (!message.isMine) 20.dp else if (meta.isLast) 20.dp else 4.dp
    )

    Column(
        horizontalAlignment = if (message.isMine)
            Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Surface(
            shape = shape,
            color = if (message.isMine)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (meta.showTimestamp) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

