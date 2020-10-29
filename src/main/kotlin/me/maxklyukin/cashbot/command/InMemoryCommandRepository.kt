package me.maxklyukin.cashbot.command

class InMemoryCommandRepository : CommandRepository {

    private var commandsData: MutableMap<String, CommandData> = mutableMapOf()

    override fun has(id: String): Boolean {
        return this.commandsData.contains(id)
    }

    override fun get(id: String): CommandData? {
        return commandsData[id]
    }

    override fun set(data: CommandData) {
        commandsData[data.id] = data
    }

    override fun remove(id: String) {
        commandsData.remove(id)
    }

    override fun getAll(): List<CommandData> {
        return commandsData.values.toList()
    }
}