package dao

import model.ActivityType
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params

interface ActivityTypeDao {

    @SqlUpdate("INSERT INTO ${Params.ActivityType.TABLE_NAME}(name) VALUES (:name)")
    fun insertNewActivityType(@Bind("name") name: String)

    @SqlQuery("SELECT * FROM ${Params.ActivityType.TABLE_NAME}")
    fun selectAllActivityTypes(): List<ActivityType>

    @SqlQuery("SELECT * FROM ${Params.ActivityType.TABLE_NAME} WHERE id = (:id)")
    fun selectActivityTypeById(@Bind("id") id: Int): List<ActivityType>

}