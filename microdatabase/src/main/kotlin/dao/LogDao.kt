package dao

import Params.Log.HEALTH_PARAMETER_ID
import Params.Log.HEALTH_PARAMETER_VALUE
import Params.Log.LOG_TIME
import Params.Log.NAME
import Params.Log.TABLE_NAME
import Params.Log.VALUE
import model.Log
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface LogDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $LOG_TIME, $HEALTH_PARAMETER_ID, $HEALTH_PARAMETER_VALUE) VALUES (:$NAME, :$LOG_TIME, :$HEALTH_PARAMETER_ID, :$HEALTH_PARAMETER_VALUE)")
    fun insertNewLogEntry(@Bind(NAME) name: String,
                          @Bind(LOG_TIME) logTime: java.sql.Timestamp,
                          @Bind(HEALTH_PARAMETER_ID) healthParameterId: Int,
                          @Bind(HEALTH_PARAMETER_VALUE) healthParameterValue: Double)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllLogEntries(): List<Log>

    @SqlQuery("SELECT * FROM $TABLE_NAME as l WHERE l.$HEALTH_PARAMETER_ID = (:$HEALTH_PARAMETER_ID)")
    fun selectAllLogEntriesByHealthParameterId(@Bind(HEALTH_PARAMETER_ID) healthParameterId: Int): List<Log>

    @SqlQuery("SELECT * FROM $TABLE_NAME as l WHERE l.$HEALTH_PARAMETER_ID = (:$HEALTH_PARAMETER_ID) AND l.$HEALTH_PARAMETER_VALUE > (:$VALUE)")
    fun selectAllLogEntriesByHealthParameterAboveThreshold(@Bind(HEALTH_PARAMETER_ID) healthParameterId: Int,
                                                           @Bind(VALUE) value: Int): List<Log>

}