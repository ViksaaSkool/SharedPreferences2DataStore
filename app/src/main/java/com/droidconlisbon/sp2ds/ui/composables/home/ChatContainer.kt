package com.droidconlisbon.sp2ds.ui.composables.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.droidconlisbon.sp2ds.R
import com.droidconlisbon.sp2ds.model.ChatMessage
import com.droidconlisbon.sp2ds.model.MessageType
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_WRITING_SPEED_VALUE
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingMedium
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingSmall
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingStandard
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingXSmall
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import com.droidconlisbon.sp2ds.util.getScreenHeight
import com.droidconlisbon.sp2ds.util.getString
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay

@SuppressLint("AutoboxingStateCreation")
@Composable
fun ChatContainer(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    avatarUri: String,
    onAvatarClick: () -> Unit,
    onAsk: (String) -> Unit
) {
    val messagesList = messages.reversed()
    var shouldAnimate by remember { mutableStateOf(false) }
    var previousSize by remember { mutableStateOf(messages.size) }

    LaunchedEffect(messages.size) {
        if (messages.size > previousSize) {
            shouldAnimate = true
        }
        previousSize = messages.size
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(spacingSmall),
            reverseLayout = true
        ) {
            items(
                messagesList.size
            ) { index ->
                ChatBubble(messagesList[index], animateAnswer = index == 0 && shouldAnimate) {
                    shouldAnimate = false
                }
                Spacer(modifier = Modifier.height(spacingSmall))
            }
        }
        Spacer(modifier = Modifier.height(spacingSmall))
        if (isLoading) {
            Loading()
            Spacer(modifier = Modifier.height(spacingSmall))
        }
        AskFiled(
            avatarUri = avatarUri,
            onAvatarClick = { onAvatarClick() },
            onAsk = { query -> onAsk(query) })
        Spacer(modifier = Modifier.height(spacingStandard))
    }
}

@Preview(showBackground = true)
@Composable
fun ChatContainerPreview() = SharedPreferencesToDataStoreTheme {
    ChatContainer(
        messages = listOf(
            ChatMessage("This is a test Question", MessageType.QUESTION),
            ChatMessage("This is a test Answer", MessageType.ANSWER)
        ),
        isLoading = false,
        avatarUri = "",
        onAvatarClick = {},
        onAsk = {}
    )
}

@Composable
fun Loading() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("loading.lottie"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .height(getScreenHeight() / 8)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() = SharedPreferencesToDataStoreTheme {
    Loading()
}


@Composable
fun ChatBubble(message: ChatMessage, animateAnswer: Boolean = false, onFinished: () -> Unit) {
    val isUser = message.messageType == MessageType.QUESTION
    val bubbleColor =
        if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(bubbleColor, shape = RoundedCornerShape(spacingMedium))
                .padding(spacingMedium)
                .widthIn(max = 300.dp)
        ) {
            if (isUser) {
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                if (animateAnswer) {
                    TypewriterText(fullText = message.message) {
                        onFinished()
                    }
                } else {
                    MarkdownText(
                        markdown = message.message,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubblePreview() = SharedPreferencesToDataStoreTheme {
    Column {
        ChatBubble(
            ChatMessage("This is a test Question", MessageType.QUESTION)
        ) {}
        Spacer(modifier = Modifier.height(spacingMedium))
        ChatBubble(
            ChatMessage("This is a test Answer", MessageType.ANSWER),
            animateAnswer = true
        ) {}
    }

}

@Composable
fun TypewriterText(
    fullText: String,
    modifier: Modifier = Modifier,
    delayMillis: Long = DEFAULT_WRITING_SPEED_VALUE,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    onFinished: () -> Unit = {}
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(fullText) {
        displayedText = ""
        fullText.forEachIndexed { index, _ ->
            displayedText = fullText.take(index + 1)
            delay(delayMillis)
        }
        onFinished()
    }
    MarkdownText(markdown = displayedText, style = textStyle, modifier = modifier)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AskFiled(
    modifier: Modifier = Modifier,
    avatarUri: String,
    onAvatarClick: () -> Unit, onAsk: (String) -> Unit
) {

    var input by remember { mutableStateOf("") }
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        GlideImage(
            model = avatarUri,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .padding(spacingXSmall)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
                .clickable { onAvatarClick() },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(spacingSmall))
        TextField(
            modifier = Modifier.weight(1f),
            value = input,
            onValueChange = { input = it },
            placeholder = { Text(R.string.ask_me_anything_text.getString()) },
            maxLines = 50,
            shape = RoundedCornerShape(spacingStandard),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (input.isNotBlank()) {
                    onAsk(input)
                    input = ""
                }
            }),
            colors = TextFieldDefaults.colors().copy(
                disabledTextColor = Color.Unspecified,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.Gray,
            )
        )

        IconButton(
            onClick = {
                if (input.isNotBlank()) {
                    onAsk(input)
                    input = ""
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AskFieldPreview() = SharedPreferencesToDataStoreTheme {
    AskFiled(avatarUri = "https://tinyurl.com/yq3qml5a", onAvatarClick = {}, onAsk = {})
}