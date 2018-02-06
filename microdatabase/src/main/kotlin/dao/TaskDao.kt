package dao

import Params.Task.ACTIVITY_ID
import Params.Task.END_TIME
import Params.Task.ID
import Params.Task.OPERATOR_ID
import Params.Task.START_TIME
import Params.Task.TABLE_NAME
import Params.Task.TASK_STATUS_ID
import model.Task
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.Timestamp

interface TaskDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($OPERATOR_ID, $START_TIME, $END_TIME, $ACTIVITY_ID, $TASK_STATUS_ID) VALUES (:$OPERATOR_ID, :$START_TIME, :$END_TIME, :$ACTIVITY_ID, :$TASK_STATUS_ID)")
    fun insertNewTask(@Bind(OPERATOR_ID) operatorId: Int,
                      @Bind(START_TIME) startTime: Timestamp,
                      @Bind(END_TIME) endTime: Timestamp,
                      @Bind(ACTIVITY_ID) activityId: Int,
                      @Bind(TASK_STATUS_ID) progress: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllTasks(): List<Task>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectTaskById(@Bind(ID) id: Int): List<Task>


}