package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.command.CommandRepositories
import me.maxklyukin.cashbot.command.CommandRepositoryException
import me.maxklyukin.cashbot.translation.t

class RemoveCommandFunction(private val commandRepos: CommandRepositories): Function() {
    override val name = "remove_command"
    override val description = t("removes_command")
    override val arguments = listOf("id")

    override suspend fun apply(context: RequestContext): Value {
        val commandId = context.textArgument("id")

        try {
            commandRepos.remove(commandId)

            return text(t("command_was_removed", mapOf("command" to commandId)))
        } catch (e: CommandRepositoryException) {
            throw ExecutionException(e.message)
        }
    }

}