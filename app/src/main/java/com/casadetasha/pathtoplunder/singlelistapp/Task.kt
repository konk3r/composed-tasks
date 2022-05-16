package com.casadetasha.pathtoplunder.singlelistapp

sealed class Task(
    var name: String,
) {
    var isCompleted: Boolean = false

    fun toggleCompleted() { isCompleted = !isCompleted }
}

class BlankTask: Task(name = "")

class CreatedTask(name: String): Task(name = name)
