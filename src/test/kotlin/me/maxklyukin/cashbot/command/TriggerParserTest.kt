package me.maxklyukin.cashbot.command

import me.maxklyukin.cashbot.command.TriggerPart.Group
import me.maxklyukin.cashbot.command.TriggerPart.Text
import me.maxklyukin.cashbot.parser.ParseException
import org.junit.Test
import kotlin.test.*

class TriggerParserTest {

    @Test
    fun itFailsForEmptyString() {
        assertFails("Expected: Trigger, Got: End of Trigger String", "")
    }

    @Test
    fun itFailsForBlankString() {
        assertFails("Expected: Trigger, Got: End of Trigger String", "  \t  \r  ")
    }

    @Test
    fun itMatchesTriggerWord() {
        val expected = listOf(Trigger(listOf(Text("test"))))

        assertParses(expected, "test")
    }

    @Test
    fun itMatchesTriggerSentence() {
        val expected = listOf(Trigger(listOf(Text("test раз два? .() тест"))))

        assertParses(expected, "test раз два? .() тест")
    }

    @Test
    fun itFailsWithEmptySeparatedTrigger1() {
        assertFails("Expected: Trigger, Got: End of Trigger String", "test|")
    }

    @Test
    fun itFailsWithEmptySeparatedTrigger2() {
        assertFails("Expected: Trigger, Got: Symbol '|', at: 1", "|test")
    }

    @Test
    fun itFailsWithEmptySeparatedTrigger3() {
        assertFails("Expected: Trigger, Got: Symbol '|', at: 8", "тест|  |check")
    }

    @Test
    fun itMatchesSeparatedTriggers() {
        val expected = listOf(
                Trigger(listOf(Text("test"))),
                Trigger(listOf(Text("раз"))),
                Trigger(listOf(Text("тест")))
        )

        assertParses(expected, "test|раз|тест")
    }

    @Test
    fun itFailsWithBrokenTrigger1() {
        assertFails("Expected: Symbol '<', Got: Symbol ':', at: 8", "test (?:one:\\d+)")
    }

    @Test
    fun itFailsWithBrokenTriggerWithoutName() {
        assertFails("Expected: Symbol '<', Got: Symbol '\\', at: 8", "test (?\\d+)")
    }

    @Test
    fun itFailsWithBrokenTriggerWithEmptyName() {
        assertFails("Expected: Group Name, Got: Symbol '>', at: 9", "test (?<>\\d+)")
    }

    @Test
    fun itFailsWithBrokenTriggerWithIllegalName() {
        assertFails("Expected: Group Name, Got: Symbol 'в', at: 9", "test (?<в>\\d+)")
    }

    @Test
    fun itFailsWithBrokenTriggerWithBlankName() {
        assertFails("Expected: Group Name, Got: Symbol ' ', at: 9", "test (?<  \t  >\\d+)")
    }

    @Test
    fun itFailsWithBrokenTriggerWithoutPattern() {
        assertFails("Expected: Group Pattern, Got: Symbol ')', at: 13", "test (?<one>)")
    }

    @Test
    fun itFailsWithBlankTriggerWithAGroup() {
        assertFails("Expected: Trigger, Got: End of Trigger String", " (?<one>\\d+)  ")
    }

    @Test
    fun itMatchesTriggerWithGroup() {
        val expected = listOf(
                Trigger(listOf(
                        Text("test "),
                        Group("one", "\\d+")
                ))
        )

        assertParses(expected, "test (?<one>\\d+)")
    }

    @Test
    fun itMatchesTriggerWithSeveralGroups() {
        val expected = listOf(
                Trigger(listOf(
                        Text("test "),
                        Group("one", "\\w"),
                        Text("бла"),
                        Group("two", "[a-Z.]+"),
                        Text("бла")
                ))
        )

        assertParses(expected, "test (?<one>\\w)бла(?<two>[a-Z.]+)бла")
    }

    @Test
    fun itMatchesTriggerWithSeparatorInAGroup() {
        val expected = listOf(
                Trigger(listOf(
                        Text("test "),
                        Group("one", "\\d|\\w")
                ))
        )

        assertParses(expected, "test (?<one>\\d|\\w)")
    }

    @Test
    fun itMatchesSeparatedTriggersWithGroups() {
        val expected = listOf(
                Trigger(listOf(
                        Text("test "),
                        Group("one", "\\d")
                )),
                Trigger(listOf(
                        Text(" "),
                        Group("two", "\\w"),
                        Text(" тест")
                ))
        )

        assertParses(expected, "test (?<one>\\d)| (?<two>\\w) тест")
    }

    @Test
    fun itMatchesComplexTriggers() {
        val expected = listOf(
                Trigger(listOf(
                        Text("test "),
                        Group("one", "\\d"),
                        Group("one_and_a_half", "\\w")
                )),
                Trigger(listOf(
                        Text("вот такой"),
                        Group("two", "[a-Zа-Я]+"),
                        Text(" тест")
                )),
                Trigger(listOf(
                        Text(".     "),
                        Group("three", "che[ck][k]?"),
                        Text("     ")
                ))
        )

        assertParses(expected, "test (?<one>\\d)(?<one_and_a_half>\\w)|вот такой(?<two>[a-Zа-Я]+) тест|.     (?<three>che[ck][k]?)     ")
    }

    private fun assertFails(expectedMessage: String, triggerString: String) {
        val exception = assertFailsWith<ParseException> { parse(triggerString) }
        assertEquals(expectedMessage, exception.message, "Wrong expected exception message")
    }

    private fun assertParses(expected: List<Trigger>, triggerString: String) {
        assertEquals(expected, parse(triggerString))
    }

    private fun parse(triggerString: String): List<Trigger> {
        return TriggerParser(triggerString).parse()
    }
}