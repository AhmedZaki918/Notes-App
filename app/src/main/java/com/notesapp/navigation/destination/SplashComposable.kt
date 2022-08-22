package com.notesapp.navigation.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.notesapp.data.local.Constants
import com.notesapp.data.local.Constants.SPLASH_SCREEN
import com.notesapp.ui.screen.splash.SplashScreen


fun NavGraphBuilder.splashComposable(
    navigateToListScreen: () -> Unit
) {

    composable(
        route = SPLASH_SCREEN
    ) {
        SplashScreen(navigateToListScreen)
    }
}