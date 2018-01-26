package microservices.microdb.controllers.api

import microservices.microdb.dao.LogDao
import microservices.microdb.model.Log
import microservices.microdb.utils.JdbiConfiguration
import microservices.microdb.utils.Params
import microservices.microdb.utils.okCreated
import microservices.microdb.utils.toJson
import spark.Request
import spark.Response
import java.sql.SQLException

object LogApi {

    /**
     * Insert a new entry in the Log table
     */
    fun addLogEntry(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<LogDao, SQLException>(LogDao::class.java)
        {
            it.insertNewLogEntry(
                request.queryParams(Params.Log.NAME),
                request.queryParams(Params.Log.LOG_TIME),
                request.queryParams(Params.Log.HEALTH_PARAMETER_ID).toInt(),
                request.queryParams(Params.Log.HEALTH_PARAMETER_VALUE).toDouble())
        }
        return response.okCreated()
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
        { it.selectAllLogEntriesByHealthParameterAboveThreshold(request.params(Params.Log.HEALTH_PARAMETER_ID).toInt(), request.params(Params.Log.VALUE).toInt()) }
            .toJson()
    }

}

