package me.maxklyukin.cashbot.command

interface CommandSaver {
    fun save(commandsInfo: List<CommandInfo>)
}

class SavingCommandRepository(
        private val repo: CommandRepository,
        private val saver: CommandSaver,
        private val commandDataFactory: CommandDataFactory
): CommandRepository {
    override fun has(id: String): Boolean {
        return repo.has(id)
    }

    override fun get(id: String): CommandData? {
        return repo.get(id)
    }

    override fun set(data: CommandData) {
        this.repo.set(data)

        save()
    }

    override fun getAll(): List<CommandData> {
        return repo.getAll()
    }

    override fun remove(id: String) {
        this.repo.remove(id)

        save()
    }

    private fun save() {
        val commandsData = getAll()
        val commandsInfo = commandsData.map { commandDataFactory.makeInfo(it) }

        saver.save(commandsInfo)
    }
}