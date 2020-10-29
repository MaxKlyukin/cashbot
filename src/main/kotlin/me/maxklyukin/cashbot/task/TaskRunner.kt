package me.maxklyukin.cashbot.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.maxklyukin.cashbot.command.CommandParser
import me.maxklyukin.cashbot.message.MessageHandler
import me.maxklyukin.cashbot.timer.Timer
import kotlin.coroutines.CoroutineContext

class TaskRunner(
        private val messageHandler: MessageHandler
) : CoroutineScope {
    private var timers: MutableMap<String, Timer> = HashMap()
    private var running = false

    fun add(task: TaskInfo) {
        if (running) {
            throw TaskException("Cant add task because runner is already on")
        }

        val command = CommandParser(task.commandString).parse()

        timers[task.id] = Timer(task.id, task.initialDelayMs, task.periodMs) {
            messageHandler.handleCommand(command)
        }
    }

    fun start() = launch {
        running = true

        val jobs = timers.values.map { timer -> timer.start() }

        for (job in jobs) {
            job.join()
        }
    }

    fun stop() {
        for ((_, timer) in timers) {
            timer.stop()
        }

        running = false
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}