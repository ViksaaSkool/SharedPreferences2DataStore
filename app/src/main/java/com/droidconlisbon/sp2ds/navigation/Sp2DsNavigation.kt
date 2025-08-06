package com.droidconlisbon.sp2ds.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.droidconlisbon.sp2ds.navigation.Routes.DATA_SCREEN
import com.droidconlisbon.sp2ds.navigation.Routes.HOME_SCREEN
import com.droidconlisbon.sp2ds.ui.composables.data.DataScreen
import com.droidconlisbon.sp2ds.ui.composables.data.ThemeViewModel
import com.droidconlisbon.sp2ds.ui.composables.home.HomeScreen

object Routes {
    const val HOME_SCREEN = "home_screen"
    const val DATA_SCREEN = "data_screen"
}

@Composable
fun Sp2DsNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val rootModifier = Modifier
        .fillMaxSize()

    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(HOME_SCREEN) {
            HomeScreen(
                modifier = rootModifier.padding(
                    WindowInsets.systemBars.asPaddingValues()
                ),
                navController = navController
            )
        }
        composable(DATA_SCREEN) {
            DataScreen(
                modifier = rootModifier,
                navController = navController,
                themeViewModel = themeViewModel
            )
        }
    }
}