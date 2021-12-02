package com.karloqvist.timetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.karloqvist.timetracker.database.TaskDatabaseDao
import com.karloqvist.timetracker.task.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author Karl Öqvist
 *
 * ViewModel that is shared between MainActivity, ActivitiesFragment and StatisticsFragment
 * for handling the all tasks and communication with database.
 */

class TaskViewModel(
    private val database: TaskDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val allTasks = database.getAllTasks()

    fun insertTask(task: Task) {
        viewModelScope.launch {
            insert(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            delete(task)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            deleteAll()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            update(task)
        }
    }

    private suspend fun insert(task: Task) {
        withContext(Dispatchers.IO) {
            database.insert(task)
        }
    }

    private suspend fun update(task: Task) {
        withContext(Dispatchers.IO) {
            database.update(task)
        }
    }

    private suspend fun delete(task: Task) {
        withContext(Dispatchers.IO) {
            database.delete(task)
        }
    }

    private suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            database.deleteAll()
        }
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return allTasks
    }

    fun getTask(index: Int): Task? {
        return allTasks.value?.get(index)
    }
}

