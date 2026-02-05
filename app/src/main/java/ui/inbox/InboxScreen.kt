package ui.inbox

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FilterNone
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.draw.clip
import ui.components.ComposeSheetContent
import ui.components.Platform
import java.lang.Thread
import kotlin.text.get


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onThreadClick: (String) -> Unit,
    onComposeClick: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var showComposeSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedTab by remember { mutableStateOf(0) }

    var threads by remember {
        mutableStateOf(
            listOf(
        Thread("Kehinde Omobaba", "Check your mails asap", "Yesterday", ui.model.Platform.SIGNAL),
        Thread("Alice", "Hey, how are you?", "09:45AM", ui.model.Platform.SIGNAL),
        Thread("Bob", "See you later", "9:00AM", ui.model.Platform.TELEGRAM),
        Thread("Mom", "Call me pls", "6:00AM", ui.model.Platform.TELEGRAM),
        Thread("Yaya", "I could have never guessed ...", "Yesterday", ui.model.Platform.SIGNAL),
        Thread("Yager", "Change up quickly", "Yesterday", ui.model.Platform.TELEGRAM),
        Thread("Max", "We scheduled it for April 13th", "Saturday", ui.model.Platform.TELEGRAM),
        Thread("Will Byers", "I've literally been missing for 2 months", "Friday", ui.model.Platform.SIGNAL),
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chats")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* settings / stacks */ }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterNone,
                            contentDescription = "Settings",
                            modifier = Modifier
                                .size(20.dp)
                                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* filter */ }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier
                                .size(20.dp)
                                .background(color = MaterialTheme.colorScheme.surface, shape = CircleShape),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Search chats") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 44.dp),
                            shape = CircleShape,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showComposeSheet = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Compose",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {Spacer(modifier = Modifier.height(8.dp))

                    InboxArchiveToggle(
                        selected = selectedTab,
                        onSelect = { selectedTab = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(
                        threads.filter {
                            it.name.contains(query, ignoreCase = true) ||
                                    it.lastMessage.contains(query, ignoreCase = true)
                        },
                        key = { it.name }
                    ) { thread ->
                        ThreadItem(
                            name = thread.name,
                            lastMessage = thread.lastMessage,
                            time = thread.time,
                            platform = thread.platform,
                            onClick = { onThreadClick(thread.name) },
                            onDelete = {
                                threads = threads.filterNot { it.name == thread.name }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showComposeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showComposeSheet = false },
            sheetState = sheetState
        ) {
            ComposeSheetContent(
                onStartChat = { name ->
                    showComposeSheet = false
                    onThreadClick(name)
                },
                onDismiss = { showComposeSheet = false }
            )
        }
    }
}


@Composable
private fun InboxArchiveToggle(
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val indicatorOffset by animateDpAsState(
        targetValue = if (selected == 0) 0.dp else 72.dp,
        label = "pill_offset"
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.wrapContentWidth()
    ) {
        Box {
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .padding(3.dp)
                    .size(width = 66.dp, height = 30.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(14.dp)
                    )
            )

            Row(
                modifier = Modifier.padding(3.dp)
            ) {
                ToggleText(
                    text = "Inbox",
                    selected = selected == 0,
                    onClick = { onSelect(0) }
                )
                ToggleText(
                    text = "Archive",
                    selected = selected == 1,
                    onClick = { onSelect(1) }
                )
            }
        }
    }
}


@Composable
private fun ToggleText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(66.dp)
            .height(30.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



data class Thread(
    val name: String,
    val lastMessage: String,
    val time: String,
    val platform: ui.model.Platform
)
