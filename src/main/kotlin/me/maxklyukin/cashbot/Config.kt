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
data class Config(
        val translation_file: String,
        val lang: String,
        val command_file: String,
        val task_file: String,
        val data_dir: String,
        val client: ClientConfig
)