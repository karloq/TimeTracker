package com.karloqvist.timetracker.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karloqvist.timetracker.R
import com.karloqvist.timetracker.database.TaskDatabase
import com.karloqvist.timetracker.databinding.FragmentStatisticsBinding
import com.karloqvist.timetracker.task.TaskStatisticsAdapter
import com.karloqvist.timetracker.viewmodel.TaskViewModel
import com.karloqvist.timetracker.viewmodel.TaskViewModelFactory

/**
 * @author Karl Öqvist
 *
 * Main fragment for displaying all current task and their historic total time.
 * Contains a RecyclerView for holding and displaying the task.
 */

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskViewModel: TaskViewModel

    private lateinit var adapter: TaskStatisticsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Get TaskViewModel shared with Main activity
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = TaskViewModelFactory(dataSource, application)
        taskViewModel = ViewModelProvider(
            requireActivity(), viewModelFactory
        ).get(TaskViewModel::class.java)

        //Initiate RecyclerView and add custom adapter
        val recyclerView: RecyclerView = view.findViewById(R.id.sfrag_rv)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        adapter = TaskStatisticsAdapter()

        taskViewModel.getAllTasks().observe(viewLifecycleOwner, {
            adapter.setTasks(it)
        })

        recyclerView.adapter = adapter
    }

    //Add custom menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear(); // clears all menu items..
        requireActivity().menuInflater.inflate(R.menu.actionbar_menu_statistics, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}