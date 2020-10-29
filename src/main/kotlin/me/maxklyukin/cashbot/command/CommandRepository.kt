package me.maxklyukin.cashbot.command

interface CommandRepository {
    fun has(id: String): Boolean
    fun get(id: String): CommandData?
    fun set(data: CommandData)
    fun remove(id: String)
    fun getAll(): List<CommandData>
}