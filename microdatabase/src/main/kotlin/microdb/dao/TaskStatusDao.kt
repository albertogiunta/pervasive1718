package microdb.dao

import Params.TaskStatus.ID
import Params.TaskStatus.NAME
import Params.TaskStatus.TABLE_NAME
import microdb.model.TaskStatus
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface TaskStatusDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME) VALUES (:$NAME)")
    fun insertNewTaskStatus(@Bind(NAME) name: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllTaskStatuss(): List<TaskStatus>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectTaskStatusById(@Bind(ID) id: Int): List<TaskStatus>


}