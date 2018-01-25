package controllers

import dao.LogDao
import model.Log
import spark.kotlin.get
import spark.kotlin.post
import utils.JdbiConfiguration
import utils.Params
import utils.okCreated
import utils.toJson
import java.sql.SQLException

object LogController : Controller {
    override fun initRoutes() {

        /**
         * Retrieves all log entries
         */
        get("/log", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
            { it.selectAllLogEntries() }
                    .toJson()
        }

        /**
         * Retrieves all log entries by health parameter id
         */
        get("/log/healthParameter/:id", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
            { it.selectAllLogEntriesByHealthParameterId(request.params(Params.Log.HEALTH_PARAMETER_ID).toInt()) }
                    .toJson()
        }

        /**
         * Retrieves all log entries by health parameter id above a given value
         */
        get("/log/healthParameter/:id/minThreshold/:value", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Log>, LogDao, SQLException>(LogDao::class.java)
            { it.selectAllLogEntriesByHealthParameterAboveThreshold(request.params(Params.Log.HEALTH_PARAMETER_ID).toInt(), request.params(Params.Log.VALUE).toInt()) }
                    .toJson()
        }

        /**
         * Insert a new entry on log
         */
        post("/log", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.useExtension<LogDao, SQLException>(LogDao::class.java)
            { it.insertNewLogEntry(request.queryParams(Params.Log.NAME), request.queryParams(Params.Log.LOG_TIME), request.queryParams(Params.Log.HEALTH_PARAMETER_ID).toInt(), request.queryParams(Params.Log.HEALTH_PARAMETER_VALUE).toDouble()) }
            response.okCreated()
        }
    }
}