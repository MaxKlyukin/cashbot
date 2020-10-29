package me.maxklyukin.cashbot.calculator

import java.math.BigDecimal
import java.math.BigDecimal.ROUND_HALF_UP
import java.math.RoundingMode

object ExpressionExecutor {
    fun execute(expression: Expression?): BigDecimal {
        val result = when (expression) {
            null -> throw CalculationException("Empty expression")
            is Expression.Number -> BigDecimal(expression.value)
            is Expression.Operation.Addition -> execute(expression.left).add(execute(expression.right))
            is Expression.Operation.Subtraction -> execute(expression.left).subtract(execute(expression.right))
            is Expression.Operation.Division -> execute(expression.left).divide(execute(expression.right), 10, RoundingMode.HALF_UP)
            is Expression.Operation.Multiplication -> execute(expression.left).multiply(execute(expression.right))
            is Expression.Operation.Exponentiation -> execute(expression.base).pow(execute(expression.power).toInt())
            is Expression.Negative -> execute(expression.value).negate()
            is Expression.Group -> execute(expression.expression)
        }

        return round(result)
    }

    private fun round(value: BigDecimal): BigDecimal {
        return value
                .setScale(10, ROUND_HALF_UP)
    }
}