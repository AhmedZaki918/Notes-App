package com.notesapp.ui.screen.task

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.notesapp.data.model.Priority
import com.notesapp.data.model.ToDoTask
import com.notesapp.ui.viewmodel.SharedViewModel
import com.notesapp.util.Action

@Composable
fun TaskScreen(
    selectedTask: ToDoTask?,
    sharedViewModel: SharedViewModel,
    navigateToListScreen: (Action) -> Unit
) {

    val title: String by sharedViewModel.title
    val description: String by sharedViewModel.description
    val priority: Priority by sharedViewModel.priority

    val context = LocalContext.current
    BackHandler { navigateToListScreen(Action.NO_ACTION) }

    Scaffold(
        topBar = {
            TaskAppBar(
                selectedTask = selectedTask,
                navigateToListScreen = { action ->
                    if (action == Action.NO_ACTION) {
                        navigateToListScreen(action)
                    } else {
                        if (sharedViewModel.validateInputs()) {
                            navigateToListScreen(action)
                        } else {
                            Toast.makeText(
                                context,
                                "Missing inputs",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        },
        content = {
            TaskContent(
                title = title,
                onTitleChanged = {
                    sharedViewModel.title.value = it
                },
                description = description,
                onDescriptionChange = {
                    sharedViewModel.description.value = it
                },
                priority = priority,
                onPrioritySelected = {
                    sharedViewModel.priority.value = it
                }
            )
        }
    )
}