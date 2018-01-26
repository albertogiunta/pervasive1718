package dao

import model.Activity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params.Activity.EXPECTED_EFFECT
import utils.Params.Activity.ID
import utils.Params.Activity.NAME
import utils.Params.Activity.SIGNATURE
import utils.Params.Activity.STATUS_ID
import utils.Params.Activity.TABLE_NAME
import utils.Params.Activity.TYPE_ID

interface ActivityDao {

    @SqlUpdate("INSERT INTO $TABLE_NAME($NAME, $EXPECTED_EFFECT, $TYPE_ID, $SIGNATURE, $STATUS_ID) VALUES (:$NAME, :$EXPECTED_EFFECT, :$TYPE_ID, :$SIGNATURE, :$STATUS_ID)")
    fun insertNewActivity(@Bind(NAME) name: String,
                          @Bind(EXPECTED_EFFECT) expectedEffect: String,
                          @Bind(TYPE_ID) typeId: Int,
                          @Bind(SIGNATURE) signature: String,
                          @Bind(STATUS_ID) statusId: Int)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllActivities(): List<Activity>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectActivityById(@Bind(ID) id: Int): List<Activity>

}

