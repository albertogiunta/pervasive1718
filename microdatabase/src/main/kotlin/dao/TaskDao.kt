package dao

import Params.Task.ACTIVITY_ID
import Params.Task.END_TIME
import Params.Task.ID
import Params.Task.OPERATOR_ID
import Params.Task.SESSION_ID
import Params.Task.START_TIME
import Params.Task.STATUS_ID
import Params.Task.TABLE_NAME
import controllers.SessionController
import model.Task
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.Timestamp

interface TaskDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($OPERATOR_ID, $START_TIME, $END_TIME, $ACTIVITY_ID, $STATUS_ID, $SESSION_ID) VALUES (:$OPERATOR_ID, :$START_TIME, :$END_TIME, :$ACTIVITY_ID, :$STATUS_ID, :$SESSION_ID)")
    fun insertNewTask(@Bind(OPERATOR_ID) operatorId: Int,
                      @Bind(START_TIME) startTime: Timestamp,
                      @Bind(END_TIME) endTime: Timestamp,
                      @Bind(ACTIVITY_ID) activityId: Int,
                      @Bind(STATUS_ID) statusId: Int,
                      @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSession())

    @SqlUpdate("UPDATE $TABLE_NAME SET $STATUS_ID = (:$STATUS_ID) WHERE $SESSION_ID = (:$SESSION_ID) AND $ID = (:$ID)")
    fun updateTaskStatus(@Bind(ID) id: Int,
                         @Bind(STATUS_ID) statusId: Int,
                         @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSession())

    @SqlUpdate("DELETE FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID) AND $ID = (:$ID)")
    fun removeTask(@Bind(ID) operatorId: Int,
                   @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSession())

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID)")
    fun selectAllTasks(@Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSession()): List<Task>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID) AND $ID = (:$ID)")
    fun selectTaskById(@Bind(ID) id: Int,
                       @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSession()): List<Task>


}