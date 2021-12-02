package com.karloqvist.timetracker.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karloqvist.timetracker.R
import com.karloqvist.timetracker.database.TaskDatabase
import com.karloqvist.timetracker.databinding.FragmentActivitiesBinding
import com.karloqvist.timetracker.dialogs.ConfirmationDialog
import com.karloqvist.timetracker.dialogs.ResetTimeDialogFragment
import com.karloqvist.timetracker.dialogs.TimeDialogFragment
import com.karloqvist.timetracker.interfaces.OnTaskClickListener
import com.karloqvist.timetracker.interfaces.OnTaskInputAccepted
import com.karloqvist.timetracker.task.Task
import com.karloqvist.timetracker.task.TaskAdapter
import com.karloqvist.timetracker.task.TaskItemTouchHelper
import com.karloqvist.timetracker.viewmodel.RunningTaskViewModel
import com.karloqvist.timetracker.viewmodel.TaskViewModel
import com.karloqvist.timetracker.viewmodel.TaskViewModelFactory

/**
 * @author Karl Öqvist
 *
 * Main fragment for displaying all current task and their time.
 * Contains a RecyclerView for holding and displaying the task.
 * Responsible for starting, adding and deducting time by opening TimeDialog or ResetTimeDialog.
 * Uses RunningTaskViewModel to start timer and to observe when the running task is stopped
 * Uses TaskViewModel to communicate to the database and inserting, deleting and reading its contents
 */

class ActivitiesFragment : Fragment(), OnTaskInputAccepted, OnTaskClickListener {
    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel

    private lateinit var adapter: TaskAdapter

    private lateinit var runningTask: Task
    private lateinit var clickedTask: Task

    private val runningTaskViewModel: RunningTaskViewModel by activityViewModels()

    private lateinit var confirmationDialog: ConfirmationDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set custom menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get taskViewModel that is shared with MainActivity
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = TaskViewModelFactory(dataSource, application)
        taskViewModel = ViewModelProvider(
            requireActivity(), viewModelFactory
        ).get(TaskViewModel::class.java)


        //Observe if the running task has been stopped
        runningTaskViewModel.taskStoppedTask.observe(viewLifecycleOwner, {
            onTaskStopped(it)
        })

        confirmationDialog = ConfirmationDialog()

        val recyclerView: RecyclerView = view.findViewById(R.id.afrag_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        adapter = TaskAdapter(this, taskViewModel, requireContext())

        taskViewModel.getAllTasks().observe(viewLifecycleOwner, {
            adapter.setTasks(it)
        })

        val callback: ItemTouchHelper.Callback = TaskItemTouchHelper(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        adapter.setTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = adapter
    }

    /* Called by observer if running task has been stopped. In that case it updates the time
    of the task. This function is called every time onViewCreated is called because the liveData
    of the newly initiated observer has new value (first value) therefore the if-case that makes
    it react to only the stop button being pressed*/
    private fun onTaskStopped(it: Boolean?) {
        if (runningTaskViewModel.stopped == true) {
            runningTask = runningTaskViewModel.taskIndex?.let { taskViewModel.getTask(it) }!!
            runningTask.time += runningTaskViewModel.newTime
            runningTask.historicTime += runningTaskViewModel.newTime
            //Update task in viewModel
            taskViewModel.updateTask(runningTask)
            runningTaskViewModel.stopped = false
        }
    }

    //Add custom options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.actionbar_menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //From OnTaskInputAccepted interface, if user adds new task via AddDialogFragment
    override fun sendInput(task: Task) {
        taskViewModel.insertTask(task)
    }

    //From OnTaskInputAccepted interface, if user adds new task via TimeDialogFragment
    override fun sendTimeAddition(time: Int) {
        clickedTask.time += time
        clickedTask.historicTime += time
        taskViewModel.updateTask(clickedTask)
    }

    //From OnTaskInputAccepted interface, if user adds new task via ResetTimeDialogFragment
    override fun sendTimeDeduction(time: Int) {
        clickedTask.time -= time
        taskViewModel.updateTask(clickedTask)
    }

    //From OnTaskClickListener interface, if user clicks play button on task in ActivitiesFragment
    override fun onPlayClick(position: Int) {
        runningTask = adapter.getTaskAt(position)
        runningTaskViewModel.playClicked = true
        runningTaskViewModel.setTaskIndex(position)
        runningTaskViewModel.taskToBeRunTask.value = runningTask
        Toast.makeText(activity, "Started activity", Toast.LENGTH_SHORT).show()
    }

    //From OnTaskClickListener interface, if user clicks add time button on task in ActivitiesFragment
    override fun onAddTimeClick(position: Int) {
        clickedTask = adapter.getTaskAt(position)
        val dialog = TimeDialogFragment()
        dialog.setTargetFragment(
            requireActivity().supportFragmentManager.findFragmentByTag("activities_fragment"),
            1
        )
        dialog.show(requireActivity().supportFragmentManager, "addTimeDialog")
    }

    //From OnTaskClickListener interface, if user clicks deduct time button on task in ActivitiesFragment
    override fun onResetTimeClick(position: Int) {
        clickedTask = adapter.getTaskAt(position)
        val dialog = ResetTimeDialogFragment(clickedTask.time)
        dialog.setTargetFragment(
            requireActivity().supportFragmentManager.findFragmentByTag("activities_fragment"),
            1
        )
        dialog.show(requireActivity().supportFragmentManager, "resetTimeDialog")
    }
}