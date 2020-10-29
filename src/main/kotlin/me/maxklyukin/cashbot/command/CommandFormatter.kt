package me.maxklyukin.cashbot.command

object CommandFormatter {

    fun format(data: CommandData): String {
        val triggerString = data.triggers.joinToString("|") { it.toString() }
        val commandString = data.command.toString()

//            """ - *$commandId*
//              |    $triggerString
//              |    $commandString""".trimMargin()
        return """*${data.id}* `$triggerString`
                      |>`$commandString`""".trimMargin()
    }
}