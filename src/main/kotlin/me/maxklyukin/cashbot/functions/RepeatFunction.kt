package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.translation.t

object RepeatFunction : Function() {
    override val name = "repeat"
    override val description = t("repeats_string")
    override val arguments = listOf("string", "times")

    override suspend fun apply(context: RequestContext): Value {
        val string = context.argument("string").toString()
        val times = context.numberArgument("times").toInt()

        return text(string.repeat(times))
    }
}