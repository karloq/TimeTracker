package com.karloqvist.timetracker.utility

/**
 * @author Karl Öqvist
 *
 * Utilities class for converting seconds into minutes and hours och seconds to a timestring
 * to be used when displaying tasks.
 */

class TimeUtil {
    fun formatTimePair(timeInSeconds: Int): Pair<Int, Int> {
        var minutes = timeInSeconds / 60
        val hours = minutes / 60
        minutes -= hours * 60
        return Pair(hours, minutes)
    }

    fun formatTimeString(timeInSeconds: Int): String {
        var minutes = timeInSeconds / 60
        val hours = minutes / 60
        minutes -= hours * 60
        val timestring: String
        if ((hours == 0) and (minutes == 0)) {
            timestring = "<1 min"
        } else {
            timestring = hours.toString() + " hrs " + minutes.toString() + " min"
        }
        return timestring
    }
}