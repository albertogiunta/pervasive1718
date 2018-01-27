package main.kotlin.microdb.dao

import Params.Activity.EXPECTED_EFFECT
import Params.Activity.ID
import Params.Activity.NAME
import Params.Activity.SIGNATURE
import Params.Activity.STATUS_ID
import Params.Activity.TABLE_NAME
import Params.Activity.TYPE_ID
import main.kotlin.microdb.model.Activity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

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

