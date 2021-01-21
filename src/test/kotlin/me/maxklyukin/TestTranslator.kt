package me.maxklyukin

import me.maxklyukin.cashbot.translation.Translator

class TestTranslator(private val value: String = "test"): Translator {
    override fun translate(id: String, vars: Map<String, String>): String {
        return value
    }
}