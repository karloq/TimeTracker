package com.karloqvist.timetracker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.karloqvist.timetracker.task.Task

/**
 * @author Karl Öqvist
 *
 * ViewModel that is shared between RunningFragment and ActivitiesFragment for handling the current
 * task that is running and save state over configuration change and process death
 */

class RunningTaskViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val taskRunningStopped = MutableLiveData<Boolean>()
    private val taskToBeRun = MutableLiveData<Task>()

    //Gets observed by ActivitiesFragment
    val taskStoppedTask: MutableLiveData<Boolean> get() = taskRunningStopped

    //Gets observed by RunningFragment
    val taskToBeRunTask: MutableLiveData<Task> get() = taskToBeRun

    var newTime: Int = 0
    var playClicked = false

    //Variables that gets saved
    var running = state.get<Boolean>("running")
    var stopped = state.get<Boolean>("stopped")
    var taskIndex = state.get<Int>("taskIndex")
    var taskTitle = state.get<String>("taskTitle")
    var chronometerBase = state.get<Long>("chronometerBase")

    /*
        Setters for updating values and saving to state
     */
    fun setRunning(boolean: Boolean) {
        running = boolean
        state.set("running", boolean)
    }

    fun setStopped(boolean: Boolean) {
        stopped = boolean
        state.set("stopped", boolean)
    }

    fun setTaskIndex(int: Int) {
        taskIndex = int
        state.set("taskIndex", int)
    }

    @JvmName("setTaskTitle1")
    fun setTaskTitle(string: String) {
        taskTitle = string
        state.set("taskTitle", string)
    }

    fun setChronometerBase(long: Long) {
        chronometerBase = long
        state.set("chronometerBase", long)
    }
}