package com.droidconlisbon.sp2ds.ui.composables.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.droidconlisbon.sp2ds.R
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingMedium
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingStandard
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import com.droidconlisbon.sp2ds.util.getString

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun OnboardingDialog(
    onAgreeClick: () -> Unit,
    onDisagreeClick: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(spacingStandard),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .width(screenWidth * 0.9f)
                .height(screenHeight * 0.75f)
        ) {
            Column(
                modifier = Modifier
                    .padding(spacingMedium)
                    .fillMaxSize()
            ) {
                Text(
                    text = R.string.onboarding_dialog_title.getString(),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(spacingStandard))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = R.string.onboarding_dialog_text.getString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(spacingStandard))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDisagreeClick) {
                        Text(text = R.string.onboarding_am_no_text.getString())
                    }
                    TextButton(onClick = onAgreeClick) {
                        Text(text = R.string.onboarding_i_agree_text.getString())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingDialogPreview() = SharedPreferencesToDataStoreTheme {
    OnboardingDialog(onDisagreeClick = {}, onAgreeClick = {}) {

    }
}