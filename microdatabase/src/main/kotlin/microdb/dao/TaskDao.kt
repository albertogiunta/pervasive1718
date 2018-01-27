package main.kotlin.microdb.dao

import Params.Task.ACTIVITY_ID
import Params.Task.END_TIME
import Params.Task.ID
import Params.Task.OPERATOR_ID
import Params.Task.PROGRESS
import Params.Task.START_TIME
import Params.Task.TABLE_NAME
import main.kotlin.microdb.model.Task
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface TaskDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($OPERATOR_ID, $START_TIME, $END_TIME, $ACTIVITY_ID, $PROGRESS) VALUES (:$OPERATOR_ID, :$START_TIME, :$END_TIME, :$ACTIVITY_ID, :$PROGRESS)")
    fun insertNewTask(@Bind(OPERATOR_ID) operatorId: Int,
                      @Bind(START_TIME) startTime: String,
                      @Bind(END_TIME) endTime: String,
                      @Bind(ACTIVITY_ID) activityId: Int,
                      @Bind(PROGRESS) progress: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllTasks(): List<Task>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectTaskById(@Bind(ID) id: Int): List<Task>


}