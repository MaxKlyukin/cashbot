package me.maxklyukin.cashbot.functions

sealed class Value {
    data class Number(val value: Double): Value() {
        override fun toString(): String {
            return value.toString()
        }
    }
    data class Text(val value: String): Value() {
        override fun toString(): String {
            return value
        }
    }
    object Nothing: Value() {
        override fun toString(): String {
            return ""
        }
    }
}