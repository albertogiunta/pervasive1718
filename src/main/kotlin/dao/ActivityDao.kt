package dao

import model.Activity
import org.jdbi.v3.sqlobject.statement.SqlQuery

interface ActivityDao {

    @SqlQuery("SELECT * FROM activity")
    fun selectAllActivities(): List<Activity>

}

