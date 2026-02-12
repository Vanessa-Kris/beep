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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FilterNone
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ui.components.ChatFilter
import ui.components.ComposeSheetContent
import ui.components.DrawerContent
import ui.model.Platform
import com.example.beep.R
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onThreadClick: (String) -> Unit,
    navController: NavController,
    onComposeClick: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var showComposeSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedTab by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf(ChatFilter.ALL_CHATS) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState()


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var threads by remember {
        mutableStateOf(
            listOf(
        Thread("Kehinde Omobaba", "Check your mails asap", "Yesterday", Platform.SIGNAL),
        Thread("Alice", "Hey, how are you?", "09:45AM", Platform.SIGNAL),
        Thread("Bob", "See you later", "9:00AM", Platform.TELEGRAM),
        Thread("Mom", "Call me pls", "6:00AM", Platform.TELEGRAM),
        Thread("Yaya", "I could have never guessed ...", "Yesterday", Platform.SIGNAL),
        Thread("Yager", "Change up quickly", "Yesterday", Platform.TELEGRAM),
        Thread("Max", "We scheduled it for April 13th", "Saturday", Platform.TELEGRAM),
        Thread("Will Byers", "I've literally been missing for 2 months", "Friday", Platform.SIGNAL),
            )
        )
    }

    val filteredThreads = threads.filter { thread ->
        val matchesSearch = thread.name.contains(query, ignoreCase = true) ||
                thread.lastMessage.contains(query, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            ChatFilter.ALL_CHATS -> true
            ChatFilter.SIGNAL -> thread.platform == Platform.SIGNAL
            ChatFilter.TELEGRAM -> thread.platform == Platform.TELEGRAM
        }

        matchesSearch && matchesFilter
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                DrawerContent(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    onAddNetwork = { /* Handle add network */ },
                    onSettings = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("settings")
                    },
                    onClose = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.chats))
                    }
                },
                windowInsets = TopAppBarDefaults.windowInsets,
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterNone,
                            contentDescription = (stringResource(R.string.settings)),
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
                            contentDescription = (stringResource(R.string.filter)),
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
                            placeholder = { Text(text = stringResource(R.string.search_chats)) },
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

                    SmallFloatingActionButton(
                        onClick = { showComposeSheet = true },
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = (stringResource(R.string.compose)),
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
                .padding(padding)
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
                        .padding(horizontal = 16.dp),
//                        .imePadding(),
                contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(
                        filteredThreads,
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
}
    if (showComposeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showComposeSheet = false },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight(0.75f)
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
    val inboxText = stringResource(R.string.inbox)
    val archiveText = stringResource(R.string.archive)

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.bodySmall

    val inboxWidth = remember(inboxText) {
        with(density) {
            textMeasurer.measure(inboxText, textStyle).size.width.toDp() + 24.dp
        }
    }

    val archiveWidth = remember(archiveText) {
        with(density) {
            textMeasurer.measure(archiveText, textStyle).size.width.toDp() + 24.dp
        }
    }

    val indicatorWidth by animateDpAsState(
        targetValue = if (selected == 0) inboxWidth else archiveWidth,
        label = "pill_width"
    )

    val indicatorOffset by animateDpAsState(
        targetValue = if (selected == 0) 0.dp else inboxWidth,
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
                    .size(width = indicatorWidth, height = 30.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(14.dp)
                    )
            )

            Row(
                modifier = Modifier.padding(3.dp)
            ) {
                ToggleText(
                    text = inboxText,
                    selected = selected == 0,
                    onClick = { onSelect(0) },
                    width = inboxWidth
                )
                ToggleText(
                    text = archiveText,
                    selected = selected == 1,
                    onClick = { onSelect(1) },
                    width = archiveWidth
                )
            }
        }
    }
}

@Composable
private fun ToggleText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    width: Dp
) {
    Box(
        modifier = Modifier
            .width(width)
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
    val platform: Platform
)
