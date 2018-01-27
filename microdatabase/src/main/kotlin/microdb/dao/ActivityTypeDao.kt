package main.kotlin.microdb.dao

import Params.ActivityType.ID
import Params.ActivityType.NAME
import Params.ActivityType.TABLE_NAME
import main.kotlin.microdb.model.ActivityType
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface ActivityTypeDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME) VALUES (:$NAME)")
    fun insertNewActivityType(@Bind(NAME) name: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllActivityTypes(): List<ActivityType>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectActivityTypeById(@Bind(ID) id: Int): List<ActivityType>

}