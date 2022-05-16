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
import com.casadetasha.pathtoplunder.singlelistapp.ui.theme.SingleListAppTheme
import com.casadetasha.pathtoplunder.singlelistapp.views.DragAndDropList

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    TaskList(viewModel.getTasks(), moveTask) { newTask ->
                        viewModel.addTask(newTask)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskList(tasks: List<Task>, moveTask: (Int, Int) -> Unit, addTask: (Task) -> Unit) {
    DragAndDropList(items = tasks, moveTask) {
        addTask(it)
    }

//    LazyColumn {
//        items(tasks) { task ->
//            TaskRow(AddTaskState(task))
//        }
//
//        item {
//            Crossfade(
//                targetState = isOpen,
//                modifier = Modifier.animateContentSize()
//            ) { wasOpened ->
//                when (wasOpened) {
//                    false -> TextButton(
//                        onClick = { isOpen = true },
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .fillMaxWidth(80f)
//                            .animateContentSize()
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Text("Add task", fontWeight = Bold)
//                            Text("+", fontSize = 18.sp, fontWeight = Bold)
//                        }
//                    }
//
//                    true -> AddTaskRow(AddTaskState(BlankTask(), isOpen = true)) { task ->
//                        isOpen = false
//                        if (task is CreatedTask) { addTask(task) }
//                    }
//                }
//            }
//        }
//    }
}

@Composable
fun TaskRow(taskState: AddTaskState) {
    val task = taskState.task
    var name by remember { mutableStateOf(task.name) }
    var isOpen by remember { mutableStateOf(taskState.isOpen) }

    Button(
        onClick = {
            isOpen = !isOpen
            if (!isOpen) { task.name = name }
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
            Text(text = task.name)
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
                    true -> CreatedTask(name)
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
fun TaskInput(name: String, onNameChanged : (String) -> Unit ) {
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
        TaskList(createDummyTasks(), { _,_ -> }) { }
    }
}

fun createDummyTasks(): List<Task> {
    return (1..5).map {
        CreatedTask("Task $it")
    }
}
