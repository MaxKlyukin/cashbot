package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.calculator.CalculationException
import me.maxklyukin.cashbot.calculator.Calculator
import me.maxklyukin.cashbot.translation.t

class CalculateFunction(private val calculator: Calculator) : Function() {

    override val name = "calculate"
    override val description = t("calculates_math_expression")
    override val arguments = listOf("operation")

    override suspend fun apply(context: RequestContext): Value {
        val input = context.textArgument("operation")

        val result = try {
            calculator.calculate(input)
        } catch (e: CalculationException) {
            throw ExecutionException("${t("expression_execution_fail")}. ${e.message}")
        }

        return text(result.stripTrailingZeros().toPlainString())
    }
}