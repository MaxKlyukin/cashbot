package me.maxklyukin.cashbot

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import me.maxklyukin.cashbot.message.Message
import me.maxklyukin.cashbot.message.Response
import me.maxklyukin.cashbot.task.TaskRunner
import me.maxklyukin.cashbot.translation.Translator
import me.maxklyukin.cashbot.yaml.YamlFile
import kotlinx.serialization.modules.*
import me.maxklyukin.cashbot.translation.DefaultTranslator

class App(private val wiring: Wiring) {

    fun run() = runBlocking {
        val messages = Channel<Message>()
        val responses = Channel<Response>()

        val handler = wiring.handlerFactory.make(messages, responses)
        val client = wiring.clientFactory.getClient()
        val taskRunner = wiring.taskRunnerFactory.make(handler)

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

    private fun initTranslator() {
        Translator.setDefault(DefaultTranslator(wiring.translationLoader, wiring.config.lang))
    }

    private fun initFunctions() {
        for (function in wiring.functions) {
            wiring.functionRepo.add(function)
        }
    }

    private fun initCommands() {
        addSystemCommands()
        addUserCommands()
    }

    private fun addSystemCommands() {
        for (info in wiring.systemCommands) {
            wiring.commandUpdater.addSystemCommand(info)
        }
    }

    private fun addUserCommands() {
        for (info in wiring.commandLoader.load()) {
            wiring.commandUpdater.addUserCommand(info)
        }
    }

    private fun initTasks(taskRunner: TaskRunner) {
        for (task in wiring.taskRepository.getTasks()) {
            taskRunner.add(task)
        }
    }
}

fun main() {
    val config = loadConfig("config.yaml")
    val wiring = Wiring(config)

    App(wiring).run()
}

private fun loadConfig(fileName: String): Config {
    YamlFile.replaceModule(makeConfigSerializersModule())

    return YamlFile(fileName).read(Config.serializer())
}

fun makeConfigSerializersModule(): SerializersModule {
    return SerializersModule {
        polymorphic(CustomConfig::class) {
//            subclass(YourCustomConfig::class)
        }
    }
}
