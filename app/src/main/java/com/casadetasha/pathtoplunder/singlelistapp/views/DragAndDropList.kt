package com.casadetasha.pathtoplunder.singlelistapp.views

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casadetasha.pathtoplunder.singlelistapp.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun DragAndDropList(
    items: List<Task>,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    addTask: (Task) -> Unit
) {

    val scope = rememberCoroutineScope()

    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    val dragDropListState = rememberDragDropListState(onMove = onMove)

    LazyColumn(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consumeAllChanges()
                        dragDropListState.onDrag(offset)

                        if (overscrollJob?.isActive == true)
                            return@detectDragGesturesAfterLongPress

                        dragDropListState.checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let { overscrollJob = scope.launch { dragDropListState.lazyListState.scrollBy(it) } }
                            ?: run { overscrollJob?.cancel() }
                    },
                    onDragStart = { offset -> dragDropListState.onDragStart(offset) },
                    onDragEnd = { dragDropListState.onDragInterrupted() },
                    onDragCancel = { dragDropListState.onDragInterrupted() }
                )
            },
        state = dragDropListState.lazyListState
    ) {
        itemsIndexed(items) { index, item ->
            Column(
                modifier = Modifier
                    .composed {
                        val offsetOrNull =
                            dragDropListState.elementDisplacement.takeIf {
                                index == dragDropListState.currentIndexOfDraggedItem
                            }

                        Modifier
                            .graphicsLayer {
                                translationY = offsetOrNull ?: 0f
                            }
                    }
                    .fillMaxWidth()
            ) {
                TaskRow(AddTaskState(item))
            }
        }

        item {
            var isOpen by remember { mutableStateOf(false) }

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
                            Text("Add task", fontWeight = FontWeight.Bold)
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    true -> AddTaskRow(AddTaskState(BlankTask(), isOpen = true)) { task ->
                        isOpen = false
                        if (task is CreatedTask) {
                            addTask(task)
                        }
                    }
                }
            }
        }
    }
}