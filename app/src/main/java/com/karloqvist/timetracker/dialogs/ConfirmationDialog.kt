package com.karloqvist.timetracker.dialogs

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.karloqvist.timetracker.R

/**
 * @author Karl Öqvist
 *
 * Dialog that opens from Main activity if user wants to delete one task or all tasks.
 * Uses ConfirmationCallback interface to communicate back the result to Main activity.
 */

class ConfirmationDialog {

    //Creates dialog with given message
    fun CreateDialog(context: Context, message: String, callback: ConfirmationCallback) {
        MaterialDialog(context)
            .show {
                title(R.string.are_you_sure)
                message(text = message)
                negativeButton(R.string.cancel) {
                    callback.cancel()
                }
                positiveButton(R.string.yes) {
                    callback.proceed()
                }
            }

    }

    interface ConfirmationCallback {
        fun proceed()
        fun cancel()
    }
}