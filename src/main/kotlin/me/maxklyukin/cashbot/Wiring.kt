package me.maxklyukin.cashbot

import me.maxklyukin.cashbot.calculator.Calculator
import me.maxklyukin.cashbot.client.ClientFactory
import me.maxklyukin.cashbot.command.*
import me.maxklyukin.cashbot.functions.*
import me.maxklyukin.cashbot.message.MessageHandlerFactory
import me.maxklyukin.cashbot.task.TaskRunnerFactory
import me.maxklyukin.cashbot.yaml.*

class Wiring(val config: Config) {

    val dataStoreFactory = YamlDataStoreFactory(config.data_dir)

    val clientFactory = ClientFactory(config.client)

    val commandDataFactory = CommandDataFactory

    val functionRepo = FunctionRepository()

    private val commandSyncer = YamlCommandSyncer(config.command_file)

    val commandSaver = commandSyncer
    val commandLoader = commandSyncer

    val userCommandRepo = InMemoryCommandRepository()

    val savingUserCommandRepo = SavingCommandRepository(
            userCommandRepo,
            commandSaver,
            commandDataFactory
    )

    val systemCommandRepo = InMemoryCommandRepository()
    val commandRepos = CommandRepositories(
            systemCommandRepo,
            savingUserCommandRepo
    )
    val commandMatcher = CommandMatcher()
    val commandExecutor = CommandExecutor(functionRepo)
    val commandTriggerFactory = CommandTriggerFactory

    val commandUpdater = CommandUpdater(
            commandRepos,
            commandDataFactory,
            commandTriggerFactory,
            commandMatcher
    )

    val handlerFactory = MessageHandlerFactory(
            commandRepos,
            commandMatcher,
            commandExecutor
    )

    val taskRepository = YamlTaskRepository(config.task_file)
    val taskRunnerFactory = TaskRunnerFactory

    val calculator = Calculator

    val translationLoader = YamlTranslationLoader(config.translation_file)

    val functions
        get() = listOf(
                ListFunctionsFunction(functionRepo),
                ListCommandsFunction(commandRepos),
                FindCommandFunction(commandRepos, commandMatcher),
                RemoveCommandFunction(commandRepos),
                SetCommandFunction(commandUpdater),

                RequestFunction,
                ChooseFunction,
                ConcatFunction,
                RandomFunction,
                RepeatFunction,
                CalculateFunction(calculator),
        )

    val systemCommands
        get() = listOf(
            CommandInfo("list_functions", "list_functions", "list_functions()"),
            CommandInfo("list_commands", "list_commands", "list_commands()"),
            CommandInfo("find_command", "find_command (?<search>.+)", "find_command(req('search'))"),
            CommandInfo("remove_command", "remove_command (?<id>\\w+)", "remove_command(req('id'))"),
            CommandInfo("set_command", "set_command (?<id>\\w+) '(?<triggers>[^']+)' (?<command>.+)", "set_command(req('id'), req('triggers'), req('command'))"),
    )
}