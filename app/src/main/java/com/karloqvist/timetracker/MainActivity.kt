package com.karloqvist.timetracker

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.karloqvist.timetracker.database.TaskDatabase
import com.karloqvist.timetracker.databinding.ActivityMainBinding
import com.karloqvist.timetracker.dialogs.AddDialogFragment
import com.karloqvist.timetracker.dialogs.ConfirmationDialog
import com.karloqvist.timetracker.fragments.ActivitiesFragment
import com.karloqvist.timetracker.fragments.RunningFragment
import com.karloqvist.timetracker.fragments.StatisticsFragment
import com.karloqvist.timetracker.viewmodel.TaskViewModel
import com.karloqvist.timetracker.viewmodel.TaskViewModelFactory

/**
 * @author Karl Öqvist
 *
 * Main activity that handles StatisticsFragment and ActivitiesFragment, app navigation
 * and custom actionbar.
 */

class MainActivity : AppCompatActivity(R.layout.activity_main),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var activitiesFragment: ActivitiesFragment
    private lateinit var statisticsFragment: StatisticsFragment
    private lateinit var runningFragment: RunningFragment
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var confirmationDialog: ConfirmationDialog
    private var openFragmentInt : Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set custom ActionBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Set navigation drawer
        drawer = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        ///Instantiate fragments
        activitiesFragment = ActivitiesFragment()
        statisticsFragment = StatisticsFragment()
        runningFragment = RunningFragment()

        if (savedInstanceState != null) {
            openFragmentInt = savedInstanceState.getInt("lastOpenFragment", 1)
        }

        //Start fragments
        if (openFragmentInt == 2){
            navigationView.setCheckedItem(R.id.nav_statistics)
            openFragmentInt = 2
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, statisticsFragment, "statistics_fragment")
                    .commit()
            }
        } else {
            navigationView.setCheckedItem(R.id.nav_activities)
            openFragmentInt = 1
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, activitiesFragment, "activities_fragment")
                    .commit()
            }
        }

        //Start running fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.running_fragment_container, runningFragment, "running_fragment")
                .commit()
        }

        //Initiate TaskViewModel
        val application = requireNotNull(this).application
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = TaskViewModelFactory(dataSource, application)
        taskViewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(TaskViewModel::class.java)

        confirmationDialog = ConfirmationDialog()
    }

    //Close navigation drawer if back is pressed
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //Open corresponding fragment when user presses option in navigation drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_activities -> {
                openFragmentInt = 1
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, activitiesFragment, "activities_fragment")
                        .commit()
                }
            }

            R.id.nav_statistics ->{
                openFragmentInt = 2
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, statisticsFragment, "statistics_fragment")
                        .commit()
                }
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.abmenu_add -> {
                onAddButtonPressed()
                return true
            }
            R.id.abmenu_deleteall -> {
                onDeleteButtonPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun onDeleteButtonPressed() {
        val confirmationCallback = object : ConfirmationDialog.ConfirmationCallback {
            override fun proceed() {
                taskViewModel.deleteAllTasks()
                Toast.makeText(applicationContext, "All activities deleted", Toast.LENGTH_SHORT).show()
            }

            override fun cancel() {
                Toast.makeText(
                    applicationContext,
                    "If you only want to delete one activity, swipe it to the right!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        confirmationDialog.CreateDialog(
            this,
            "Pressing 'Yes' will delete all activities, an action that is irrevocable",
            confirmationCallback
        )
    }

    private fun onAddButtonPressed() {
        val dialog = AddDialogFragment()
        dialog.setTargetFragment(supportFragmentManager.findFragmentByTag("activities_fragment"), 1)
        dialog.show(supportFragmentManager, "customDialog")
    }

    //Save currently open fragment
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("lastOpenFragment", openFragmentInt)
    }
}