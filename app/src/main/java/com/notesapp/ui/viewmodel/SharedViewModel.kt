package com.notesapp.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesapp.data.model.Priority
import com.notesapp.data.model.ToDoTask
import com.notesapp.data.repository.DataStoreRepo
import com.notesapp.data.repository.ToDoRepo
import com.notesapp.util.Action
import com.notesapp.util.RequestState
import com.notesapp.util.SearchAppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repo: ToDoRepo,
    private val dataStoreRepo: DataStoreRepo
) : ViewModel() {

    val action: MutableState<Action> = mutableStateOf(Action.NO_ACTION)

    // For task content
    val id: MutableState<Int> = mutableStateOf(0)
    val title: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val priority: MutableState<Priority> = mutableStateOf(Priority.LOW)

    // For search app bar
    val searchAppBarState: MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSED)
    val searchTextState: MutableState<String> = mutableStateOf("")

    // For searching tasks in database
    private val _searchedTasks =
        MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.Idle)
    val searchedTasks: StateFlow<RequestState<List<ToDoTask>>> = _searchedTasks

    // For all tasks
    private val _allTasks =
        MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.Idle)
    val allTasks: StateFlow<RequestState<List<ToDoTask>>> = _allTasks

    // For selected task
    private val _selectedTask: MutableStateFlow<ToDoTask?> = MutableStateFlow(null)
    val selectedTask: StateFlow<ToDoTask?> = _selectedTask

    // For saving sort state
    private val _sortState =
        MutableStateFlow<RequestState<Priority>>(RequestState.Idle)
    val sortState: StateFlow<RequestState<Priority>> = _sortState

    // For sorting the list
    val lowPriorityTasks: StateFlow<List<ToDoTask>> =
        repo.sortByLowPriority.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val highPriorityTasks: StateFlow<List<ToDoTask>> =
        repo.sortByHighPriority.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            emptyList()
        )


    init {
        getAllTasks()
        readSortState()
    }


    private fun readSortState() {
        _sortState.value = RequestState.Loading
        try {
            viewModelScope.launch {
                dataStoreRepo.readSortState
                    .map { Priority.valueOf(it) }
                    .collect {
                        _sortState.value = RequestState.Success(it)
                    }
            }
        } catch (e: Exception) {
            _sortState.value = RequestState.Error(e)
        }
    }


    fun persistSortState(priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepo.writeSortState(priority)
        }
    }

    fun searchDatabase(searchQuery: String) {
        _searchedTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repo.searchDatabase(searchQuery = "%$searchQuery%").collect { searchedTasks ->
                    _searchedTasks.value = RequestState.Success(searchedTasks)
                }
            }
        } catch (e: Exception) {
            _searchedTasks.value = RequestState.Error(e)
        }
        searchAppBarState.value = SearchAppBarState.TRIGGERED
    }


    private fun getAllTasks() {
        _allTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repo.getAllTasks.collect {
                    _allTasks.value = RequestState.Success(it)
                }
            }
        } catch (e: Exception) {
            _allTasks.value = RequestState.Error(e)
        }
    }


    fun getSelectedTask(taskId: Int) {
        viewModelScope.launch {
            repo.getSelectedTask(taskId).collect { task ->
                _selectedTask.value = task
            }
        }
    }


    fun updateTaskFields(selectedTask: ToDoTask?) {
        if (selectedTask != null) {
            id.value = selectedTask.id
            title.value = selectedTask.title
            description.value = selectedTask.description
            priority.value = selectedTask.priority
        } else {
            id.value = 0
            title.value = ""
            description.value = ""
            priority.value = Priority.LOW
        }
    }


    fun validateInputs(): Boolean {
        return title.value.isNotEmpty() && description.value.isNotEmpty()
    }


    fun handleDatabaseActions(action: Action) {
        when (action) {
            Action.ADD -> {
                addTask()
            }
            Action.UPDATE -> {
                updateTask()
            }
            Action.DELETE -> {
                deleteTask()
            }
            Action.DELETE_ALL -> {
                deleteAllTasks()
            }
            else -> {}
        }
    }


    private fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addTask(
                ToDoTask(
                    title = title.value,
                    description = description.value,
                    priority = priority.value
                )
            )
        }
        searchAppBarState.value = SearchAppBarState.CLOSED
    }


    private fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateTask(
                ToDoTask(
                    id = id.value,
                    title = title.value,
                    description = description.value,
                    priority = priority.value
                )
            )
        }
    }


    private fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteTask(
                ToDoTask(
                    id = id.value,
                    title = title.value,
                    description = description.value,
                    priority = priority.value
                )
            )
        }
    }


    private fun deleteAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAllTasks()
        }
    }
}