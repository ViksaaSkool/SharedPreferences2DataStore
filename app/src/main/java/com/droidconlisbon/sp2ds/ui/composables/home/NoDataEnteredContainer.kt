package com.droidconlisbon.sp2ds.ui.composables.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.droidconlisbon.sp2ds.R
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingHuge
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingStandard
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import com.droidconlisbon.sp2ds.util.getString

@Composable
fun NoDataEnteredContainer(
    modifier: Modifier = Modifier,
    title: String = R.string.are_willing_to_title.getString(),
    message: String = R.string.are_willing_to_button_text.getString(),
    buttonText: String = R.string.lets_go_text.getString(),
    onButtonClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(spacingStandard)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacingStandard)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = spacingHuge)
        ) {
            Text(text = buttonText)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoDataEnteredContainerPreview() = SharedPreferencesToDataStoreTheme {
    NoDataEnteredContainer {

    }
}