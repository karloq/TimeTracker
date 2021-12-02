package com.karloqvist.timetracker.interfaces

import com.karloqvist.timetracker.task.Task

/**
 * @author Karl Öqvist
 *
 * Interface used for communication between MainActivity and dialogs for adding tasks,
 * adding time to tasks or deducting time from tasks
 */

interface OnTaskInputAccepted {
    fun sendInput(task: Task)
    fun sendTimeAddition(time: Int)
    fun sendTimeDeduction(time: Int)
}