package dao

import model.HealthParameter
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params

interface HealthParameterDao {

    @SqlUpdate("INSERT INTO ${Params.HealthParameter.TABLE_NAME}(name, signature) VALUES (:name, :signature)")
    fun insertNewHealthParameter(@Bind("name") name: String, @Bind("signature") signature: String)

    @SqlQuery("SELECT * FROM ${Params.HealthParameter.TABLE_NAME}")
    fun selectAllHealthParameters(): List<HealthParameter>

    @SqlQuery("SELECT * FROM ${Params.HealthParameter.TABLE_NAME} WHERE id = (:id)")
    fun selectHealthParameterById(@Bind("id") id: Int): List<HealthParameter>

}

