package dao

import Params.Task.ACTIVITY_ID
import Params.Task.END_TIME
import Params.Task.ID
import Params.Task.OPERATOR_CF
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

    @SqlUpdate("INSERT INTO $TABLE_NAME($ID, $SESSION_ID, $OPERATOR_CF, $START_TIME, $END_TIME, $ACTIVITY_ID, $STATUS_ID) VALUES (:$ID, :$SESSION_ID, :$OPERATOR_CF, :$START_TIME, :$END_TIME, :$ACTIVITY_ID, :$STATUS_ID)")
    fun insertNewTask(@Bind(ID) id: Int,
                      @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSessionId(),
                      @Bind(OPERATOR_CF) operatorCF: String,
                      @Bind(START_TIME) startTime: Timestamp,
                      @Bind(END_TIME) endTime: Timestamp,
                      @Bind(ACTIVITY_ID) activityId: Int,
                      @Bind(STATUS_ID) statusId: Int)

    @SqlUpdate("UPDATE $TABLE_NAME SET $STATUS_ID = (:$STATUS_ID) WHERE $SESSION_ID = (:$SESSION_ID) AND $ID = (:$ID)")
    fun updateTaskStatus(@Bind(ID) id: Int,
                         @Bind(STATUS_ID) statusId: Int,
                         @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSessionId())

    @SqlUpdate("DELETE FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID) AND $ID = (:$ID)")
    fun removeTask(@Bind(ID) taskId: Int,
                   @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSessionId())

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID)")
    fun selectAllTasks(@Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSessionId()): List<Task>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID) AND $ID = (:$ID)")
    fun selectTaskById(@Bind(ID) id: Int,
                       @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSessionId()): List<Task>
}