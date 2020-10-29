package me.maxklyukin.cashbot.command

class TriggerPatternBuilder(private val triggers: List<Trigger>) {

    fun build(): Regex {
        return Regex(start + triggerPatterns.joinToString("$end|$start") + end, RegexOption.IGNORE_CASE)
    }

    private val triggerPatterns: List<String> get() {
        return triggers.withIndex().map { indexed ->
            val trigger = indexed.value
            val i = indexed.index

            triggerPattern(trigger, i)
        }
    }

    private fun triggerPattern(trigger: Trigger, i: Int) =
        trigger.parts.joinToString("") { part ->
            when (part) {
                is TriggerPart.Text -> {
                    part.value.replace(unsafeSymbols) { m -> "\\${m.value}" }
                }
                is TriggerPart.Group -> {
                    "(?<${part.name}${groupIndexFormat.format(i)}>${part.pattern})"
                }
            }
        }

    companion object {

        private const val punctuation = "[\\.,-\\/#!$%\\^&\\*;:{}=\\-_`~()?]+"
        private const val start = "(?:^|\\s|$punctuation)"
        private const val end = "(?:$|\\s|$punctuation)"

        private val unsafeSymbols = Regex("""[-/\\^$*+?.()|\[\]{}]""")
        const val groupIndexFormat = "%02d"
    }
}