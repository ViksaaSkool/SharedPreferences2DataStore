package com.droidconlisbon.sp2ds.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.droidconlisbon.sp2ds.navigation.Animations.ENTER_ANIMATION
import com.droidconlisbon.sp2ds.navigation.Animations.EXIT_ANIMATION
import com.droidconlisbon.sp2ds.navigation.Routes.DATA_SCREEN
import com.droidconlisbon.sp2ds.navigation.Routes.HOME_SCREEN
import com.droidconlisbon.sp2ds.ui.composables.data.DataScreen
import com.droidconlisbon.sp2ds.ui.composables.data.ThemeViewModel
import com.droidconlisbon.sp2ds.ui.composables.home.HomeScreen


object Routes {
    const val HOME_SCREEN = "home_screen"
    const val DATA_SCREEN = "data_screen"
}

object Animations {
    val ENTER_ANIMATION = slideInHorizontally(
        initialOffsetX = { -1000 },
        animationSpec = tween(500)
    ) + fadeIn(animationSpec = tween(500))
    val EXIT_ANIMATION = slideOutHorizontally(
        targetOffsetX = { -1000 },
        animationSpec = tween(500)
    ) + fadeOut(animationSpec = tween(500))
}


@Composable
fun Sp2DsNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val rootModifier = Modifier
        .fillMaxSize()

    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN,
        enterTransition = { ENTER_ANIMATION },
        exitTransition = { EXIT_ANIMATION }
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