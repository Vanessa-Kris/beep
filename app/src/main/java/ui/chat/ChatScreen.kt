package ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    threadName: String,
    onBackClick: () -> Unit
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                Message("Hey!", false, "9:41 AM"),
                Message("Are you around?", false, "9:41 AM"),
                Message("Yep 👋", true, "9:42 AM"),
                Message("Cool, let’s meet later", false, "9:45 AM"),
                Message("Sounds good!", true, "10:00 AM"),
                Message("See you :)", true, "10:02 AM"),
                Message("Call me okay?", true, "10:04 AM")
            )
        )
    }

    val listState = rememberLazyListState()

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(threadName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
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

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    reverseLayout = true
                ) {
                    itemsIndexed(messages.reversed()) { index, message ->
                        val realIndex = messages.lastIndex - index
                        val meta = bubbleMeta(realIndex, messages)

                        MessageBubble(
                            message = message,
                            meta = meta
                        )
                    }
                }

                MessageInput(
                    onSend = { newText ->
                        messages = messages + Message(
                            text = newText,
                            isMine = true,
                            timestamp = "Now"
                        )
                    }
                )
            }
        }
    }

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(0)
    }
}

fun bubbleMeta(
    index: Int,
    messages: List<Message>
): BubbleMeta {
    val current = messages[index]
    val prev = messages.getOrNull(index - 1)
    val next = messages.getOrNull(index + 1)

    val sameAsPrev = prev?.isMine == current.isMine
    val sameAsNext = next?.isMine == current.isMine

    return BubbleMeta(
        isFirst = !sameAsPrev,
        isLast = !sameAsNext,
        showTimestamp = !sameAsNext
    )
}

data class BubbleMeta(
    val isFirst: Boolean,
    val isLast: Boolean,
    val showTimestamp: Boolean
)
