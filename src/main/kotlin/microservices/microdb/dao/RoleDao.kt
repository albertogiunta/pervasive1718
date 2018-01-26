package microservices.microdb.dao

import microservices.microdb.model.Role
import microservices.microdb.utils.Params.Role.ID
import microservices.microdb.utils.Params.Role.NAME
import microservices.microdb.utils.Params.Role.TABLE_NAME
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface RoleDao {

    @SqlUpdate("INSERT INTO ${TABLE_NAME}($NAME) VALUES (:$NAME)")
    fun insertNewRole(@Bind(NAME) name: String)

    @SqlQuery("SELECT * FROM $TABLE_NAME")
    fun selectAllRoles(): List<Role>

    @SqlQuery("SELECT * FROM $TABLE_NAME WHERE $ID = (:$ID)")
    fun selectRoleById(@Bind(ID) id: Int): List<Role>

}

