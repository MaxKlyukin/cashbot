package me.maxklyukin.cashbot.yaml

import kotlinx.serialization.KSerializer
import me.maxklyukin.cashbot.data.DataStore

class YamlDataStore<T>(fileName: String, private val serializer: KSerializer<T>) : DataStore<T> {
    private val yamlFile = YamlFile(fileName)

    override fun get(): T? {
        return if (yamlFile.file.exists()) {
            yamlFile.read(serializer)
        } else null
    }

    override fun set(data: T) {
        yamlFile.write(serializer, data)
    }
}