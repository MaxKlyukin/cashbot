package me.maxklyukin.cashbot.command

interface CommandLoader {
    fun load(): List<CommandInfo>
}