package com.droidconlisbon.sp2ds.ui.composables.home

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.droidconlisbon.sp2ds.navigation.Routes.DATA_SCREEN
import com.droidconlisbon.sp2ds.ui.composables.data.DataScreenState
import com.droidconlisbon.sp2ds.ui.composables.data.logData
import com.droidconlisbon.sp2ds.ui.composables.dialog.OnboardingDialog
import com.droidconlisbon.sp2ds.ui.composables.dialog.TimestampDialog
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import com.droidconlisbon.sp2ds.util.hideSoftKeyboardOnOutsideClick
import com.droidconlisbon.sp2ds.util.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber


@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, navController: NavHostController,
    viewModel: IHomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val activity = LocalContext.current as? Activity
    val localFocusManager = LocalFocusManager.current
    var isTimestampDialogOpen by remember { mutableStateOf(false) }
    val homeScreenDataState by viewModel.homeScreenDataStateFlow.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val cameFromDataScreenFlow = remember {
        savedStateHandle?.getStateFlow("cameFromDataScreen", false)
    }
    val cameFromDataScreen by cameFromDataScreenFlow?.collectAsState() ?: remember { mutableStateOf(false) }
    LaunchedEffect(cameFromDataScreen) {
        if (cameFromDataScreen) {
            viewModel.refreshData()
            savedStateHandle?.set("cameFromDataScreen", false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        with(homeScreenDataState) {
            if (!isInitialized) {
                return
            }
            hasBeenOnboarded?.let {
                if (!it) {
                    OnboardingDialog(onDisagreeClick = {
                        activity?.finishAffinity()
                    }, onAgreeClick = { viewModel.onTermsAccepted() })
                }
            }
            if (hasPopulatedData) {
                ChatContainer(
                    modifier = modifier.hideSoftKeyboardOnOutsideClick(localFocusManager),
                    avatarUri = avatarUri,
                    isLoading = isLoading,
                    messages = chatMessages,
                    onAvatarClick = {
                        savedStateHandle?.set("cameFromDataScreen", true)
                        navController.navigate(DATA_SCREEN)
                    },
                    onAsk = { query ->
                        viewModel.onChat(query)
                        isTimestampDialogOpen = true
                    })
                if (timeOutTimestamp.isNotEmpty() && isTimestampDialogOpen) {
                    TimestampDialog(timeOutTimestamp) {
                        isTimestampDialogOpen = false
                    }
                }
            } else {
                NoDataEnteredContainer(modifier = modifier) {
                    savedStateHandle?.set("cameFromDataScreen", true)
                    navController.navigate(DATA_SCREEN)
                }
            }
            if (errorMessage.isNotEmpty()) {
                activity?.showToast(errorMessage)
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun HomeContainerPreview() = SharedPreferencesToDataStoreTheme {
    val navController = rememberNavController()
    HomeScreen(navController = navController, viewModel = object : IHomeViewModel() {
        override val homeScreenDataStateFlow: MutableStateFlow<HomeScreenDataState> =
            MutableStateFlow(HomeScreenDataState().copy(isInitialized = true))
    })
}