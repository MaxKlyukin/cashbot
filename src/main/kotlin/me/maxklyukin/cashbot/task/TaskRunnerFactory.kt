package me.maxklyukin.cashbot.task

import me.maxklyukin.cashbot.message.MessageHandler

object TaskRunnerFactory {
    fun make(messageHandler: MessageHandler): TaskRunner {
        return TaskRunner(messageHandler)
    }
}