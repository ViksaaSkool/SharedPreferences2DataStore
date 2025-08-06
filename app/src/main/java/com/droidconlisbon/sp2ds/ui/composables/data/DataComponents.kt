package com.droidconlisbon.sp2ds.ui.composables.data

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.droidconlisbon.sp2ds.R
import com.droidconlisbon.sp2ds.model.User
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingHuge
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingMedium
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingStandard
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import com.droidconlisbon.sp2ds.util.createUriForImage
import com.droidconlisbon.sp2ds.util.getString
import com.droidconlisbon.sp2ds.util.requestPermissionAndLaunch
import com.droidconlisbon.sp2ds.util.showToast
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserComponent(
    modifier: Modifier = Modifier,
    user: User,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onImageUriChanged: (String) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacingStandard),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Circle image with Glide
        if (user.picUri.isNotEmpty()) {
            GlideImage(
                model = user.picUri,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(spacingStandard))

        // Upload/Edit Button
        TextButton(onClick = {
            showBottomSheet = true
        }) {
            Text(
                if (user.picUri.isNotEmpty()) {
                    R.string.edit_photo_text.getString()
                } else {
                    R.string.upload_photo_text.getString()
                }
            )
        }

        Spacer(modifier = Modifier.height(spacingStandard))

        // Editable first name
        OutlinedTextField(
            value = user.firstName,
            onValueChange = {
                onFirstNameChanged(it)
            },
            label = { Text(R.string.name_text.getString()) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacingStandard))
        OutlinedTextField(
            value = user.lastName,
            onValueChange = {
                onLastNameChanged(it)
            },
            label = { Text(R.string.surname_text.getString()) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showBottomSheet) {
        ImageSourceBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            onImagePicked = { uri ->
                showBottomSheet = false
                onImageUriChanged(uri.toString())
            },
            sheetState = bottomSheetState
        )
    }
}


@Preview(showBackground = true)
@Composable
fun UserComponentPreview() = SharedPreferencesToDataStoreTheme {
    UserComponent(
        user = User(),
        onImageUriChanged = {},
        onLastNameChanged = {},
        onFirstNameChanged = {})
}


@Composable
fun ThemeToggle(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onToggleTheme: (Boolean) -> Unit
) {
    var themeValue by remember { mutableStateOf(isDarkTheme) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacingStandard),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (themeValue) {
                R.string.dark_theme_text.getString()
            } else {
                R.string.light_theme_text.getString()
            },
            style = MaterialTheme.typography.titleMedium
        )
        Switch(
            checked = themeValue,
            onCheckedChange = {
                themeValue = it
                onToggleTheme(it)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeTogglePreview() = SharedPreferencesToDataStoreTheme {
    ThemeToggle {
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AndroidKnowledgeComponent(value: Float, onValueChange: (Float) -> Unit) {
    Column(
        modifier = Modifier
            .padding(spacingStandard)
            .fillMaxWidth()
    ) {
        Text(
            text = R.string.rate_android_knowledge_text.getString(),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(spacingStandard))

        Text(
            text = String.format("%.1f", value),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Slider(
            value = value,
            onValueChange = {
                onValueChange(it.coerceIn(1f, 10f))
            },
            valueRange = 1f..10f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AndroidKnowledgeComponentPreview() = SharedPreferencesToDataStoreTheme {
    AndroidKnowledgeComponent(5.9f) {

    }
}

@Composable
fun CommaSeparatedComponent(
    initialValue: String = "",
    onValuesChange: (List<String>) -> Unit
) {
    val errorText = R.string.three_word_error_text.getString()
    var text by remember { mutableStateOf(initialValue) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacingStandard)
    ) {

        Text(
            text = R.string.three_word_description_text.getString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                val parts = it.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }
                if (parts.size == 3) {
                    error = null
                    onValuesChange(parts)
                } else {
                    error = errorText
                }
            },
            label = { Text(R.string.three_word_hint_text.getString()) },
            isError = error != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommaSeparatedComponentPreview() = SharedPreferencesToDataStoreTheme {
    CommaSeparatedComponent {

    }
}

@Composable
fun DataButtons(
    isSaveEnabled: Boolean = false,
    isClearEnabled: Boolean = false,
    onSaveClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacingStandard),
        horizontalArrangement = Arrangement.spacedBy(
            spacingStandard,
            Alignment.CenterHorizontally
        )
    ) {
        Button(onClick = {
            onSaveClick()
        }, enabled = isSaveEnabled) {
            Text(text = R.string.save_data_button_text.getString())
        }
        Button(onClick = {
            onClearClick()
        }, enabled = isClearEnabled) {
            Text(text = R.string.clear_data_button_text.getString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DataButtonsPreview() = SharedPreferencesToDataStoreTheme {
    DataButtons(isSaveEnabled = true, onSaveClick = {}, onClearClick = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSourceBottomSheet(
    onDismissRequest: () -> Unit,
    onImagePicked: (Uri) -> Unit,
    sheetState: SheetState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        Timber.d("ImageSourceBottomSheet() | success = $success")
        if (success) {
            cameraImageUri.value?.let { uri ->
                onImagePicked(uri)
            }
        }
        onDismissRequest()
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            onImagePicked(it)
        }
        onDismissRequest()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        with(context) {
            showToast(
                if (isGranted) getString(R.string.permission_granted_text)
                else getString(R.string.permission_denied_text)
            )
        }
    }

    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val cameraPermission = android.Manifest.permission.CAMERA

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(Modifier.padding(spacingMedium)) {
            Text(
                text = R.string.modal_title.getString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(spacingHuge))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    spacingStandard,
                    Alignment.CenterHorizontally
                )
            ) {
                OutlinedButton(onClick = {
                    context.requestPermissionAndLaunch(
                        permission = cameraPermission,
                        permissionLauncher = permissionLauncher
                    ) {
                        scope.launch {
                            val uri = context.createUriForImage() ?: return@launch
                            Timber.d("ImageSourceBottomSheet() |  uri = $uri")
                            cameraImageUri.value = uri
                            takePictureLauncher.launch(uri)
                        }
                    }
                }) {
                    Text(text = R.string.take_picture_text.getString())
                }

                OutlinedButton(onClick = {
                    context.requestPermissionAndLaunch(
                        permission = mediaPermission,
                        permissionLauncher = permissionLauncher
                    ) {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                }) {
                    Text(text = R.string.pick_from_gallery_text.getString())
                }
            }
            Spacer(Modifier.height(spacingStandard))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ImageSourceBottomSheetPreview() = SharedPreferencesToDataStoreTheme {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ImageSourceBottomSheet(
        onDismissRequest = {},
        onImagePicked = {},
        bottomSheetState
    )
}
