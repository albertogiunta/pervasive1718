package utils

import org.slf4j.LoggerFactory
import org.slf4j.Marker

/**
 * A Static Decorated Logger with @org.slf4j.Logger
 *
 * @author XanderC
 *
 */
object Logger : org.slf4j.Logger {

    private var logger = LoggerFactory.getLogger(Logger::class.java)

    fun setLogger(name : String) : org.slf4j.Logger {
        logger = LoggerFactory.getLogger(name)
        return this
    }

    fun setLogger(clazz : Class<out Any>) : org.slf4j.Logger {
        logger = LoggerFactory.getLogger(clazz)
        return this
    }

    override fun info(msg: String?) = logger.info(msg)

    override fun error(msg: String?) = logger.error(msg)

    override fun warn(msg: String?) = logger.warn(msg)

    override fun debug(msg: String?) = logger.debug(msg)

    override fun info(format: String?, arg: Any?) = logger.info(format, arg)

    override fun info(format: String?, arg1: Any?, arg2: Any?) = logger.info(format, arg1, arg2)

    override fun info(format: String?, vararg arguments: Any?) = logger.info(format, arguments)

    override fun info(msg: String?, t: Throwable?) = logger.info(msg, t)

    override fun info(marker: Marker?, msg: String?) = logger.info(marker, msg)

    override fun info(marker: Marker?, format: String?, arg: Any?) = logger.info(marker, format, arg)

    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.info(marker, format, arg1, arg2)

    override fun info(marker: Marker?, format: String?, vararg arguments: Any?) = logger.info(marker, format, arguments)

    override fun info(marker: Marker?, msg: String?, t: Throwable?) = logger.info(marker, msg, t)

    override fun error(format: String?, arg: Any?) = logger.error(format, arg)

    override fun error(format: String?, arg1: Any?, arg2: Any?) = logger.error(format, arg1, arg2)

    override fun error(format: String?, vararg arguments: Any?) = logger.error(format, arguments)

    override fun error(msg: String?, t: Throwable?) = logger.error(msg, t)

    override fun error(marker: Marker?, msg: String?) = logger.error(marker, msg)

    override fun error(marker: Marker?, format: String?, arg: Any?) = logger.error(marker, format, arg)

    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.error(marker, format, arg1, arg2)

    override fun error(marker: Marker?, format: String?, vararg arguments: Any?) = logger.error(marker, format, arguments)

    override fun error(marker: Marker?, msg: String?, t: Throwable?) = logger.error(marker, msg, t)

    override fun warn(format: String?, arg: Any?) = logger.warn(format, arg)

    override fun warn(format: String?, arg1: Any?, arg2: Any?) = logger.warn(format, arg1, arg2)

    override fun warn(format: String?, vararg arguments: Any?) = logger.warn(format, arguments)

    override fun warn(msg: String?, t: Throwable?) = logger.warn(msg, t)

    override fun warn(marker: Marker?, msg: String?) = logger.warn(marker, msg)

    override fun warn(marker: Marker?, format: String?, arg: Any?) = logger.warn(marker, format, arg)

    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.warn(marker, format, arg1, arg2)

    override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) = logger.warn(marker, format, arguments)

    override fun warn(marker: Marker?, msg: String?, t: Throwable?) = logger.warn(marker, msg, t)

    override fun debug(format: String?, arg: Any?) = logger.debug(format, arg)

    override fun debug(format: String?, arg1: Any?, arg2: Any?) = logger.debug(format, arg1, arg2)

    override fun debug(format: String?, vararg arguments: Any?) = logger.debug(format, arguments)

    override fun debug(msg: String?, t: Throwable?) = logger.debug(msg, t)

    override fun debug(marker: Marker?, msg: String?) = logger.debug(marker, msg)

    override fun debug(marker: Marker?, format: String?, arg: Any?) = logger.debug(marker, format, arg)

    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.debug(marker, format, arg1, arg2)

    override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) = logger.debug(marker, format, arguments)

    override fun debug(marker: Marker?, msg: String?, t: Throwable?) = logger.debug(marker, msg, t)

    override fun getName(): String = logger.name

    override fun isErrorEnabled(): Boolean = logger.isErrorEnabled

    override fun isErrorEnabled(marker: Marker?): Boolean = logger.isErrorEnabled(marker)

    override fun isDebugEnabled(): Boolean = logger.isDebugEnabled

    override fun isDebugEnabled(marker: Marker?): Boolean = logger.isDebugEnabled(marker)

    override fun isInfoEnabled(): Boolean = logger.isInfoEnabled

    override fun isInfoEnabled(marker: Marker?): Boolean = logger.isInfoEnabled(marker)

    override fun trace(msg: String?) = logger.trace(msg)

    override fun trace(format: String?, arg: Any?) = logger.trace(format, arg)

    override fun trace(format: String?, arg1: Any?, arg2: Any?) = logger.trace(format, arg1, arg2)

    override fun trace(format: String?, vararg arguments: Any?) = logger.trace(format, arguments)

    override fun trace(msg: String?, t: Throwable?) = logger.trace(msg, t)

    override fun trace(marker: Marker?, msg: String?) = logger.trace(marker, msg)

    override fun trace(marker: Marker?, format: String?, arg: Any?) = logger.trace(marker, format, arg)

    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.trace(marker, format, arg1, arg2)

    override fun trace(marker: Marker?, format: String?, vararg argArray: Any?) = logger.trace(marker, format, argArray)

    override fun trace(marker: Marker?, msg: String?, t: Throwable?) = logger.trace(marker, msg, t)

    override fun isWarnEnabled(): Boolean = logger.isWarnEnabled

    override fun isWarnEnabled(marker: Marker?): Boolean = logger.isWarnEnabled(marker)

    override fun isTraceEnabled(): Boolean = isTraceEnabled

    override fun isTraceEnabled(marker: Marker?): Boolean = logger.isTraceEnabled(marker)
}