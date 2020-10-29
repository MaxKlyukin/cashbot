package me.maxklyukin.cashbot.message

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import me.maxklyukin.cashbot.command.CommandExecutor
import me.maxklyukin.cashbot.command.CommandMatcher
import me.maxklyukin.cashbot.command.CommandRepositories

class MessageHandlerFactory(
        private val commandRepos: CommandRepositories,
        private val commandMatcher: CommandMatcher,
        private val commandExecutor: CommandExecutor
) {

    fun make(messages: ReceiveChannel<Message>, responses: SendChannel<Response>): MessageHandler {
        return MessageHandler(commandRepos, commandMatcher, commandExecutor, messages, responses)
    }
}