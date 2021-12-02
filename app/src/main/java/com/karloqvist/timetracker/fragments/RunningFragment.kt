package com.karloqvist.timetracker.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.karloqvist.timetracker.databinding.FragmentRunningBinding
import com.karloqvist.timetracker.task.Task
import com.karloqvist.timetracker.viewmodel.RunningTaskViewModel

/**
 * @author Karl Öqvist
 *
 * Fragment that is just underneath the action bar that holds a timer for the running task.
 * Saves state through RunningTaskViewModel in configuration change and process death.
 */

class RunningFragment : Fragment() {
    private var _binding: FragmentRunningBinding? = null
    private val binding get() = _binding!!

    private lateinit var chronometer: Chronometer

    private val runningTaskViewModel: RunningTaskViewModel by activityViewModels()

    private var runningTaskTime = 0

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferencesEditor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        sharedPreferences =
            requireActivity().getSharedPreferences("TimeTracker", Context.MODE_PRIVATE)
        preferencesEditor = sharedPreferences.edit()

        _binding = FragmentRunningBinding.inflate(inflater, container, false)

        loadData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        chronometer = binding.runningCTime

        //If running is true then resume the chronometer
        if (runningTaskViewModel.running == true) {
            resumeChronometer()
        }

        //Observe if there is a task to be started
        runningTaskViewModel.taskToBeRunTask.observe(viewLifecycleOwner, {
            if (runningTaskViewModel.running != true && runningTaskViewModel.playClicked) {
                saveTaskInfo(it)
                startChronometer()
                runningTaskViewModel.playClicked = false
            }
        })

        binding.runningBStop.setOnClickListener {
            stopChronometer()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun saveTaskInfo(it: Task?) {
        it?.let { it1 -> runningTaskViewModel.setTaskTitle(it1.title) }
    }

    /*
    Stop timer and update the time addition to ViewModel.
    Changes the visibility of the timer and button to invisible
     */
    private fun stopChronometer() {
        chronometer.stop()

        binding.runningCTime.visibility = View.GONE
        binding.runningBStop.visibility = View.GONE
        binding.runningTvTitle.text = "No task running"

        runningTaskTime = ((SystemClock.elapsedRealtime() - chronometer.base) / 1000).toInt()

        runningTaskViewModel.setStopped(true)
        runningTaskViewModel.setRunning(false)
        runningTaskViewModel.newTime = runningTaskTime
        runningTaskViewModel.taskStoppedTask.value = true
    }

    /*
    Start timer and save timer base, what task is running and running boolean to ViewModel.
    Changes the visibility of the timer and button to visible
     */
    private fun startChronometer() {
        val base = SystemClock.elapsedRealtime()
        binding.runningTvTitle.text = runningTaskViewModel.taskTitle
        binding.runningBStop.visibility = View.VISIBLE
        binding.runningCTime.visibility = View.VISIBLE

        //Save values to viewModel to enable resuming the timer
        runningTaskViewModel.setRunning(true)
        runningTaskViewModel.setChronometerBase(base)

        //Reset tasktime, update running task and start timer
        runningTaskViewModel.taskStoppedTask.value = false
        runningTaskTime = 0
        chronometer.base = base
        chronometer.start()
    }

    /*
    Retrieves timer base from viewModel to resume at correct time
    Sets visibilty of counter and button to be visible and starts the timer
     */
    private fun resumeChronometer() {
        binding.runningTvTitle.text = runningTaskViewModel.taskTitle
        binding.runningBStop.visibility = View.VISIBLE
        binding.runningCTime.visibility = View.VISIBLE
        chronometer.base = runningTaskViewModel.chronometerBase!!
        chronometer.start()
    }

    /*
    Save data to shared preferences so that timer resumes even if the application fully closes.
    Only saves data if there is a timer running otherwise it only saves running boolean
     */
    private fun saveData() {
        val running = runningTaskViewModel.running
        if (running == true) {
            preferencesEditor.apply {
                runningTaskViewModel.chronometerBase?.let { putLong("chronometerBase", it) }
                runningTaskViewModel.running?.let { putBoolean("running", it) }
                runningTaskViewModel.stopped?.let { putBoolean("stopped", it) }
                runningTaskViewModel.taskIndex?.let { putInt("savedTaskIndex", it) }
                runningTaskViewModel.taskTitle?.let { putString("savedTaskTitle", it) }
                apply()
            }
        } else {
            preferencesEditor.apply {
                runningTaskViewModel.running?.let { putBoolean("running", it) }
                apply()
            }
        }
    }

    /*
    Load data from shared preferences to resume timer.
    Only loads data if there was a timer running when the application closed down.
     */
    private fun loadData() {
        val running = sharedPreferences.getBoolean("running", false)
        if (running) {
            runningTaskViewModel.chronometerBase = sharedPreferences.getLong("chronometerBase", 0)
            runningTaskViewModel.running = running
            runningTaskViewModel.stopped = sharedPreferences.getBoolean("stopped", false)
            runningTaskViewModel.taskIndex = sharedPreferences.getInt("savedTaskIndex", 0)
            runningTaskViewModel.taskTitle = sharedPreferences.getString("savedTaskTitle", "Fault")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Call saveData to save timer values
    override fun onStop() {
        super.onStop()
        saveData()
    }
}