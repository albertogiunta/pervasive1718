@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import LifeParameters
import Params
import badRequest
import com.beust.klaxon.Klaxon
import controllers.SessionController
import dao.LogDao
import model.Log
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object LogApi {

    /**
     * Insert a new entry in the Log table
     */
    fun addLogEntry(request: Request, response: Response): String {
        val log: Log = Klaxon().parse<Log>(request.body()) ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<LogDao, SQLException>(LogDao::class.java)
        {
            it.insertNewLogEntry(
                log.name,
                log.logTime,
                log.healthParameterId,
                log.healthParameterValue,
                SessionController.getCurrentSession())
        }
        return response.okCreated()
    }

    /**
     * Insert a new entry in the Log table
     */
    fun addLogEntry(param: LifeParameters, value: Double) {
        val log = Log(name = LifeParameters.HEART_RATE.longName, healthParameterId = LifeParameters.HEART_RATE.id, healthParameterValue = value)
        JdbiConfiguration.INSTANCE.jdbi.useExtension<LogDao, SQLException>(LogDao::class.java)
        {
            it.insertNewLogEntry(
                log.name,
                log.logTime,
                log.healthParameterId,
                log.healthParameterValue,
                SessionController.getCurrentSession())
        }
    }

    /**
     * Retrieves all the log entries
     */
    fun getAllLogEntries(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
        { it.selectAllLogEntries() }
            .toJson()
    }

    /**
     * Retrieves all log entries by health parameter id
     */
    fun getAllLogEntriesByHealthParameterId(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
        { it.selectAllLogEntriesByHealthParameterId(request.params(Params.Log.HEALTH_PARAMETER_ID).toInt()) }
            .toJson()
    }

    /**
     * Retrieves all log entries by health parameter id above a given value
     */
    fun getAllLogEntriesByHealthParameterIdAboveValue(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
        { it.selectAllLogEntriesByHealthParameterAboveThreshold(request.params(Params.Log.HEALTH_PARAMETER_ID).toInt(), request.params(Params.Log.HEALTH_PARAMETER_VALUE).toInt()) }
            .toJson()
    }

}

