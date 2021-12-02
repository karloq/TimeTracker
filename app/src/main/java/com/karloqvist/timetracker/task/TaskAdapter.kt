package com.karloqvist.timetracker.task

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.karloqvist.timetracker.R
import com.karloqvist.timetracker.dialogs.ConfirmationDialog
import com.karloqvist.timetracker.interfaces.OnTaskClickListener
import com.karloqvist.timetracker.utility.TimeUtil
import com.karloqvist.timetracker.viewmodel.TaskViewModel

/**
 * @author Karl Öqvist
 *
 * Custom adapter for displaying tasks in recyclerview of ActivitiesFragment
 */

class TaskAdapter(
    private val listener: OnTaskClickListener,
    private val taskViewModel: TaskViewModel,
    private val context: Context
) : ItemTouchHelperAdapter, RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var tasks: ArrayList<Task> = ArrayList()

    private lateinit var touchHelper: ItemTouchHelper

    private val confirmationDialog = ConfirmationDialog()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.timetask_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskHolder -> {
                holder.bind(tasks[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun setTasks(taskList: List<Task>) {
        tasks = taskList as ArrayList<Task>
        notifyDataSetChanged()
    }

    override fun getTaskAt(position: Int): Task {
        return tasks[position]
    }

    override fun OnItemMove(fromPosition: Int, toPosition: Int) {
        val fromTask = tasks[fromPosition]
        tasks.remove(fromTask)
        tasks.add(toPosition, fromTask)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun OnItemSwipe(position: Int) {

        val confirmationCallback = object : ConfirmationDialog.ConfirmationCallback {
            override fun proceed() {
                taskViewModel.deleteTask(tasks[position])
                notifyItemRemoved(position)
                Toast.makeText(
                    context,
                    "Task deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun cancel() {
                notifyItemRangeChanged(0, tasks.size)
            }
        }
        context.let {
            confirmationDialog.CreateDialog(
                context,
                "Pressing 'Yes' will delete the task, an action that is irrevocable",
                confirmationCallback
            )
        }
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {
        this.touchHelper = touchHelper
    }

    /*
     * Custom ViewHolder class for adapter
     */
    inner class TaskHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {
        val tvTitle: TextView = itemView.findViewById(R.id.card_tv_title)
        val tvDescription: TextView = itemView.findViewById(R.id.card_tv_desciption)
        val tvTime: TextView = itemView.findViewById(R.id.card_tv_time)
        val ibAddtime: ImageButton = itemView.findViewById(R.id.card_ib_addtime)
        val ibPlay: ImageButton = itemView.findViewById(R.id.card_ib_play)
        val ibReset: ImageButton = itemView.findViewById(R.id.card_ib_resettime)
        val timeUtil = TimeUtil()

        init {
            ibAddtime.setOnClickListener {
                val position = adapterPosition
                listener.onAddTimeClick(position)
            }

            ibPlay.setOnClickListener {
                val position = adapterPosition
                listener.onPlayClick(position)
            }

            ibReset.setOnClickListener {
                val position = adapterPosition
                listener.onResetTimeClick(position)
            }

            itemView.setOnTouchListener(this)
        }

        fun bind(task: Task) {
            tvTitle.text = task.title
            tvDescription.text = task.description
            tvTime.text = timeUtil.formatTimeString(task.time)
        }

        //Multitouch function for reordering tasks in recyclerView
        override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
            val action = p1!!.action
            when (action and MotionEvent.ACTION_MASK) {
                // Additional fingers placed causes a drag event to start
                MotionEvent.ACTION_POINTER_DOWN -> {
                    touchHelper.startDrag(this)
                }
                else -> {
                }
            }
            return true
        }
    }
}