package me.maxklyukin.cashbot.command

data class CommandData(val id: String, val triggers: List<Trigger>, val command: Command)