package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.command.CommandFormatter
import me.maxklyukin.cashbot.command.CommandRepositories
import me.maxklyukin.cashbot.translation.t

class ListCommandsFunction(private val commandRepos: CommandRepositories) : Function() {
    override val name = "list_commands"
    override val description = t("prints_list_of_commands")
    override val arguments = listOf<String>()

    override suspend fun apply(context: RequestContext): Value {
        val commands = commandRepos.getAll()
                .joinToString("\n") {
                    CommandFormatter.format(it)
                }

        return text(commands)
    }

}