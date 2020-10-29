package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.command.*
import me.maxklyukin.cashbot.parser.ParseException
import me.maxklyukin.cashbot.translation.t

class SetCommandFunction(
        private val commandUpdater: CommandUpdater
) : Function() {
    override val name = "set_command"
    override val description = t("update_command")
    override val arguments = listOf("id", "triggers", "command")

    override suspend fun apply(context: RequestContext): Value {
        val commandId = context.textArgument("id")
        val triggerString = context.textArgument("triggers")
        val commandString = context.textArgument("command")

        return try {
            val info = CommandInfo(commandId, triggerString, commandString)
            val data = commandUpdater.updateUserCommand(info)

            text(CommandFormatter.format(data))
        } catch (e: ParseException) {
            throw ExecutionException(e.message)
        }
    }
}