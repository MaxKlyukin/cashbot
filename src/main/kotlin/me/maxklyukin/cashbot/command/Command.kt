package me.maxklyukin.cashbot.command

sealed class Command {
    data class Number(val value: Double) : Command() {
        override fun toString(): String {
            return value.toString()
        }
    }
    data class Text(val value: String) : Command() {
        override fun toString(): String {
            return "'$value'"
        }
    }
    data class Function(val name: String, val arguments: List<Command>) : Command() {
        override fun toString(): String {
            val argumentsString = arguments.joinToString(", ") { it.toString() }

            return "$name($argumentsString)"
        }
    }
}