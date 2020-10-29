package me.maxklyukin.cashbot.parser

class Parser(private val content: String, endOfStringMessage: String? = null) {
    private val endOfStringMessage = endOfStringMessage ?: "End of String"

    private val spaceRegex = "\\s".toRegex()
    private val nonNegativeNumberRegex = "[\\d.]".toRegex()
    private val numberRegex = "[\\d.-]".toRegex()

    private var position: Int = 0

    fun reset() {
        position = 0
    }

    fun next() {
        position++
    }

    fun <T>withSpaces(op: () -> T): T {
        matchAnySpaces()
        val result = op()
        matchAnySpaces()

        return result
    }

    fun matchAnySpaces() {
        while (!isFinished() && currentCharacter().toString().matches(spaceRegex)) {
            next()
        }
    }

    fun matchWhile(cond: () -> Boolean): String {
        val sequenceBuilder = StringBuilder()

        while (!isFinished() && cond()) {
            sequenceBuilder.append(currentCharacter())
            next()
        }

        return sequenceBuilder.toString()
    }

    fun matchNumber(nonNegative: Boolean = false): Double {
        val expected = "Number"

        val regex = if (nonNegative) nonNegativeNumberRegex else numberRegex

        val numberString = matchWhile {
            currentCharacterMatches(regex)
        }

        try {
            return numberString.toDouble()
        } catch (e: NumberFormatException) {
            throw unexpectedSymbol(expected)
        }
    }

    fun match(toMatch: Char) {
        this.guardNotFinished(toMatch)

        if (!currentCharacterMatches(toMatch)) {
            throw unexpectedSymbol(toMatch)
        }

        next()
    }

    fun currentCharacter(): Char {
        return content.elementAt(position)
    }

    fun currentCharacterMatches(toMatch: Regex): Boolean {
        return currentCharacter().toString().matches(toMatch)
    }

    fun currentCharacterMatches(toMatch: Char): Boolean {
        return content.elementAt(position) == toMatch
    }

    fun currentCharactersMatch(toMatch: String): Boolean {
        return content.regionMatches(position, toMatch, 0, toMatch.length)
    }

    fun currentCharacterLooksLikeNumber(nonNegative: Boolean = false): Boolean {
        return currentCharacterMatches(if (nonNegative) nonNegativeNumberRegex else numberRegex)
    }

    fun isFinished() = position >= content.length

    fun unexpectedSymbol(expected: String): ParseException {
        guardNotFinished(expected)

        return ParseException(expected, symbol(currentCharacter()), position + 1)
    }

    fun unexpectedSymbol(expected: Char): ParseException {
        return unexpectedSymbol(symbol(expected))
    }

    fun guardNotFinished(expected: String) {
        if (isFinished()) {
            throw ParseException(expected, endOfStringMessage)
        }
    }

    fun guardNotFinished(expected: Char) {
        guardNotFinished(symbol(expected))
    }

    fun symbol(char: Char): String {
        return "Symbol '$char'"
    }

    fun guardFinished() {
        if (!isFinished()) {
            throw unexpectedSymbol(endOfStringMessage)
        }
    }
}