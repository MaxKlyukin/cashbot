package me.maxklyukin.cashbot.calculator

import java.math.BigDecimal
import java.math.BigDecimal.ROUND_HALF_UP
import java.math.RoundingMode

object ExpressionExecutor {
    fun execute(expression: Expression?): BigDecimal {
        val result = when (expression) {
            null -> throw CalculationException("Empty expression")
            is Expression.Number -> executeNumber(expression)
            is Expression.Operation.Addition -> executeAddition(expression)
            is Expression.Operation.Subtraction -> executeSubtraction(expression)
            is Expression.Operation.Division -> executeDivision(expression)
            is Expression.Operation.Multiplication -> executeMultiplication(expression)
            is Expression.Operation.Exponentiation -> executeExponentiation(expression)
            is Expression.Negative -> executeNegative(expression)
            is Expression.Group -> executeGroup(expression)
        }

        return round(result)
    }

    private fun executeNumber(expression: Expression.Number) =
        BigDecimal(expression.value)

    private fun executeAddition(expression: Expression.Operation.Addition) =
        execute(expression.left).add(execute(expression.right))

    private fun executeSubtraction(expression: Expression.Operation.Subtraction) =
        execute(expression.left).subtract(execute(expression.right))

    private fun executeDivision(expression: Expression.Operation.Division) =
        execute(expression.left).divide(execute(expression.right), 10, RoundingMode.HALF_UP)

    private fun executeMultiplication(expression: Expression.Operation.Multiplication) =
        execute(expression.left).multiply(execute(expression.right))

    private fun executeExponentiation(expression: Expression.Operation.Exponentiation): BigDecimal {
        val power = execute(expression.power)
        return if (isIntegerValue(power)) {
            execute(expression.base).pow(power.toInt())
        } else {
            BigDecimalMath.pow(execute(expression.base), power)
        }
    }

    private fun executeNegative(expression: Expression.Negative) =
        execute(expression.value).negate()

    private fun executeGroup(expression: Expression.Group) =
        execute(expression.expression)

    private fun isIntegerValue(value: BigDecimal): Boolean {
        return value.stripTrailingZeros().scale() <= 0
    }

    private fun round(value: BigDecimal): BigDecimal {
        return value.setScale(10, ROUND_HALF_UP)
    }
}