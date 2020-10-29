package me.maxklyukin.cashbot.command

import me.maxklyukin.cashbot.command.Command.Number
import me.maxklyukin.cashbot.command.Command.Text
import me.maxklyukin.cashbot.command.Command.Function
import me.maxklyukin.cashbot.parser.ParseException
import org.junit.Test
import kotlin.test.*

class CommandParserTest {

    @Test
    fun itFailsForEmptyString() {
        assertFails("Expected: Function or Number or Text, Got: End of Command", "")
    }

    @Test
    fun itFailsForUnfinishedText1() {
        assertFails("Expected: Symbol ''', Got: End of Command", "'")
    }

    @Test
    fun itFailsForUnfinishedText2() {
        assertFails("Expected: Symbol ''', Got: End of Command", "'test")
    }

    @Test
    fun itMatchesText() {
        val expected = Text("test")

        assertParses(expected, "'test'")
    }

    @Test
    fun itMatchesTextWithSpaces() {
        val expected = Text("test")

        assertParses(expected, "  'test'    ")
    }

    @Test
    fun itMatchesIntNumber() {
        val expected = Number(1.0)

        assertParses(expected, "1")
    }

    @Test
    fun itMatchesFloatNumber() {
        val expected = Number(34.68)

        assertParses(expected, "34.68")
    }

    @Test
    fun itMatchesNegativeFloatNumber() {
        val expected = Number(-420.69)

        assertParses(expected, "-420.69")
    }

    @Test
    fun itFailsForCommandWithoutBrackets() {
        assertFails("Expected: Symbol '(', Got: End of Command", "test")
    }

    @Test
    fun itFailsForCommandWithoutClosingBracket() {
        assertFails("Expected: Symbol ')', Got: End of Command", "test(")
    }

    @Test
    fun itMatchesCommandWithoutArguments() {
        val expected = Function("test", listOf())

        assertParses(expected, "test()")
    }

    @Test
    fun itFailsForCommandWhichStartsWithMinus() {
        assertFails("Expected: Number, Got: Symbol 't', at: 2", "-test()")
    }

    @Test
    fun itFailsForCommandWhichStartsWithDigit() {
        assertFails("Expected: End of Command, Got: Symbol 't', at: 2", "1test()")
    }

    @Test
    fun itMatchesCommandWithNumberInFunctionName() {
        val expected = Function("test", listOf())

        assertParses(expected, "test()")
    }

    @Test
    fun itMatchesCommandWithTextArgument() {
        val expected = Function("test", listOf(Text("foo")))

        assertParses(expected, "test('foo')")
    }

    @Test
    fun itMatchesCommandWithArgumentAndSpaces() {
        val expected = Function("test", listOf(Text("foo")))

        assertParses(expected, "   test  (  'foo'   )   ")
    }

    @Test
    fun itFailsForCommandWithCommaInsteadOfArguments() {
        assertFails("Expected: Function or Number or Text, Got: Symbol ',', at: 6", "test(,)")
    }

    @Test
    fun itMatchesCommandWithTrailingComma() {
        val expected = Function("test", listOf(Text("foo")))

        assertParses(expected, "test('foo',)")
    }

    @Test
    fun itFailsForCommandWithCommaEmptyArgument() {
        assertFails("Expected: Function or Number or Text, Got: Symbol ',', at: 6", "test(,'foo')")
    }

    @Test
    fun itMatchesCommandInCommand() {
        val expected = Function("test", listOf(Function("foo", listOf())))

        assertParses(expected, "test(foo())")
    }

    @Test
    fun itMatchesComplexCommand() {
        val expected = Function("test", listOf(
                Function("foo", listOf()),
                Text("bar"),
                Number(-12.06),
                Function("bazz", listOf(
                        Text("check"),
                        Text("match")
                ))
        ))

        assertParses(expected, "test(foo(), 'bar', -12.060, bazz('check', 'match'),)")
    }

    @Test
    fun itMatchesComplexCommandWithSpaces() {
        val expected = Function("test", listOf(
                Function("foo", listOf()),
                Text("bar"),
                Number(-12.06),
                Function("bazz", listOf(
                        Text("check"),
                        Text("match")
                ))
        ))

        assertParses(expected, " test  ( foo( ) ,  'bar'  ,  -12.060     ,bazz  (  'check','match' ) ,   ) ")
    }

    private fun assertFails(expectedMessage: String, commandString: String) {
        val exception = assertFailsWith<ParseException> { parse(commandString) }
        assertEquals(expectedMessage, exception.message, "Wrong expected exception message")
    }

    private fun assertParses(expected: Command, commandString: String) {
        assertEquals(expected, parse(commandString))
    }

    private fun parse(commandString: String): Command {
        return CommandParser(commandString).parse()
    }
}