package com.casadetasha.pathtoplunder.singlelistapp.app.repos

import androidx.room.*
import com.casadetasha.pathtoplunder.singlelistapp.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert()
    fun insert(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tasks: List<Task>)

    @Query("SELECT * FROM task")
    fun getAll(): Flow<List<Task>>

    @Query("SELECT * FROM task")
    fun getAllNow(): List<Task>

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)
}