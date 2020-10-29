package me.maxklyukin.cashbot.calculator

import me.maxklyukin.cashbot.parser.ParseException
import java.math.BigDecimal

object Calculator {

    fun calculate(input: String): BigDecimal {
        val tokens = try {
            ExpressionParser(input).parse()
        } catch (e: ParseException) {
            throw CalculationException(e.message)
        }

        val expression = ExpressionBuilder(tokens).build()

        return ExpressionExecutor.execute(expression)
    }
}