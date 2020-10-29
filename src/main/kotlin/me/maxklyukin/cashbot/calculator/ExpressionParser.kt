package me.maxklyukin.cashbot.calculator

import me.maxklyukin.cashbot.parser.Parser

class ExpressionParser(expressionString: String) {
    private val parser = Parser(expressionString, endOfStringMessage = "End of Expression")

    private val operationRegex = "[+-/*^]".toRegex()
    private val startGroupSymbol = '('
    private val endGroupSymbol = ')'

    fun parse(): List<ExpressionToken> {
        parser.reset()

        val tokens = parser.withSpaces {
            parseTokens()
        }

        parser.guardFinished()

        return tokens
    }

    private fun parseTokens() : List<ExpressionToken> {
        val tokens = mutableListOf<ExpressionToken>()

        while (!parser.isFinished() && !parser.currentCharacterMatches(endGroupSymbol)) {
            val token = parseToken()
            tokens.add(token)
        }

        return tokens
    }

    private fun parseToken(): ExpressionToken {
        val expected = "Number or Operation or Brackets"

        return parser.withSpaces {
            when {
                parser.currentCharacterLooksLikeNumber(nonNegative = true) -> parseNumber()
                parser.currentCharacterMatches(operationRegex) -> parseOperation()
                parser.currentCharacterMatches(startGroupSymbol) -> parseGroup()
                else -> throw parser.unexpectedSymbol(expected)
            }
        }
    }

    private fun parseNumber(): ExpressionToken.Number {
        return ExpressionToken.Number(parser.matchNumber(nonNegative = true))
    }

    private fun parseOperation(): ExpressionToken.Operation {
        val expected = "Operation"

        return when {
            parser.currentCharacterMatches('+') -> {
                val operation = ExpressionToken.Operation.Addition
                parser.next()
                operation
            }
            parser.currentCharacterMatches('-') -> {
                val operation = ExpressionToken.Operation.Subtraction
                parser.next()
                operation
            }
            parser.currentCharacterMatches('/') -> {
                val operation = ExpressionToken.Operation.Division
                parser.next()
                operation
            }
            parser.currentCharacterMatches('*') -> {
                val operation = ExpressionToken.Operation.Multiplication
                parser.next()
                operation
            }
            parser.currentCharacterMatches('^') -> {
                val operation = ExpressionToken.Operation.Exponentiation
                parser.next()
                operation
            }
            else -> throw parser.unexpectedSymbol(expected)
        }
    }

    private fun parseGroup(): ExpressionToken.Group {
        parser.match(startGroupSymbol)

        val tokens = parseTokens()

        parser.match(endGroupSymbol)

        return ExpressionToken.Group(tokens)
    }
}

sealed class ExpressionToken {
    sealed class Operation : ExpressionToken() {
        object Addition : Operation()
        object Subtraction : Operation()
        object Division : Operation()
        object Multiplication : Operation()
        object Exponentiation : Operation()
    }

    data class Group(val tokens: List<ExpressionToken>): ExpressionToken()

    data class Number(val value: Double): ExpressionToken()
}