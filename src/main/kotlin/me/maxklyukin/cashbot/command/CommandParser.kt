package me.maxklyukin.cashbot.command

import me.maxklyukin.cashbot.parser.Parser

class CommandParser(commandString: String) {
    private val parser = Parser(commandString, endOfStringMessage = "End of Command")

    private val commandNameRegex = "[a-z_]".toRegex()

    private val textQuoteSymbol = '\''
    private val startArgumentsSymbol = '('
    private val endArgumentsSymbol = ')'
    private val argumentsSeparatorSymbol = ','

    fun parse(): Command {
        parser.reset()

        val commandOrText = parser.withSpaces {
            tryMatchCommand()
        }

        parser.guardFinished()

        return commandOrText
    }

    private fun tryMatchCommand(): Command {
        val expected = "Function or Number or Text"
        parser.guardNotFinished(expected)

        return when {
            parser.currentCharacter() == textQuoteSymbol -> matchText()
            parser.currentCharacterLooksLikeNumber() -> matchNumber()
            parser.currentCharacterMatches(commandNameRegex) -> matchCommand()
            else -> throw parser.unexpectedSymbol(expected)
        }
    }

    private fun matchText(): Command.Text {
        parser.match(textQuoteSymbol)

        val text = parser.matchWhile {
            !parser.currentCharacterMatches(textQuoteSymbol)
        }

        parser.match(textQuoteSymbol)

        return Command.Text(text)
    }

    private fun matchNumber(): Command.Number {
        return Command.Number(parser.matchNumber())
    }

    private fun matchCommand(): Command.Function {
        val name = matchCommandName()
        parser.matchAnySpaces()
        val arguments = matchCommandArguments()

        return Command.Function(name, arguments)
    }

    private fun matchCommandName(): String {
        return parser.matchWhile {
            parser.currentCharacterMatches(commandNameRegex)
        }
    }

    private fun matchCommandArguments(): MutableList<Command> {
        val arguments = mutableListOf<Command>()

        parser.match(startArgumentsSymbol)
        parser.matchAnySpaces()
        while (!parser.isFinished() && parser.currentCharacter() != endArgumentsSymbol) {
            parser.withSpaces {
                arguments.add(tryMatchCommand())
            }

            if (!parser.isFinished() && parser.currentCharacter() == argumentsSeparatorSymbol) {
                parser.match(argumentsSeparatorSymbol)
                parser.matchAnySpaces()
                continue
            } else {
                break
            }
        }
        parser.matchAnySpaces()
        parser.match(endArgumentsSymbol)

        return arguments
    }
}