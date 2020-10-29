package me.maxklyukin.cashbot.command

class CommandMatcher {
    private val triggers = mutableMapOf<String, CommandTrigger>()

    fun add(id: String, commandTrigger: CommandTrigger) {
        triggers[id] = commandTrigger
    }

    fun match(input: String): CommandMatch? {
        for ((id, trigger) in triggers) {
            val match = trigger.pattern.find(input)
            if (match != null) {
                return CommandMatch(id, getMatched(trigger, match))
            }
        }

        return null
    }

    private fun getMatched(trigger: CommandTrigger, match: MatchResult): Map<String, String> {
        if (trigger.groups.isEmpty()) {
            return mapOf()
        }

        for (i in 0 until trigger.triggers) {
            val index = TriggerPatternBuilder.groupIndexFormat.format(i)
            val matched = getMatched(trigger, match, index)
            if (matched != null) {
                return matched
            }
        }

        return mapOf()
    }

    private fun getMatched(trigger: CommandTrigger, match: MatchResult, index: String): MutableMap<String, String>? {
        if (match.groups["${trigger.groups.first()}$index"] == null) {
            return null
        }

        val matched = mutableMapOf<String, String>()
        for (groupName in trigger.groups) {
            val matchedGroup = match.groups["$groupName$index"]
                    ?: return null

            matched[groupName] = matchedGroup.value
        }
        return matched
    }
}

data class CommandMatch(
        val commandId: String,
        val matched: Map<String, String>
)