package me.maxklyukin.cashbot.yaml

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import java.io.File

class YamlFile(fileName: String) {

    val file = File(fileName)

    fun <T> read(deserializer: DeserializationStrategy<T>): T {
        val content = file.readText()

        return yaml.decodeFromString(deserializer, content)
    }

    fun <T> write(serializer: SerializationStrategy<T>, obj: T) {
        val content = yaml.encodeToString(serializer, obj)

        file.writeText(content)
    }

    companion object {
        private var yaml = makeYaml()

        private fun makeYaml(serializersModule: SerializersModule = EmptySerializersModule): Yaml {
            return Yaml(configuration = YamlConfiguration(
                polymorphismStyle = PolymorphismStyle.Property,
                polymorphismPropertyName = "type"
            ), serializersModule = serializersModule)
        }

        fun replaceModule(serializersModule: SerializersModule) {
            yaml = makeYaml(serializersModule)
        }
    }
}