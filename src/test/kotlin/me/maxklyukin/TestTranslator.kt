package me.maxklyukin

import me.maxklyukin.cashbot.translation.Translation
import me.maxklyukin.cashbot.translation.TranslationException
import me.maxklyukin.cashbot.translation.TranslationListTranslator
import me.maxklyukin.cashbot.translation.Translator

class TestTranslator(
    private val default: String = "test",
    private val translations: MutableMap<String, List<Translation>> = mutableMapOf()
) : Translator {

    private val lang = "test"

    private val translator = TranslationListTranslator(translations, lang)

    override fun translate(id: String, vars: Map<String, String>): String {
        return try {
            translator.translate(id, vars)
        } catch (e: TranslationException) {
            default
        }
    }

    fun addTranslation(id: String, translation: String) {
        translations[id] = listOf(Translation(lang, translation))
    }
}