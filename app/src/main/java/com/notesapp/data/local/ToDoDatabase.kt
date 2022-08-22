package com.notesapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notesapp.data.model.ToDoTask

@Database(entities = [ToDoTask::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun toDoDao(): ToDoDao
}