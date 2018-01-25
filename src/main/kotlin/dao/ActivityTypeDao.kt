package dao

import model.ActivityType
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface ActivityTypeDao {

    @SqlQuery("SELECT * FROM activityType")
    fun selectAllActivityTypes(): List<ActivityType>

    @SqlQuery("SELECT * FROM activityType WHERE id = (:id)")
    fun selectActivityTypeById(@Bind("id") id: Int): List<ActivityType>

    @SqlUpdate("INSERT INTO activityType(name) VALUES (:name)")
    fun insertNewActivityType(@Bind("name") name: String)

}