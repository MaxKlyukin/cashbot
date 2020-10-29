package me.maxklyukin.cashbot.command

object CommandTriggerFactory {

    fun make(triggers: List<Trigger>): CommandTrigger {
        guardEmpty(triggers)

        val groups = getGroups(triggers)
        val pattern = TriggerPatternBuilder(triggers).build()

        return CommandTrigger(
                pattern,
                triggers.size,
                groups
        )
    }

    private fun guardEmpty(triggers: List<Trigger>) {
        if (triggers.isEmpty()) {
            throw CommandTriggerException("No triggers")
        }
    }

    private fun getGroups(triggers: List<Trigger>): List<String> {
        val firstGroups = triggers.first().groupsNames

        val otherGroups = triggers.drop(1)
                .map { it.groupsNames }

        if (otherGroups.any{it.size != firstGroups.size || it != firstGroups}) {
            throw CommandTriggerException("Different sets of groups in triggers")
        }

        return firstGroups
    }
}

data class CommandTrigger(
        val pattern: Regex,
        val triggers: Int = 0,
        val groups: List<String> = listOf()
)

class CommandTriggerException(override val message: String): RuntimeException()