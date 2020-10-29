package me.maxklyukin.cashbot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ClientConfig {
    @SerialName("slack")
    @Serializable
    data class Slack(val token: String): ClientConfig()
}

@Serializable
abstract class CustomConfig {
    abstract val key: String
}

@Serializable
data class Config(
    val translation_file: String,
    val lang: String,
    val command_file: String,
    val task_file: String,
    val data_dir: String,
    val client: ClientConfig,
    val custom: List<CustomConfig>
) {
    inline fun <reified T : CustomConfig> custom(key: String): T {
        return custom
            .filterIsInstance<T>()
            .firstOrNull { it.key == key }
            ?: throw ConfigException("No config ${T::class.simpleName} with key $key")
    }
}

class ConfigException(override val message: String) : RuntimeException(message)
