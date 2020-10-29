package me.maxklyukin.cashbot.command

class CommandUpdater(
        private val commandRepos: CommandRepositories,
        private val commandDataFactory: CommandDataFactory,
        private val commandTriggerFactory: CommandTriggerFactory,
        private val commandMatcher: CommandMatcher
) {
    fun addSystemCommand(info: CommandInfo): CommandData {
        return update(info) { commandRepos.addSystem(it) }
    }

    fun addUserCommand(info: CommandInfo): CommandData {
        return update(info) { commandRepos.addUser(it) }
    }

    fun updateUserCommand(info: CommandInfo): CommandData {
        return update(info) { commandRepos.updateUser(it) }
    }

    private fun update(info: CommandInfo, updateOp: (data: CommandData) -> Unit): CommandData {
        val data = commandDataFactory.makeData(info)

        updateOp(data)

        val commandTrigger = commandTriggerFactory.make(data.triggers)
        commandMatcher.add(data.id, commandTrigger)

        return data
    }
}