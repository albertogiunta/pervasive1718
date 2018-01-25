import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface RoleDao {

    @SqlUpdate("INSERT INTO role(name) VALUES (:name)")
    fun insertNewRole(@Bind("name") name: String)

    @SqlQuery("SELECT * FROM role WHERE id = (:id)")
    fun selectRoleById(@Bind("id") id: Int): List<Role>

    @SqlQuery("SELECT * FROM role")
    fun selectAllRoles(): List<Role>

}

