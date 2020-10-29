package me.maxklyukin.cashbot.client

import me.maxklyukin.cashbot.ClientConfig
import me.maxklyukin.cashbot.client.slack.SlackClient
import me.maxklyukin.cashbot.client.slack.SlackConnectionInfoRetriever

class ClientFactory(private val config: ClientConfig) {
    fun getClient(): Client {
        when (config) {
            is ClientConfig.Slack -> {
                return SlackClient(config, SlackConnectionInfoRetriever())
            }
            else -> {
                throw ClientException("Invalid client type in config")
            }
        }
    }
}