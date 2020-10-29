package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.translation.t

object RequestFunction : Function() {
    override val name = "req"
    override val description = t("prints_matched_group")
    override val arguments = listOf("name", "type")

    override suspend fun apply(context: RequestContext): Value {
        val name = context.textArgument("name")
        val type = if (context.hasArgument("type")) context.textArgument("type") else "text"

        val value = context.requestParameters.getOrElse(name, {
            throw ExecutionException(t("group_wasnt_matched", mapOf("group" to name)))
        })

        return when (type) {
            "text" -> text(value)
            "number" -> number(value.toDouble())
            else -> throw ExecutionException(t("type_unknown", mapOf("type" to type)))
        }
    }
}