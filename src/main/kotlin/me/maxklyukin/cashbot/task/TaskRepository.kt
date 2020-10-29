package me.maxklyukin.cashbot.task

interface TaskRepository {
    fun getTasks(): List<TaskInfo>
}

data class TaskInfo(
        val id: String,
        val commandString: String,
        val initialDelayMs: Long,
        val periodMs: Long
)