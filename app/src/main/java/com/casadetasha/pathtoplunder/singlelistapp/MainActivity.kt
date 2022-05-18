package com.casadetasha.pathtoplunder.singlelistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.casadetasha.pathtoplunder.singlelistapp.app.repos.TaskRoomDatabase
import com.casadetasha.pathtoplunder.singlelistapp.ui.theme.SingleListAppTheme
import com.casadetasha.pathtoplunder.singlelistapp.views.DragAndDropList
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadTasks(TaskRoomDatabase.getDatabase(application))
        setContent {
            SingleListAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val moveTask: (Int, Int) -> Unit =  { fromIndex, toIndex ->
                        viewModel.moveTask(fromIndex, toIndex)
                    }
                    TaskList(lifecycleScope, viewModel.getTasks(), moveTask) { newTask ->
                        viewModel.addTask(newTask)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskList(
    scope: CoroutineScope,
    tasks: List<Task>,
    moveTask: (Int, Int) -> Unit,
    addTask: (Task) -> Unit,
) {
    DragAndDropList(scope, items = tasks, moveTask) {
        addTask(it)
    }
}

@Composable
fun TaskRow(scope: CoroutineScope, taskState: AddTaskState) {
    val task = taskState.task
    var name by remember { mutableStateOf(task.name) }
    var isCompleted by remember { mutableStateOf(task.isCompleted) }
    var isOpen by remember { mutableStateOf(taskState.isOpen) }
    var clickJob: Job? = null

    Button(
        onClick = {
            when (val job = clickJob) {
                null -> {
                    clickJob = scope.launch {
                        delay(timeMillis = 300)
                        if (!isActive) return@launch

                        clickJob = null
                        isOpen = !isOpen
                        if (!isOpen) { task.name = name }
                    }
                }

                else -> {
                    job.cancel("Double tap recognized, ending click job")
                    task.isCompleted = !task.isCompleted
                    isCompleted = task.isCompleted
                    clickJob = null
                }
            }
        },
        elevation = ButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        ),
        modifier = Modifier
            .background(Color.Transparent)
            .padding(8.dp)
            .fillMaxWidth(90f)
            .animateContentSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${task.name} ${if (isCompleted) "completed" else ""}")
            if (isOpen) {
                TaskInput(name) { name = it }
            }
        }
    }
}

@Composable
fun AddTaskRow(taskState: AddTaskState, onInputFinished: (Task) -> Unit) {
    val task = taskState.task
    var name by remember { mutableStateOf(task.name) }
    var isOpen by remember { mutableStateOf(taskState.isOpen) }

    Button(
        onClick = {
            isOpen = !isOpen
            if (!isOpen) {
                val outgoingTask = when(name.isNotBlank()) {
                    true -> Task(name)
                    false -> task
                }
                onInputFinished(outgoingTask);
            }
        },
        elevation = ButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(90f)
            .animateContentSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = task.name)
            if (isOpen) {
                TaskInput(name) { name = it }
            }
        }
    }
}

class AddTaskState(val task: Task, var isOpen: Boolean = false)

@Composable
fun TaskInput(name: String, onNameChanged : (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = { onNameChanged(it) },
        label = { Text("Name") }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SingleListAppTheme {
        TaskList(MainScope(), createDummyTasks(), { _, _ -> }, { })
    }
}

fun createDummyTasks(): List<Task> {
    return (1..5).map {
        Task("Task $it")
    }
}
