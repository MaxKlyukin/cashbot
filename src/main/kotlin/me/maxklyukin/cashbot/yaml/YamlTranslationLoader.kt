package me.maxklyukin.cashbot.yaml

import kotlinx.serialization.Serializable
import me.maxklyukin.cashbot.translation.Translation
import me.maxklyukin.cashbot.translation.TranslationLoader

@Serializable
data class Translations(
        val translations: Map<String, List<TranslationRow>>
)

@Serializable
data class TranslationRow(
        val lang: String,
        val translation: String
)

class YamlTranslationLoader(fileName: String) : TranslationLoader {
    private val yamlFile = YamlFile(fileName)

    override fun load(): Map<String, List<Translation>> {
        val (translations) = yamlFile.read(Translations.serializer())

        return translations.mapValues { (_, translations) ->
            translations.map { t -> Translation(t.lang, t.translation) }
        }
    }
}