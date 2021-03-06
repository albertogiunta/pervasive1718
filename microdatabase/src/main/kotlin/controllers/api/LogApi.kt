@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.LogDao
import model.LifeParameters
import model.Log
import okCreated
import spark.Request
import spark.Response
import utils.KlaxonDate
import utils.dateConverter
import utils.toJson
import java.sql.SQLException

object LogApi {

    /**
     * Insert a new entry in the Log table
     */
    fun addLogEntry(request: Request, response: Response): String {
        val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
        val log: Log = klaxon.parse<Log>(
                request.body()) ?: return response.badRequest("Expected Log json serialized object," +
                "found: ${request.body()}")
        JdbiConfiguration.INSTANCE.jdbi.useExtension<LogDao, SQLException>(LogDao::class.java)
        {
            it.insertNewLogEntry(
                log.name,
                log.logTime,
                log.healthParameterId,
                log.healthParameterValue)
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the log entries
     */
    fun getAllLogEntriesBySessionId(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
        { it.selectAllLogEntriesBySessionId(request.params(Params.Log.SESSION_ID).toInt()) }
            .toJson()
    }

    /**
     * Retrieves all log entries by health parameter id
     */
    fun getAllLogEntriesByHealthParameterId(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
        { it.selectAllLogEntriesByHealthParameterId(request.params(Params.Log.HEALTH_PARAMETER_ID).toInt(),
                request.params(Params.Log.SESSION_ID).toInt()) }
            .toJson()
    }

    /**
     * Retrieves all log entries by health parameter id above a given value
     */
    fun getAllLogEntriesByHealthParameterIdAboveValue(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
        { it.selectAllLogEntriesByHealthParameterAboveThreshold(request.params(Params.Log.HEALTH_PARAMETER_ID).toInt(),
                request.params(Params.Log.HEALTH_PARAMETER_VALUE).toInt(),
                request.params(Params.Log.SESSION_ID).toInt()) }
            .toJson()
    }
}

