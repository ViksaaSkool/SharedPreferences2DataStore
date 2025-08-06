package com.droidconlisbon.sp2ds.ui.composables.data


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.droidconlisbon.sp2ds.R
import com.droidconlisbon.sp2ds.navigation.Routes.HOME_SCREEN
import com.droidconlisbon.sp2ds.ui.composables.dialog.GoBackDialog
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingBig
import com.droidconlisbon.sp2ds.ui.theme.Dimens.spacingSmall
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import com.droidconlisbon.sp2ds.util.getString
import com.droidconlisbon.sp2ds.util.hideSoftKeyboardOnOutsideClick
import com.droidconlisbon.sp2ds.util.showToast


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: IDataViewModel = hiltViewModel<DataViewModel>(),
    themeViewModel: IThemeViewModel = hiltViewModel<ThemeViewModel>()
) {
    val context = LocalContext.current
    val localFocusManager = LocalFocusManager.current
    val dataScreenState by viewModel.dataScreenStateFlow.collectAsState()
    val isDarkThemeState by themeViewModel.isDarkThemeStateFlow.collectAsState()
    var goBackDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = { Text(R.string.your_data_title.getString()) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (dataScreenState.canSave) {
                            goBackDialogOpen = true
                        } else {
                            navController.navigate(HOME_SCREEN)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .hideSoftKeyboardOnOutsideClick(localFocusManager)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(spacingSmall))
            ThemeToggle(isDarkTheme = isDarkThemeState) {
                themeViewModel.onThemeChanged(it)
            }
            UserComponent(
                user = dataScreenState.user,
                onImageUriChanged = {
                    viewModel.onImageUriChanged(it.toUri())
                },
                onFirstNameChanged = {
                    viewModel.onFirstNameChanged(it)
                },
                onLastNameChanged = {
                    viewModel.onLastNameChanged(it)
                })
            Spacer(modifier = Modifier.height(spacingSmall))
            CommaSeparatedComponent(
                initialValue = if (dataScreenState.description.isEmpty()) {
                    ""
                } else {
                    dataScreenState.description.toString()
                }
            ) {
                viewModel.onDescriptionChanged(it)
            }
            Spacer(modifier = Modifier.height(spacingSmall))
            AndroidKnowledgeComponent(dataScreenState.androidRate) {
                viewModel.onRateChange(it)
            }
            Spacer(modifier = Modifier.height(spacingSmall))
            DataButtons(
                isSaveEnabled = dataScreenState.canSave,
                isClearEnabled = dataScreenState.canClear, onSaveClick = {
                    viewModel.onSaveData()
                    context.run {
                        showToast(getString(R.string.magic_feature_usage_text))
                    }
                    navController.navigate(HOME_SCREEN)
                }, onClearClick = {
                    viewModel.clearData()
                })
            Spacer(modifier = Modifier.height(spacingBig))
        }
    }
    if (goBackDialogOpen) {
        GoBackDialog(onGoBack = {
            navController.navigate(HOME_SCREEN)
        }, onDismiss = {
            goBackDialogOpen = false
        })
    }

    BackHandler {
        if (dataScreenState.canSave) {
            goBackDialogOpen = true
        } else {
            navController.navigate(HOME_SCREEN)
        }

    }
}


@Preview(showBackground = true)
@Composable
fun DataScreenPreview() = SharedPreferencesToDataStoreTheme {
    val navController = rememberNavController()
    DataScreen(
        navController = navController,
        viewModel = object : IDataViewModel() {},
        themeViewModel = object : IThemeViewModel() {})
}