package me.maxklyukin.cashbot.command

data class CommandInfo(val id: String, val triggerString: String?, val commandString: String?)

object CommandDataFactory {

    fun makeData(info: CommandInfo): CommandData {
        val id = info.id

        val triggers = info.triggerString?.let {
            TriggerParser(it).parse()
        } ?: listOf(Trigger.withText(id))

        val command = info.commandString?.let {
            CommandParser(it).parse()
        } ?: Command.Text(id)

        return CommandData(id, triggers, command)
    }

    fun makeInfo(data: CommandData): CommandInfo {
        val id = data.id

        val triggersString = data.triggers.joinToString("|")
        val commandString = data.command.toString()

        return CommandInfo(id, triggersString, commandString)
    }

}