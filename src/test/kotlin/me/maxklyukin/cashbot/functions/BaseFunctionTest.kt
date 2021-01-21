package me.maxklyukin.cashbot.functions

import kotlinx.coroutines.runBlocking
import me.maxklyukin.TestTranslator
import me.maxklyukin.cashbot.translation.Translator
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

abstract class BaseFunctionTest {

    protected val translator = TestTranslator()

    init {
        Translator.setDefault(translator)
    }

    protected abstract fun func(): Function

    protected fun assertFails(arguments: List<Value>, expectedMessage: String) {
        val function = func()
        val context = RequestContext(arguments, function.arguments, mapOf())
        val exception = assertFailsWith<ExecutionException> { runBlocking { function.apply(context) } }
        assertEquals(expectedMessage, exception.message, "Wrong expected exception message")
    }

    protected fun assertCompletes(arguments: List<Value>, expected: Value) {
        assertCompletesWith(arguments) {result ->
            assertEquals(expected, result)
        }
    }

    protected fun assertCompletesWith(arguments: List<Value>, op: (result: Value) -> Unit) {
        val function = func()
        val context = RequestContext(arguments, function.arguments, mapOf())
        val result = runBlocking { function.apply(context) }
        op(result)
    }
}