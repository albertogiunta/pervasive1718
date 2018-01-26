package dao

import model.Role
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import utils.Params.Role.ID
import utils.Params.Role.NAME
import utils.Params.Role.TABLE_NAME

interface RoleDao {

    @SqlUpdate("INSERT INTO ${TABLE_NAME}($NAME) VALUES (:$NAME)")
    fun insertNewRole(@Bind(NAME) name: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllRoles(): List<Role>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectRoleById(@Bind(ID) id: Int): List<Role>

}

