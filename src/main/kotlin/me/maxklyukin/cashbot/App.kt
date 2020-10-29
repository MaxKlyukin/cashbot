package me.maxklyukin.cashbot

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import me.maxklyukin.cashbot.message.Message
import me.maxklyukin.cashbot.message.Response
import me.maxklyukin.cashbot.task.TaskRunner
import me.maxklyukin.cashbot.translation.Translator

class App {

    fun run() {
        runBlocking {
            val messages = Channel<Message>()
            val responses = Channel<Response>()

            val handler = Wiring.handlerFactory.make(messages, responses)
            val client = Wiring.clientFactory.getClient()
            val taskRunner = Wiring.taskRunnerFactory.make(handler)

            initTranslator()
            initFunctions()
            initCommands()
            initTasks(taskRunner)

            val handlerJob = handler.handleMessages()
            val clientJob = client.communicate(messages, responses)
            val taskJob = taskRunner.start()

            handlerJob.join()
            clientJob.join()
            taskJob.join()
        }
    }

    private fun initTranslator() {
        Translator.setDefault(Translator(Wiring.translationLoader, Wiring.config.lang))
    }

    private fun initFunctions() {
        for (function in Wiring.functions) {
            Wiring.functionRepo.add(function)
        }
    }

    private fun initCommands() {
        addSystemCommands()
        addUserCommands()
    }

    private fun addSystemCommands() {
        for (info in Wiring.systemCommands) {
            Wiring.commandUpdater.addSystemCommand(info)
        }
    }

    private fun addUserCommands() {
        for (info in Wiring.commandLoader.load()) {
            Wiring.commandUpdater.addUserCommand(info)
        }
    }

    private fun initTasks(taskRunner: TaskRunner) {
        for (task in Wiring.taskRepository.getTasks()) {
            taskRunner.add(task)
        }
    }
}

fun main() {
    App().run()
}
