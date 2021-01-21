package me.maxklyukin.cashbot.translation

interface Translator {
    fun translate(id: String, vars: Map<String, String> = mapOf()): String


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

class DefaultTranslator(loader: TranslationLoader, private val lang: String): Translator {
    private val translations = loader.load()

    override fun translate(id: String, vars: Map<String, String>): String {
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
}

fun t(id: String, vars: Map<String, String> = mapOf()): String {
    return Translator.default.translate(id, vars)
}