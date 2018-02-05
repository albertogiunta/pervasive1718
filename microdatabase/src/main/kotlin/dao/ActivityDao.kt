package dao

import Params.Activity.ID
import Params.Activity.NAME
import Params.Activity.ACRONYM
import Params.Activity.STATUS_ID
import Params.Activity.TABLE_NAME
import Params.Activity.ACTIVITY_TYPE_ID
import model.Activity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface ActivityDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $ACTIVITY_TYPE_ID, $ACRONYM, $STATUS_ID) VALUES (:$NAME, :$ACTIVITY_TYPE_ID, :$ACRONYM, :$STATUS_ID)")
    fun insertNewActivity(@Bind(NAME) name: String,
                          @Bind(ACTIVITY_TYPE_ID) typeId: Int,
                          @Bind(ACRONYM) signature: String,
                          @Bind(STATUS_ID) statusId: Int)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllActivities(): List<Activity>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectActivityById(@Bind(ID) id: Int): List<Activity>

}

