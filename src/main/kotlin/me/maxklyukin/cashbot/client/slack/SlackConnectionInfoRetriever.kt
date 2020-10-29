package me.maxklyukin.cashbot.client.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.*
import io.ktor.client.request.get
import io.ktor.util.*
import org.slf4j.LoggerFactory

class SlackConnectionInfoRetriever {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val httpClient = HttpClient/*(CIO)*/ {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        install(Logging) {
            level = LogLevel.NONE
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class UserInfo(
            val id: String,
            val name: String
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ChannelInfo(
            val id: String,
            val name: String,
            val is_general: Boolean
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class DMInfo(
            val id: String,
            val user: String
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ConnectionInfo(
            val ok: Boolean,
            val url: String,
            val self: UserInfo,
            val users: List<UserInfo>,
            val channels: List<ChannelInfo>,
            val ims: List<DMInfo>,
    )

    private suspend fun getConnectionInfo(url: String): ConnectionInfo {
        return httpClient.get(url)
    }

    private suspend fun fetchConnectionInfo(url: String): ConnectionInfo? {
        return try {
            getConnectionInfo(url)
        } catch (e: RuntimeException) {
            logger.error("Failed to get connection info")
            logger.error(e)
            null
        }
    }

    suspend fun getInfo(token: String): SlackInfo? {
        val response = fetchConnectionInfo("https://slack.com/api/rtm.start?token=$token")

        return if (response != null && response.ok) {
            SlackInfo(
                    response.url,
                    SlackUserInfo(response.self.id, response.self.name),
                    response.users.map { SlackUserInfo(it.id, it.name) },
                    response.channels.map { SlackChannelInfo(it.id, it.name, it.is_general) },
                    response.ims.map { SlackDMInfo(it.id, it.user) }
            )
        } else null
    }
}

data class SlackInfo(
        val url: String,
        val self: SlackUserInfo,
        val users: List<SlackUserInfo>,
        val channels: List<SlackChannelInfo>,
        val dms: List<SlackDMInfo>
)

data class SlackUserInfo(
        val id: String,
        val name: String
)

data class SlackChannelInfo(
        val id: String,
        val name: String,
        val is_general: Boolean
)


data class SlackDMInfo(
        val id: String,
        val userId: String
)
