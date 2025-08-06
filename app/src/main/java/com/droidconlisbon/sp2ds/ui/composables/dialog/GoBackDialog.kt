package com.droidconlisbon.sp2ds.ui.composables.dialog

import android.annotation.SuppressLint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.droidconlisbon.sp2ds.R
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import com.droidconlisbon.sp2ds.util.getString

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun GoBackDialog(
    onGoBack: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = R.string.are_you_sure_dialog_title.getString(),
                style = MaterialTheme.typography.titleLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onGoBack) {
                Text(R.string.are_you_sure_dialog_text.getString())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(R.string.are_you_sure_negative_dialog_text.getString())
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun GoBackDialogPreview() = SharedPreferencesToDataStoreTheme {
    GoBackDialog(onGoBack = {}) {
    }
}