package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.translation.t

object ConcatFunction : Function() {

    override val name = "concat"
    override val description = t("concatenates_arguments_into_a_string")
    override val arguments = listOf<String>()

    override suspend fun apply(context: RequestContext): Value {
        if (context.arguments.isEmpty()) {
            throw ExecutionException(t("no_arguments"))
        }

        return text(context.arguments.joinToString("") { it.toString() })
    }
}