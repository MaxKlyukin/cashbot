package me.maxklyukin.cashbot.parser

class ParseException(val expected: String, val got: String, val position: Int? = null) : RuntimeException() {
    override val message: String = "Expected: $expected, Got: $got${if (position != null) ", at: $position" else ""}"
}