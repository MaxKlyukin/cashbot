package me.maxklyukin.cashbot.yaml

import kotlinx.serialization.Serializable
import me.maxklyukin.cashbot.task.TaskInfo
import me.maxklyukin.cashbot.task.TaskRepository

class YamlTaskRepository(fileName: String) : TaskRepository {
    private val yamlFile = YamlFile(fileName)

    override fun getTasks(): List<TaskInfo> {
        val tasksInfo = yamlFile.read(YamlTasksInfo.serializer())

        return tasksInfo.tasks.map { taskInfo ->
            TaskInfo(
                    taskInfo.id,
                    taskInfo.command,
                    taskInfo.initialDelay,
                    taskInfo.period
            )
        }
    }
}

@Serializable
data class YamlTasksInfo(
        val tasks: List<YamlTaskInfo>
)

@Serializable
data class YamlTaskInfo(
        val id: String,
        val command: String,
        val initialDelay: Long,
        val period: Long
)