package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.command.CommandFormatter
import me.maxklyukin.cashbot.command.CommandMatcher
import me.maxklyukin.cashbot.command.CommandRepositories
import me.maxklyukin.cashbot.translation.t

class FindCommandFunction(private val commandRepos: CommandRepositories, private val commandMatcher: CommandMatcher) : Function() {
    override val name = "find_command"
    override val description = t("finds_a_command")
    override val arguments = listOf("search")

    override suspend fun apply(context: RequestContext): Value {
        val search = context.textArgument("search")

        if (commandRepos.has(search)) {
            return formatCommand(search)
        }

        val matched = commandMatcher.match(search)
        if (matched != null) {
            return formatCommand(matched.commandId)
        }

        for (data in commandRepos.getAll()) {
            if (data.triggers.any { it.texts.joinToString(" ").indexOf(search) >= 0 }) {
                return formatCommand(data.id)
            }
        }

        throw ExecutionException(t("command_not_found"))
    }

    private fun formatCommand(commandId: String): Value {
        val data = commandRepos.get(commandId)!!

        return text(CommandFormatter.format(data))
    }
}