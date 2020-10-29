package me.maxklyukin.cashbot.calculator

import me.maxklyukin.cashbot.calculator.Expression.*
import me.maxklyukin.cashbot.calculator.Expression.Number
import me.maxklyukin.cashbot.calculator.Expression.Operation.*
import kotlin.reflect.KClass

class ExpressionBuilder(private val tokens: List<ExpressionToken>) {
    private var rootExpression: Expression? = null
    private var currentOperation: Operation? = null

    fun build(): Expression? {
        rootExpression = null
        currentOperation = null

        for (token in tokens) {
            buildForToken(token)
        }

        return rootExpression
    }

    private fun buildForToken(token: ExpressionToken) {
        when (token) {
            is ExpressionToken.Number -> buildNumber(token)
            is ExpressionToken.Operation -> buildOperation(token)
            is ExpressionToken.Group -> buildGroup(token)
        }
    }

    private fun buildGroup(token: ExpressionToken.Group) {
        val expression = Group(ExpressionBuilder(token.tokens).build())

        buildNonOperation(token, expression)
    }

    private fun buildNumber(token: ExpressionToken.Number) {
        val expression = Number(token.value)

        buildNonOperation(token, expression)
    }

    private fun buildNonOperation(token: ExpressionToken, expression: Expression) {
        when (val root = rootExpression) {
            null -> {
                rootExpression = expression
                return
            }
            is Group -> throw expectException(token::class, root::class)
            is Number -> throw throw expectException(token::class, root::class)
            is Negative -> {
                if (root.value != null) throw expectException(token::class, root.value!!::class)
                rootExpression = Negative(expression)
                return
            }
        }

        when (val current = currentOperation) {
            null -> throw unknownException()
            else -> {
                if (current.right != null) {
                    throw expectException(token::class, current.right!!::class)
                }
                current.right = expression
            }
        }
    }

    private fun unknownException() = CalculationException("Something went wrong")

    private fun buildOperation(operationToken: ExpressionToken.Operation) {
        val root = rootExpression

        if (root == null) {
            rootExpression = when (operationToken) {
                ExpressionToken.Operation.Addition -> null
                ExpressionToken.Operation.Subtraction -> Negative()
                else -> throw expectException(operationToken::class)
            }

            return
        }

        return when (root) {
            is Number -> {
                currentOperation = makeOperation(operationToken, root)
                rootExpression = currentOperation
            }
            is Group -> {
                currentOperation = makeOperation(operationToken, root)
                rootExpression = currentOperation
            }
            is Operation -> {
                when {
                    root.right == null -> throw throw expectException(operationToken::class, root::class)
                    //2*4^3 = mul(2,4) -> mul(2,exp(4,))
                    ((operationToken is ExpressionToken.Operation.Exponentiation) && (
                            root is Addition
                            || root is Subtraction
                            || root is Multiplication
                            || root is Division
                    )) -> {
                        val expression = makeOperation(operationToken, root.right!!)
                        currentOperation = expression
                        root.right = expression
                    }
                    //2+4*3 = add(2,4) -> add(2,mul(4,))
                    ((
                            operationToken is ExpressionToken.Operation.Multiplication
                            || operationToken is ExpressionToken.Operation.Division
                    ) && (root is Addition || root is Subtraction)) -> {
                        val expression = makeOperation(operationToken, root.right!!)
                        currentOperation = expression
                        root.right = expression
                    }
                    //2*4*3 = mul(2,4) -> mul(mul(2, 4),)
                    else -> {
                        currentOperation = makeOperation(operationToken, root)
                        rootExpression = currentOperation
                    }
                }

            }
            is Negative -> {
                if (root.value == null) throw expectException(operationToken::class, Subtraction::class)

                currentOperation = makeOperation(operationToken, root)
                rootExpression = currentOperation
            }
        }
    }

    private fun makeOperation(operationToken: ExpressionToken.Operation, left: Expression): Operation {
        return when (operationToken) {
            ExpressionToken.Operation.Addition -> Addition(left)
            ExpressionToken.Operation.Subtraction -> Subtraction(left)
            ExpressionToken.Operation.Division -> Division(left)
            ExpressionToken.Operation.Multiplication -> Multiplication(left)
            ExpressionToken.Operation.Exponentiation -> Exponentiation(left)
        }
    }

    private fun <T : ExpressionToken, A : Expression> expectException(type: KClass<T>, after: KClass<A>?): CalculationException {
        var message = "Unexpected ${type.simpleName}"
        if (after != null) {
            message += " after ${after.simpleName}"
        }

        return CalculationException(message)
    }

    private fun <T : ExpressionToken> expectException(type: KClass<T>): CalculationException {
        return expectException<T, Expression>(type, null)
    }
}

sealed class Expression {
    class Group(var expression: Expression?) : Expression()

    class Negative(var value: Expression? = null) : Expression()

    sealed class Operation : Expression() {

        abstract val left: Expression
        abstract var right: Expression?

        class Addition(override val left: Expression, override var right: Expression? = null) : Operation()
        class Subtraction(override val left: Expression, override var right: Expression? = null) : Operation()
        class Division(override val left: Expression, override var right: Expression? = null) : Operation()
        class Multiplication(override val left: Expression, override var right: Expression? = null) : Operation()
        class Exponentiation(override val left: Expression, override var right: Expression? = null) : Operation() {
            val base get() = left
            val power get() = right
        }
    }

    data class Number(val value: Double) : Expression()
}