package me.maxklyukin.cashbot.command

import me.maxklyukin.cashbot.functions.*
import me.maxklyukin.cashbot.translation.t

class CommandExecutor(private val functionRepo: FunctionRepository) {

    /**
     * @throws ExecutionException
     */
    suspend fun execute(command: Command, requestParameters: Map<String, String> = mapOf()): Value {
        return when (command) {
            is Command.Number -> Value.Number(command.value)
            is Command.Text -> Value.Text(command.value)
            is Command.Function -> executeFunction(command, requestParameters)
        }
    }

    /**
     * @throws ExecutionException
     */
    private suspend fun executeFunction(functionCommand: Command.Function, requestParameters: Map<String, String>): Value {
        val function = functionRepo.search(functionCommand.name)
                ?: throw ExecutionException(t("function_not_found", mapOf("name" to functionCommand.name)))

        val arguments = functionCommand.arguments.map { execute(it, requestParameters) }

        val context = RequestContext(arguments, function.arguments, requestParameters)

        return function.apply(context)
    }
}