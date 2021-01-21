package me.maxklyukin.cashbot.client.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.HttpClient
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.wss
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import me.maxklyukin.cashbot.ClientConfig
import me.maxklyukin.cashbot.client.Client
import me.maxklyukin.cashbot.message.Message
import me.maxklyukin.cashbot.message.RespondTo
import me.maxklyukin.cashbot.message.Response
import me.maxklyukin.cashbot.timer.Timer
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class SlackClient(
        private val config: ClientConfig.Slack,
        private val infoRetriever: SlackConnectionInfoRetriever
) : Client, CoroutineScope {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private fun getClient(): HttpClient {
        return HttpClient/*(CIO)*/ {
            install(Logging) {
                level = LogLevel.NONE
            }
            install(WebSockets)
        }
    }

    private var retries: Int = 0
    private var running: Boolean = true
    private var connecting: Boolean = false
    private var connected: Boolean = false

    override fun communicate(messages: SendChannel<Message>, responses: ReceiveChannel<Response>) =
            launch {
                while (running) {
                    if (!connected && !connecting) {
                        connect(messages, responses)
                    }
                    delay(60 * 1000)
                }
            }

    fun stop() {
        running = false
    }

    private suspend fun connect(messages: SendChannel<Message>, responses: ReceiveChannel<Response>) {
        if (retries++ >= MAX_RETRIES) {
            logger.error("Max retries reached")
            running = false

            return
        }

        connecting = true

        val info = infoRetriever.getInfo(config.token)

        if (info == null) {
            logger.error("Could not get Slack connection info")

            connecting = false

            return
        }

        getClient().use {
            try {
                it.wss(urlString = info.url, request = {}, block = {
                    connected = true
                    connecting = false
                    retries = 0

                    logger.info("Connected to slack")

                    val client = SlackClientHandler(info, messages, responses, incoming, outgoing)

                    client.onDisconnect {
                        logger.info("Disconnected from Slack")

                        client.stop()
                        connected = false
                    }

                    client.start().join()
                })
            } catch (e: RuntimeException) {
                connecting = false
                logger.error("Failed to connect to Slack")
                logger.error(e)
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job()

    companion object {
        const val MAX_RETRIES = 10

        internal val processContext = newSingleThreadContext("SlackMessageEncoder")
    }
}

class SlackClientHandler(
        private val info: SlackInfo,
        private val messages: SendChannel<Message>,
        private val responses: ReceiveChannel<Response>,
        private val incoming: ReceiveChannel<Frame>,
        private val outgoing: SendChannel<Frame>
) : CoroutineScope {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private var messagesJob: Job? = null
    private var responsesJob: Job? = null
    private var pingPongJob: Job? = null

    private var onDisconnectOp: (() -> Unit)? = null

    private val messageId = AtomicInteger(0)

    private val generalChannelId = info.channels.find { it.is_general }!!.id

    fun start() = launch {
        messagesJob = launch { handleMessages(incoming, messages) }
        responsesJob = launch { handleResponses(outgoing, responses) }
        pingPongJob = Timer("slack ping pong", 60 * 1000, 60 * 1000) { ping(outgoing) }.start()

        messagesJob!!.join()
        responsesJob!!.join()
        pingPongJob!!.join()
    }

    fun stop() {
        messagesJob?.cancel()
        responsesJob?.cancel()
        pingPongJob?.cancel()
    }

    private suspend fun handleMessages(incoming: ReceiveChannel<Frame>, messages: SendChannel<Message>) {
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    handleMessage(frame.readText(), messages)
                }
                else -> {
                    logger.info("Non text frame '$frame'")
                }
            }
        }

        onDisconnectOp?.invoke()
    }

    private suspend fun handleResponses(outgoing: SendChannel<Frame>, responses: ReceiveChannel<Response>) {
        for (response in responses) {
            when (response) {
                is Response.WithText -> handleResponse(outgoing, response)
                is Response.Empty -> {
                }
            }
        }
    }

    private suspend fun ping(outgoing: SendChannel<Frame>) {
        logger.info("<< PING")

        val responseString = encodeOutgoing(SlackOutgoing.Ping(messageId.incrementAndGet()))

        outgoing.send(Frame.Text(responseString))
    }

    private suspend fun handleMessage(messageString: String, messages: SendChannel<Message>) {
        val message = decodeIncoming(messageString)

        val channelId = message.channel

        if (message.type == "message" && message.text != null && message.user != null && channelId != null) {
            logger.info(">> (${idToName(channelId)}) ${message.text}")
            messages.send(Message(message.text, message.user, channelId))
        }
    }

    private suspend fun handleResponse(outgoing: SendChannel<Frame>, response: Response.WithText) {
        val channelId = when (response.respondTo) {
            is RespondTo.Id -> response.respondTo.id
            is RespondTo.Default -> generalChannelId
        }

        logger.info("<< (${idToName(channelId)}) ${response.text}")
        val slackResponse = SlackOutgoing.Message(messageId.incrementAndGet(), channelId, response.text)

        val responseString = encodeOutgoing(slackResponse)

        outgoing.send(Frame.Text(responseString))
    }

    private suspend fun encodeOutgoing(outgoing: SlackOutgoing): String = runInterruptible(SlackClient.processContext) {
        val mapper = jacksonObjectMapper()

        mapper.writeValueAsString(outgoing)
    }

    private suspend fun decodeIncoming(incoming: String): SlackIncomingMessage = runInterruptible(SlackClient.processContext) {
        val mapper = jacksonObjectMapper()

        mapper.readValue(incoming)
    }

    private fun idToName(id: String): String {
        val channelInfo = info.channels.find { it.id == id }
        if (channelInfo !== null) {
            return "channel: ${channelInfo.name}"
        }

        val dmInfo = info.dms.find { it.id == id }
        val dmUserInfo = dmInfo?.let { dm -> info.users.find { it.id == dm.userId } }
        if (dmUserInfo !== null) {
            return "dm: ${dmUserInfo.name}"
        }

        return id
    }

    fun onDisconnect(op: () -> Unit) {
        onDisconnectOp = op
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class SlackIncomingMessage(val type: String?, val text: String?, val user: String?, val channel: String?)

    private sealed class SlackOutgoing {
        abstract val id: Int
        abstract val type: String

        data class Message(override val id: Int, val channel: String, val text: String) : SlackOutgoing() {
            override val type = "message"
        }

        data class Ping(override val id: Int) : SlackOutgoing() {
            override val type = "ping"
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}