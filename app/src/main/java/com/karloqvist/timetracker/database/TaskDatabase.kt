package com.karloqvist.timetracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.karloqvist.timetracker.task.Task

/**
 * @author Karl Öqvist
 *
 * Database cointaining the Task entities
 */

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract val taskDatabaseDao: TaskDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = buildDatabase(context)
                }
                return instance
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TaskDatabase::class.java,
                "task_database"
            )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                })
                .build()
    }
}