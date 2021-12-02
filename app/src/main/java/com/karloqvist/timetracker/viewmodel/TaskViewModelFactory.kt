package com.karloqvist.timetracker.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.karloqvist.timetracker.database.TaskDatabaseDao

/**
 * @author Karl Öqvist
 *
 * Custom ViewModelFactory used for TaskViewModel
 */

class TaskViewModelFactory(
    private val dataSource: TaskDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}