package me.maxklyukin.cashbot.functions

import me.maxklyukin.cashbot.translation.t
import kotlin.reflect.KClass

abstract class Function {
    abstract val name: String
    abstract val description: String
    abstract val arguments: List<String>

    /**
     * @throws ExecutionException
     */
    abstract suspend fun apply(context: RequestContext): Value

    protected val nothing: Value.Nothing = Value.Nothing

    protected fun text(value: String): Value.Text {
        return Value.Text(value)
    }

    protected fun number(value: Number): Value.Number {
        return Value.Number(value.toDouble())
    }
}

data class RequestContext(
        val arguments: List<Value>,
        val argumentNames: List<String>,
        val requestParameters: Map<String, String>
) {
    fun textArgument(name: String): String {
        val argument = argument(name)

        return if (argument is Value.Text)
            argument.value
        else throw wrongType(name, argument, Value.Text::class)
    }

    fun numberArgument(name: String): Double {
        val argument = argument(name)

        return if (argument is Value.Number)
            argument.value
        else throw wrongType(name, argument, Value.Number::class)
    }

    fun hasArgument(name: String): Boolean {
        val idx = argumentNames.indexOf(name)

        return idx >= 0 && idx < arguments.size
    }

    fun argument(name: String): Value {
        val idx = argumentNames.indexOf(name)

        if (idx == -1) {
            throw ExecutionException(t("function_doesnt_have_argument", mapOf("argument" to name)))
        }

        if (idx >= arguments.size) {
            throw ExecutionException(t("argument_hasnt_been_passed", mapOf("argument" to name)))
        }

        return arguments[idx]
    }

    private fun <T : Value> wrongType(name: String, value: Value, expected: KClass<T>): Throwable {
        return ExecutionException(t("argument_cant_be_casted", mapOf(
                "argument" to name,
                "actual" to (value::class.simpleName ?: ""),
                "expected" to (expected.simpleName ?: "")
        )))
    }
}

class ExecutionException(override val message: String) : RuntimeException()