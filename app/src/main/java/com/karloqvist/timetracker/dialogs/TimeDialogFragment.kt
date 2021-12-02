package com.karloqvist.timetracker.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.karloqvist.timetracker.R
import com.karloqvist.timetracker.interfaces.OnTaskInputAccepted

/**
 * @author Karl Öqvist
 *
 * Dialog that opens from activitiesFragment if user wants to add time to task.
 * Uses OnTaskInputAccepted interface for communicating with Main activity
 */

class TimeDialogFragment() : DialogFragment() {

    private lateinit var mOnTaskInputAccepted: OnTaskInputAccepted

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_dialog_time, container, false)

        rootView.findViewById<Button>(R.id.time_b_accept).setOnClickListener {
            acceptTime()
        }

        rootView.findViewById<Button>(R.id.time_b_cancel).setOnClickListener {
            dismiss()
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOnTaskInputAccepted = targetFragment as OnTaskInputAccepted
    }

    //If user accepts time addition, converts hours and minutes into minutes to send back via interface
    private fun acceptTime() {
        val hoursField: TextInputEditText = requireView().findViewById(R.id.time_et_hours)
        val hours = hoursField.text.toString().toInt()
        val minutesField: TextInputEditText = requireView().findViewById(R.id.time_et_minutes)
        var minutes = minutesField.text.toString().toInt()
        minutes = (minutes * 60) + (hours * 3600)
        Log.d("time", minutes.toString())
        mOnTaskInputAccepted.sendTimeAddition(minutes)
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnTaskInputAccepted = targetFragment as OnTaskInputAccepted
        } catch (e: ClassCastException) {
            Log.e("AddDialogFragment", "onAttach: ClassCastException : " + e.message)
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }
}