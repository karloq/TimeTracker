package com.karloqvist.timetracker.task

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Karl Öqvist
 *
 * TouchHelper for managing touches and clicks in recyclerView of ActivitiesFragment
 */

open class TaskItemTouchHelper(
    private var adapter: ItemTouchHelperAdapter
) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    //A task in the list is being dragged
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.OnItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    //Task is swiped to be deleted
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.OnItemSwipe(viewHolder.adapterPosition)
    }
}