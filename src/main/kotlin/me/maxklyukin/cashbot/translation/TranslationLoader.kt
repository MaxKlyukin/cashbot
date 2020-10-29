package me.maxklyukin.cashbot.translation

interface TranslationLoader {
    fun load(): Map<String, List<Translation>>
}
