package dao

import Params.Log.HEALTH_PARAMETER_ID
import Params.Log.HEALTH_PARAMETER_VALUE
import Params.Log.LOG_TIME
import Params.Log.NAME
import Params.Log.SESSION_ID
import Params.Log.TABLE_NAME
import controllers.InstanceIdController
import model.Log
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface LogDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $LOG_TIME, $HEALTH_PARAMETER_ID, $HEALTH_PARAMETER_VALUE, $SESSION_ID) VALUES (:$NAME, :$LOG_TIME, :$HEALTH_PARAMETER_ID, :$HEALTH_PARAMETER_VALUE, :$SESSION_ID)")
    fun insertNewLogEntry(@Bind(NAME) name: String,
                          @Bind(LOG_TIME) logTime: java.sql.Timestamp,
                          @Bind(HEALTH_PARAMETER_ID) healthParameterId: Int,
                          @Bind(HEALTH_PARAMETER_VALUE) healthParameterValue: Double,
                          @Bind(SESSION_ID) sessionId: Int = InstanceIdController.getCurrentInstanceID())

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID)")
    fun selectAllLogEntries(@Bind(SESSION_ID) sessionId: Int = InstanceIdController.getCurrentInstanceID()): List<Log>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID) AND $HEALTH_PARAMETER_ID = (:$HEALTH_PARAMETER_ID)")
    fun selectAllLogEntriesByHealthParameterId(@Bind(HEALTH_PARAMETER_ID) healthParameterId: Int,
                                               @Bind(SESSION_ID) sessionId: Int = InstanceIdController.getCurrentInstanceID()): List<Log>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID) AND $HEALTH_PARAMETER_ID = (:$HEALTH_PARAMETER_ID) AND $HEALTH_PARAMETER_VALUE > (:$HEALTH_PARAMETER_VALUE)")
    fun selectAllLogEntriesByHealthParameterAboveThreshold(@Bind(HEALTH_PARAMETER_ID) healthParameterId: Int,
                                                           @Bind(HEALTH_PARAMETER_VALUE) value: Int,
                                                           @Bind(SESSION_ID) sessionId: Int = InstanceIdController.getCurrentInstanceID()): List<Log>

//    @SqlUpdate("DELETE FROM $TABLE_NAME")
//    fun deleteAllLogs()

}