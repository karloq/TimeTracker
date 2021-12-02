package com.karloqvist.timetracker.dialogs

import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
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
import com.karloqvist.timetracker.task.Task

/**
 * @author Karl Öqvist
 *
 * Dialog that opens from Main activity if user wants to add new Task to database
 * Uses OnTaskInputAccepted interface for communicating with Main activity
 */

class AddDialogFragment : DialogFragment() {

    private lateinit var mOnTaskInputAccepted: OnTaskInputAccepted

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_dialog_add, container, false)

        rootView.findViewById<Button>(R.id.time_b_accept).setOnClickListener {
            acceptTask()
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

    override fun onStart() {
        super.onStart()
        var dialog = dialog
        if(dialog != null){
            var width = ViewGroup.LayoutParams.MATCH_PARENT
            var height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    private fun acceptTask() {
        val title: TextInputEditText = requireView().findViewById(R.id.time_et_hours)
        val titleText = title.text.toString()
        val description: TextInputEditText = requireView().findViewById(R.id.time_et_minutes)
        val descriptionText = description.text.toString()
        mOnTaskInputAccepted.sendInput(Task(titleText, descriptionText))
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