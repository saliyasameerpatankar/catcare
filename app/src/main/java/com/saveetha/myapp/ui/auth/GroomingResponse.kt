package com.saveetha.myapp.ui.auth

data class GroomingTask(
    val task_name: String,
    val frequency: String,
    val last_done: String,
    val next_due: String
)

data class GroomingTaskResponse(
    val status: String,
    val data: List<GroomingTask>
)
