package com.karloqvist.timetracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.karloqvist.timetracker.task.Task

/**
 * @author Karl Öqvist
 *
 * Database Dao interface for TaskDatabase
 */

@Dao
interface TaskDatabaseDao {

    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Delete
    fun deleteMultiple(taskList: ArrayList<Task>)

    @Query("DELETE FROM task_table")
    fun deleteAll()

    @Query("SELECT * FROM task_table WHERE id = :id ORDER BY id DESC")
    fun getTask(id: Int): Task

    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<Task>>
}