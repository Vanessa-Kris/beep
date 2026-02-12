package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.beep.R

enum class ChatFilter {
    ALL_CHATS,
    SIGNAL,
    TELEGRAM
}

@Composable
fun DrawerContent(
    selectedFilter: ChatFilter,
    onFilterSelected: (ChatFilter) -> Unit,
    onAddNetwork: () -> Unit,
    onSettings: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(vertical = 24.dp)
    ) {

        Text(
            text = (stringResource(R.string.app_name)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(16.dp))

        DrawerMenuItem(
            icon = painterResource(id = R.drawable.inbox),
            label = (stringResource(R.string.all_chats)),
            selected = selectedFilter == ChatFilter.ALL_CHATS,
            onClick = {
                onFilterSelected(ChatFilter.ALL_CHATS)
                onClose()
            }
        )

        DrawerMenuItemWithPlatform(
            platform = Platform.SIGNAL,
            label = (stringResource(R.string.signal)),
            selected = selectedFilter == ChatFilter.SIGNAL,
            onClick = {
                onFilterSelected(ChatFilter.SIGNAL)
                onClose()
            }
        )

        DrawerMenuItemWithPlatform(
            platform = Platform.TELEGRAM,
            label = (stringResource(R.string.telegram)),
            selected = selectedFilter == ChatFilter.TELEGRAM,
            onClick = {
                onFilterSelected(ChatFilter.TELEGRAM)
                onClose()
            }
        )

        DrawerMenuItem(
            icon = Icons.Default.Add,
            label = (stringResource(R.string.add_network)),
            selected = false,
            onClick = {
                onAddNetwork()
                onClose()
            }
        )

        Spacer(Modifier.weight(1f))

//        // Settings at bottom
//        HorizontalDivider(
//            modifier = Modifier.padding(horizontal = 16.dp),
//            color = MaterialTheme.colorScheme.outlineVariant
//        )

        Spacer(Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.Settings,
            label = (stringResource(R.string.settings)),
            selected = false,
            onClick = {
                onSettings()
                onClose()
            }
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: Any,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = if (selected)
            MaterialTheme.colorScheme.secondaryContainer
        else
            Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                is androidx.compose.ui.graphics.painter.Painter -> Icon(
                    painter = icon,
                    contentDescription = label,
                    tint = if (selected)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
private fun DrawerMenuItemWithPlatform(
    platform: Platform,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = if (selected)
            MaterialTheme.colorScheme.secondaryContainer
        else
            Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = platform.iconRes),
                contentDescription = label,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
