package me.maxklyukin.cashbot.translation

class Translator(loader: TranslationLoader, private val lang: String) {
    private val translations = loader.load()

    fun translate(id: String, vars: Map<String, String> = mapOf()): String {
        if (!translations.containsKey(id)) {
            throw TranslationException("No translations for $id")
        }

        val translation = translations[id]!!.find { it.lang == lang }
                ?: throw TranslationException("No translation to $lang for $id")

        return vars.toList()
                .fold(translation.translation) { t, (key, value) ->
                    t.replace("%$key%", value)
                }
    }

    companion object {
        private var _default: Translator? = null

        val default: Translator
            get() {
                if (_default == null) throw TranslationException("No default translation was set")
                return _default!!
            }

        fun setDefault(translator: Translator) {
            _default = translator
        }
    }
}

fun t(id: String, vars: Map<String, String> = mapOf()): String {
    return Translator.default.translate(id, vars)
}