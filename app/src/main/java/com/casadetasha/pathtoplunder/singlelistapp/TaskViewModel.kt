package com.casadetasha.pathtoplunder.singlelistapp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    private var mutableTasks: List<Task> by mutableStateOf(emptyList())

    init {
        loadTasks()
    }

    fun getTasks(): List<Task> {
        return mutableTasks
    }

    private fun loadTasks() {
        (1..5).onEach {
            addTask(CreatedTask("Task ${mutableTasks.size + 1}"))
        }
    }

    fun addTask(task: Task) {
        mutableTasks = mutableTasks + task
    }

    fun removeTask(task: Task) {
        mutableTasks = mutableTasks.toMutableList().apply { remove(task) }
    }
}

