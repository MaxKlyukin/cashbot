package me.maxklyukin.cashbot.command

import me.maxklyukin.cashbot.parser.Parser

class TriggerParser(triggerString: String) {
    private val parser = Parser(triggerString, endOfStringMessage = "End of Trigger String")

    private val groupNameRegex = "\\w".toRegex()

    private val triggerSeparatorSymbol = '|'
    private val groupStartSymbols = "(?"
    private val groupEndSymbol = ')'
    private val groupNameStartSymbols = '<'
    private val groupNameEndSymbols = '>'

    fun parse(): List<Trigger> {
        parser.reset()

        val triggers = mutableListOf<Trigger>()

        while (true) {
            triggers.add(matchTrigger())

            if (!parser.isFinished() && parser.currentCharacterMatches(triggerSeparatorSymbol)) {
                parser.match(triggerSeparatorSymbol)
                continue
            } else {
                break
            }
        }

        parser.guardFinished()

        return triggers
    }

    private fun matchTrigger(): Trigger {
        val expected = "Trigger"

        parser.guardNotFinished(expected)

        val triggerParts = mutableListOf<TriggerPart>()

        while (!parser.isFinished() && !parser.currentCharacterMatches(triggerSeparatorSymbol)) {
            if (!parser.currentCharactersMatch(groupStartSymbols)) {
                triggerParts.add(matchText())
            } else {
                triggerParts.add(matchGroup())
            }
        }

        val blankTriggerText = triggerParts.filterIsInstance<TriggerPart.Text>().map { it.value }.all { it.isBlank() }
        if (blankTriggerText) {
            throw parser.unexpectedSymbol(expected)
        }

        return Trigger(triggerParts)
    }

    private fun matchText(): TriggerPart.Text {
        val text = parser.matchWhile {
            !parser.currentCharacterMatches(triggerSeparatorSymbol)
            && !parser.currentCharactersMatch(groupStartSymbols)
        }

        return TriggerPart.Text(text)
    }

    private fun matchGroup(): TriggerPart.Group {
        groupStartSymbols.forEach { parser.match(it) }

        val groupName = matchGroupName()
        val groupPattern = matchGroupPattern()

        parser.match(groupEndSymbol)

        return TriggerPart.Group(groupName, groupPattern)
    }

    private fun matchGroupName(): String {
        val expected = "Group Name"

        parser.match(groupNameStartSymbols)

        val name = parser.matchWhile {
            parser.currentCharacterMatches(groupNameRegex)
        }

        if (name.isBlank()) {
            throw parser.unexpectedSymbol(expected)
        }

        parser.match(groupNameEndSymbols)

        return name
    }

    private fun matchGroupPattern(): String {
        val expected = "Group Pattern"

        val pattern = parser.matchWhile {
            !parser.currentCharacterMatches(groupEndSymbol)
        }

        if (pattern.isEmpty()) {
            throw parser.unexpectedSymbol(expected)
        }

        return pattern
    }
}

data class Trigger(val parts: List<TriggerPart>) {
    val texts by lazy { parts.filterIsInstance<TriggerPart.Text>() }
    val groups by lazy { parts.filterIsInstance<TriggerPart.Group>() }

    val groupsNames: List<String> by lazy { groups.map { it.name } }

    companion object {
        fun withText(value: String): Trigger {
            return Trigger(listOf(TriggerPart.Text(value)))
        }
    }

    override fun toString() = parts.joinToString("")
}

sealed class TriggerPart {
    data class Text(val value: String) : TriggerPart() {
        override fun toString() = value
    }

    data class Group(val name: String, val pattern: String) : TriggerPart() {
        override fun toString() = "(?<$name>$pattern)"
    }
}