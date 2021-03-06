package dao

import Params.Activity.ACRONYM
import Params.Activity.ACTIVITY_TYPE_ID
import Params.Activity.HEALTH_PARAMETER_IDS
import Params.Activity.ID
import Params.Activity.NAME
import Params.Activity.TABLE_NAME
import model.Activity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface ActivityDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $ACTIVITY_TYPE_ID, $ACRONYM, $HEALTH_PARAMETER_IDS) VALUES (:$NAME, :$ACTIVITY_TYPE_ID, :$ACRONYM, :$HEALTH_PARAMETER_IDS)")
    fun insertNewActivity(@Bind(NAME) name: String,
                          @Bind(ACTIVITY_TYPE_ID) typeId: Int,
                          @Bind(ACRONYM) signature: String,
                          @Bind(HEALTH_PARAMETER_IDS) healthParameterIds: List<Int>)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllActivities(): List<Activity>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectActivityById(@Bind(ID) id: Int): List<Activity>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ACTIVITY_TYPE_ID = (:$ACTIVITY_TYPE_ID)")
    fun selectActivitiesByActivityTypeId(@Bind(ACTIVITY_TYPE_ID) id: Int): List<Activity>

}

