package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.translation.t
import kotlin.random.Random

object ChooseFunction: Function() {
    override val name = "choose"
    override val description = t("chooses_random_argument")
    override val arguments = listOf("arguments...")

    override suspend fun apply(context: RequestContext): Value {
        if (context.arguments.isEmpty()) {
            throw ExecutionException(t("no_arguments"))
        }

        return context.arguments[Random.nextInt(context.arguments.size)]
    }
}