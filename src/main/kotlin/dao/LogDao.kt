package dao

import model.Log
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params

interface LogDao {

    @SqlUpdate("INSERT INTO ${Params.Log.TABLE_NAME}(name, logtime, healthparameterid, healthparametervalue) VALUES (:name, :logtime, :healthparameterid, :healthparametervalue)")
    fun insertNewLogEntry(@Bind("name") name: String,
                          @Bind("logtime") logtime: String,
                          @Bind("healthparameterid") healthparameterid: Int,
                          @Bind("healthparametervalue") healthparametervalue: Double)

    @SqlQuery("SELECT * FROM ${Params.Log.TABLE_NAME}")
    fun selectAllLogEntries(): List<Log>

    @SqlQuery("SELECT * FROM ${Params.Log.TABLE_NAME} as l WHERE l.healthparameterid = (:healthparameterid)")
    fun selectAllLogEntriesByHealthParameterId(@Bind("healthparameterid") healthparameterid: Int): List<Log>

    @SqlQuery("SELECT * FROM ${Params.Log.TABLE_NAME} as l WHERE l.healthparameterid = (:healthparameterid) AND l.healthparametervalue > (:value)")
    fun selectAllLogEntriesByHealthParameterAboveThreshold(@Bind("healthparameterid") healthparameterid: Int,
                                                           @Bind("value") value: Int): List<Log>

}