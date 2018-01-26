package dao

import model.Activity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params

interface ActivityDao {

    @SqlUpdate("INSERT INTO ${Params.Activity.TABLE_NAME}(name, expectedEffect, typeId, signature, statusId) VALUES (:name, :expectedEffect, :typeId, :signature, :statusId)")
    fun insertNewActivity(@Bind("name") name: String,
                          @Bind("expectedEffect") expectedEffect: String,
                          @Bind("typeId") typeId: Int,
                          @Bind("signature") signature: String,
                          @Bind("statusId") statusId: Int)

    @SqlQuery("SELECT * FROM ${Params.Activity.TABLE_NAME}")
    fun selectAllActivities(): List<Activity>

    @SqlQuery("SELECT * FROM ${Params.Activity.TABLE_NAME} WHERE id = (:id)")
    fun selectActivityById(@Bind("id") id: Int): List<Activity>

}

