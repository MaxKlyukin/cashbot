package me.maxklyukin.cashbot.functions

import org.junit.Test
import kotlin.test.assertTrue

class ChooseFunctionTest : BaseFunctionTest() {

    init {
        translator.addTranslation("no_arguments", "no argument has been passed")
    }

    @Test
    fun itKillsExecutionIfItsTakingTooLong() {
        assertFails(listOf(), "no argument has been passed")
    }

    @Test
    fun itReturnsRandomElement() {
        val values = listOf(Value.Text("a"), Value.Text("b"), Value.Text("c"))
        assertCompletesWith(values) { result ->
            assertTrue("result is not in values") {
                result in values
            }
        }
    }

    override fun func(): Function {
        return ChooseFunction
    }
}