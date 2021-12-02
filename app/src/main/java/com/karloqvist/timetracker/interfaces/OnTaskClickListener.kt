package com.karloqvist.timetracker.interfaces


/**
 * @author Karl Öqvist
 *
 * Interface used for communication between MainActivity and ActivitiesFragment
 */

interface OnTaskClickListener {
    fun onPlayClick(position: Int)
    fun onAddTimeClick(position: Int)
    fun onResetTimeClick(position: Int)
}