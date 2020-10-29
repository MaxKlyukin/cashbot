package me.maxklyukin.cashbot.message

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import me.maxklyukin.cashbot.command.Command
import me.maxklyukin.cashbot.command.CommandExecutor
import me.maxklyukin.cashbot.command.CommandMatcher
import me.maxklyukin.cashbot.command.CommandRepositories
import me.maxklyukin.cashbot.functions.ExecutionException
import me.maxklyukin.cashbot.functions.Value
import kotlin.coroutines.CoroutineContext

class MessageHandler(
        private val commandRepos: CommandRepositories,
        private val commandMatcher: CommandMatcher,
        private val commandExecutor: CommandExecutor,
        private val messages: ReceiveChannel<Message>,
        private val responses: SendChannel<Response>
) : CoroutineScope {

    fun handleMessages() =
            launch {
                for (message in messages) {
                    handleMessage(message)
                }
            }

    private suspend fun handleMessage(message: Message) {
        val (commandId, requestParameters) = commandMatcher.match(message.text)
                ?: return

        val commandData = commandRepos.get(commandId)
                ?: return

        handleCommand(commandData.command, requestParameters, message.channel)
    }

    suspend fun handleCommand(command: Command, requestParameters: Map<String, String> = mapOf(), responseId: String? = null) {
        try {
            val result = commandExecutor.execute(command, requestParameters)
            val response = if (result is Value.Nothing) {
                Response.Empty
            } else {
                Response.WithText(result.toString(), respondTo(responseId))
            }
            responses.send(response)
        } catch (e: ExecutionException) {
            responses.send(Response.WithText("~${e.message}~", respondTo(responseId)))
        }
    }

    private fun respondTo(id: String?): RespondTo {
       return if(id != null) {
           RespondTo.Id(id)
       } else {
            RespondTo.Default
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}