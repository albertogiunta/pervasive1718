package dao

import Params.Task.ACTIVITY_ID
import Params.Task.END_TIME
import Params.Task.ID
import Params.Task.OPERATOR_CF
import Params.Task.SESSION_ID
import Params.Task.START_TIME
import Params.Task.STATUS_ID
import Params.Task.TABLE_NAME
import Params.Task.TASK_NAME
import controllers.SessionController
import model.Task
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.Timestamp

interface TaskDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($TASK_NAME, $SESSION_ID, $OPERATOR_CF, $START_TIME, $ACTIVITY_ID, $STATUS_ID) VALUES (:$TASK_NAME, :$SESSION_ID, :$OPERATOR_CF, :$START_TIME, :$ACTIVITY_ID, :$STATUS_ID)")
    fun insertNewTask(@Bind(TASK_NAME) name: String,
                      @Bind(SESSION_ID) sessionId: Int = SessionController.getCurrentSessionId(),
                      @Bind(OPERATOR_CF) operatorCF: String,
                      @Bind(START_TIME) startTime: Timestamp,
//                      @Bind(END_TIME) endTime: Timestamp,
                      @Bind(ACTIVITY_ID) activityId: Int,
                      @Bind(STATUS_ID) statusId: Int)

    @SqlUpdate("UPDATE $TABLE_NAME SET $STATUS_ID = (:$STATUS_ID) WHERE $ID = (:$ID)")
    fun updateTaskStatus(@Bind(ID) id: Int,
                         @Bind(STATUS_ID) statusId: Int)

    @SqlUpdate("UPDATE $TABLE_NAME SET $STATUS_ID = (:$STATUS_ID) WHERE $TASK_NAME = (:$TASK_NAME)")
    fun updateTaskStatusByName(@Bind(TASK_NAME) name: String,
                               @Bind(STATUS_ID) statusId: Int)

    @SqlUpdate("UPDATE $TABLE_NAME SET $END_TIME = (:$END_TIME) WHERE $TASK_NAME = (:$TASK_NAME)")
    fun updateTaskEndTime(@Bind(TASK_NAME) name: String,
                          @Bind(END_TIME) endTime: Timestamp)

    @SqlUpdate("UPDATE $TABLE_NAME SET $END_TIME = (:$END_TIME) WHERE $TASK_NAME = (:$TASK_NAME)")
    fun updateTaskEndTimeByName(@Bind(TASK_NAME) id: String,
                                @Bind(END_TIME) endTime: Timestamp)

    @SqlUpdate("DELETE FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun removeTask(@Bind(ID) taskId: Int)

    @SqlUpdate("DELETE FROM $TABLE_NAME WHERE $TASK_NAME = (:$TASK_NAME)")
    fun removeTaskByName(@Bind(TASK_NAME) name: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllTasks(): List<Task>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $SESSION_ID = (:$SESSION_ID)")
    fun selectAllTasks(@Bind(SESSION_ID) sessionId: Int): List<Task>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectTaskById(@Bind(ID) id: Int): List<Task>
}