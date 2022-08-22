package com.notesapp.navigation

import androidx.navigation.NavHostController
import com.notesapp.data.local.Constants.LIST_SCREEN
import com.notesapp.data.local.Constants.SPLASH_SCREEN
import com.notesapp.util.Action

class Screens(navController: NavHostController) {

    val splash: () -> Unit = {
        navController.navigate("list/${Action.NO_ACTION}") {
            popUpTo(SPLASH_SCREEN) { inclusive = true }
        }
    }

    val task: (Action) -> Unit = { action ->
        navController.navigate("list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }

    val list: (Int) -> Unit = { taskId ->
        navController.navigate("task/$taskId")
    }
}