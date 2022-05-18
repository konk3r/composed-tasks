package com.casadetasha.pathtoplunder.singlelistapp

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.casadetasha.pathtoplunder.singlelistapp.app.repos.TaskRoomDatabase
import com.casadetasha.pathtoplunder.singlelistapp.views.move
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private lateinit var database: TaskRoomDatabase
    private lateinit var tasks: Flow<List<Task>>

    private var mutableTasks: List<Task> by mutableStateOf(emptyList())

    fun getTasks(): List<Task> {
        return mutableTasks
    }

    fun loadTasks(database: TaskRoomDatabase) {
        this.database = database
        tasks = database.taskDao().getAll()
        GlobalScope.launch {
            tasks.collectLatest { mutableTasks = it }
        }
    }

    fun addTask(task: Task) {
        GlobalScope.launch {
            database.taskDao().insert(task)
        }
    }

    fun removeTask(task: Task) {
        mutableTasks = mutableTasks.toMutableList().apply { remove(task) }
    }

    fun moveTask(fromIndex: Int, toIndex: Int) {
        mutableTasks = mutableTasks.toMutableList().apply { move(fromIndex, toIndex) }
    }
}

