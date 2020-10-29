package me.maxklyukin.cashbot.client

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import me.maxklyukin.cashbot.message.Message
import me.maxklyukin.cashbot.message.Response

interface Client {
    fun communicate(messages: SendChannel<Message>, responses: ReceiveChannel<Response>): Job
}