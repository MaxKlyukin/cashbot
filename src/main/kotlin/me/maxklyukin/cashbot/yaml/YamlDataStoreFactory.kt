package me.maxklyukin.cashbot.yaml

import kotlinx.serialization.KSerializer
import me.maxklyukin.cashbot.data.DataStore
import me.maxklyukin.cashbot.data.DataStoreFactory
import java.io.File

class YamlDataStoreFactory(private val dataDir: String): DataStoreFactory {

    init {
        val dir = File(dataDir)

        val exists = dir.exists()
        if (exists && !dir.isDirectory) {
            throw RuntimeException("Data dir '$dataDir' is not a directory")
        }

        if (!exists) {
            dir.mkdir()
        }
    }

    override fun <T> make(name: String, serializer: KSerializer<T>): DataStore<T> {
        val fileName = "$dataDir/$name.yaml"

        return YamlDataStore(fileName, serializer)
    }
}