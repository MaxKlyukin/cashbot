package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.translation.t
import kotlin.random.Random

object RandomFunction : Function() {

    override val name = "random"
    override val description = t("prints_random_number")
    override val arguments = listOf("until")

    override suspend fun apply(context: RequestContext): Value {
        val until = context.numberArgument("until").toInt()
        val chance = Random.nextInt(until)

        return number(chance)
    }
}