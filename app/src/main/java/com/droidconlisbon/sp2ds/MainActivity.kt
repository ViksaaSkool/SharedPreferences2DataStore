package com.droidconlisbon.sp2ds

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.droidconlisbon.sp2ds.navigation.Sp2DsNavigation
import com.droidconlisbon.sp2ds.ui.composables.data.ThemeViewModel
import com.droidconlisbon.sp2ds.ui.theme.SharedPreferencesToDataStoreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themeViewModel.isDarkThemeStateFlow.collectAsState()
            SharedPreferencesToDataStoreTheme(isDarkTheme) {
                SystemBarStyler(darkTheme = isDarkTheme)
                Sp2DsNavigation(themeViewModel = themeViewModel)
            }
        }
    }
}


@SuppressLint("ContextCastToActivity")
@Composable
fun SystemBarStyler(
    darkTheme: Boolean
) {
    val activity = LocalContext.current as Activity
    val window = activity.window
    val useDarkIcons = !darkTheme

    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = useDarkIcons
            isAppearanceLightNavigationBars = useDarkIcons
        }
    }
}

