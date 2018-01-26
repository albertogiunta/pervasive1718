package dao

import model.Role
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params

interface RoleDao {

    @SqlUpdate("INSERT INTO ${Params.Role.TABLE_NAME}(name) VALUES (:name)")
    fun insertNewRole(@Bind("name") name: String)

    @SqlQuery("SELECT * FROM ${Params.Role.TABLE_NAME}")
    fun selectAllRoles(): List<Role>

    @SqlQuery("SELECT * FROM ${Params.Role.TABLE_NAME} WHERE id = (:id)")
    fun selectRoleById(@Bind("id") id: Int): List<Role>

}

