package me.maxklyukin.cashbot.timer

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class Timer(
        private val name: String,
        private val initialDelayMs: Long,
        private val periodMs: Long,
        private val task: suspend () -> Unit
): CoroutineScope {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Volatile private var running: Boolean = false

    fun start() = launch {
        if (!running) {
            delay(initialDelayMs)
            running = true
            while(running) {
                logger.info("Running task $name")
                task()
                delay(periodMs)
            }
        }
    }

    fun stop() {
        this.running = false
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}