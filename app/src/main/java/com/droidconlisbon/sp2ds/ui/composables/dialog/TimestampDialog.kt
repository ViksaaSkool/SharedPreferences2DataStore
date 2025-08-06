package com.droidconlisbon.sp2ds.ui.composables.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.droidconlisbon.sp2ds.R
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingMedium
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingStandard
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import com.droidconlisbon.sp2ds.util.getString
import com.droidconlisbon.sp2ds.util.startCountdown

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TimestampDialog(
    date: String,
    onDismiss: () -> Unit,
) {
    var deadlineValue by remember { mutableStateOf("") }

    LaunchedEffect(date) {
        startCountdown(date) { remaining ->
            deadlineValue = remaining
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(spacingStandard),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .padding(spacingMedium)
                    .wrapContentSize()
            ) {
                Text(
                    text = R.string.timestamp_dialog_title.getString(),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(spacingStandard))

                Text(
                    text = "${R.string.timestamp_dialog_text.getString()} $deadlineValue",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(spacingStandard))

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss
                ) {
                    Text(text = R.string.ok_text.getString())
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TimestampDialogPreview() = SharedPreferencesToDataStoreTheme {
    TimestampDialog(date = "") {

    }
}