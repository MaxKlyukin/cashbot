package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.translation.t

class ListFunctionsFunction(private val functionRepo: FunctionRepository) : Function() {
    override val name = "list_functions"
    override val description = t("prints_list_of_functions")
    override val arguments = listOf<String>()

    override suspend fun apply(context: RequestContext): Value {
        val functions = functionRepo.getAll()
                .joinToString("\n") {
                    "*${it.name}(${it.arguments.joinToString(", ")})* - ${it.description}"
                }

        return text(functions)
    }
}