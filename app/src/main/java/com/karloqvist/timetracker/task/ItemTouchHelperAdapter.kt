package com.karloqvist.timetracker.task

/**
 * @author Karl Öqvist
 *
 * Interface for TaskItemTouchHelper
 */

interface ItemTouchHelperAdapter {

    fun OnItemMove(fromPosition: Int, toPosition: Int)
    fun OnItemSwipe(position: Int)
    fun getTaskAt(position: Int): Task
}