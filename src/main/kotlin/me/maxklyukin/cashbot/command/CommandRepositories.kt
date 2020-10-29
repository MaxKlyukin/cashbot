package me.maxklyukin.cashbot.command

class CommandRepositories(
        private val system: CommandRepository,
        private val user: CommandRepository
) {

    fun has(id: String) = system.has(id) || user.has(id)

    fun isSystem(id: String) = system.has(id)

    fun get(id: String): CommandData? {
        return system.get(id)
                ?: user.get(id)
    }

    fun updateUser(data: CommandData) {
        guardSystem(data.id)

        user.set(data)
    }

    fun addSystem(data: CommandData) {
        guardExists(data.id)

        system.set(data)
    }

    fun addUser(data: CommandData) {
        guardExists(data.id)

        user.set(data)
    }

    fun remove(id: String) {
        guardSystem(id)

        user.remove(id)
    }

    fun getAll(): List<CommandData> {
        return system.getAll() + user.getAll()
    }

    private fun guardExists(id: String) {
        if (has(id)) {
            throw CommandRepositoryException("Command $id already exists")
        }
    }

    private fun guardSystem(id: String) {
        if (system.has(id)) {
            throw CommandRepositoryException("Command $id is system")
        }
    }
}

class CommandRepositoryException(override val message: String) : RuntimeException()