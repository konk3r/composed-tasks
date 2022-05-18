package com.casadetasha.pathtoplunder.singlelistapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import java.util.UUID.randomUUID

@Entity
class Task(
    var name: String,
) {
    @PrimaryKey
    var uuid: UUID = randomUUID()
    var isCompleted: Boolean = false

    fun toggleCompleted() { isCompleted = !isCompleted }
}
