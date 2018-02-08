package utils

import org.slf4j.LoggerFactory

object Logger {

    private val logger = LoggerFactory.getLogger(Logger::class.java)

    fun info(message: String) = logger.info(message)

    fun error(message: String) = logger.error(message)

    fun debug(message: String) = logger.debug(message)
}