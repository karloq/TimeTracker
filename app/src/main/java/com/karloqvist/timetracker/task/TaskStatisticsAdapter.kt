package com.karloqvist.timetracker.task

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karloqvist.timetracker.R
import com.karloqvist.timetracker.utility.TimeUtil


/**
 * @author Karl Öqvist
 *
 * Custom adapter for displaying tasks in recyclerview of StatisticsFragment
 */

class TaskStatisticsAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var tasks: ArrayList<Task> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskStatisticsHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.statisticstask_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskStatisticsHolder -> {
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

    inner class TaskStatisticsHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {
        val tvTitle: TextView = itemView.findViewById(R.id.statcard_tv_title)
        val tvDescription: TextView = itemView.findViewById(R.id.statcard_tv_description)
        val tvTime: TextView = itemView.findViewById(R.id.statcard_tv_time)
        val timeUtil = TimeUtil()

        fun bind(task: Task) {
            tvTitle.text = task.title
            tvDescription.text = task.description
            tvTime.text = timeUtil.formatTimeString(task.historicTime)
        }

        override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
            return true
        }
    }
}