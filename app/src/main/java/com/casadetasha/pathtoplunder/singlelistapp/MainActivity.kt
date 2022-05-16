package com.casadetasha.pathtoplunder.singlelistapp

import android.graphics.fonts.FontStyle
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casadetasha.pathtoplunder.singlelistapp.ui.theme.SingleListAppTheme

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
                    TaskList()
                }
            }
        }
    }

    @Composable
    private fun TaskList() {
        var isOpen by remember { mutableStateOf(false) }

        LazyColumn {
            items(viewModel.getTasks()) { task ->
                TaskRow(AddTaskState(task)) { }
            }

            item {
                Crossfade(
                    targetState = isOpen,
                    modifier = Modifier.animateContentSize()
                ) { wasOpened ->
                    when (wasOpened) {
                        false -> TextButton(
                                onClick = { isOpen = true },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(80f)
                                    .animateContentSize()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Add task", fontWeight = Bold)
                                    Text("+", fontSize = 18.sp, fontWeight = Bold)
                                }
                            }

                        true -> TaskRow(AddTaskState(BlankTask(), isOpen = true)) { task ->
                            if (task !is BlankTask) viewModel.addTask(task)
                            isOpen = false
                        }
                    }
                }
            }
        }
//        DragAndDropList(
//            items = tasks,
//            { from, to -> }
//        )
    }

    @Composable
    private fun TaskRow(taskState: AddTaskState, onClose: (Task) -> Unit) {
        val task = taskState.task
        var isOpen by remember { mutableStateOf(taskState.isOpen) }
        Button(
            onClick = {
                isOpen = !isOpen
                if (!isOpen) onClose(task)
                Log.d("Task", "Toggling open")
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
                    TaskInput(task)
                }
            }
        }
    }
}

class AddTaskState(val task: Task, var isOpen: Boolean = false)

@Composable
private fun TaskInput(task: Task) {
    var name by remember { mutableStateOf(task.name) }

    TextField(
        value = name,
        onValueChange = { task.name = it; name = it },
        label = { Text("Name") }
    )
    Text(text = "Task complete!")
    Text(text = "Good job!")
    Text(text = "Have cookie!")
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SingleListAppTheme {
        Greeting("Android")
    }
}