package me.maxklyukin.cashbot.yaml

import kotlinx.serialization.Serializable
import me.maxklyukin.cashbot.command.*

@Serializable
private data class Commands(
        val commands: List<CommandRow>
)

@Serializable
private data class CommandRow(
        val id: String,
        val triggers: String?,
        val command: String?
)

class YamlCommandSyncer(fileName: String) : CommandSaver, CommandLoader {
    private val yamlFile = YamlFile(fileName)

    override fun load(): List<CommandInfo> {
        if (!yamlFile.file.exists()) {
            return listOf()
        }

        val (commandRows) = yamlFile.read(Commands.serializer())

        return commandRows.map { commandRow ->
            CommandInfo(commandRow.id, commandRow.triggers, commandRow.command)
        }
    }

    override fun save(commandsInfo: List<CommandInfo>) {

        val commandRows = commandsInfo.map { info ->
            CommandRow(info.id, info.triggerString, info.commandString)
        }

        yamlFile.write(Commands.serializer(), Commands(commandRows))
    }
}