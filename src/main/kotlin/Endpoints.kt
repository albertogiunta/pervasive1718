import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import spark.Spark.port
import spark.kotlin.get
import spark.kotlin.post
import utils.toJson
import java.sql.SQLException


fun main(args: Array<String>) {
    port(8080)

    val jdbi = Jdbi
        .create("jdbc:postgresql://2.234.121.101:5432/tiopentone", "pervasive", "zeronegativo")
        .installPlugin(PostgresPlugin())
        .installPlugin(SqlObjectPlugin())
        .installPlugin(KotlinPlugin())
        .installPlugin(KotlinSqlObjectPlugin())

    /**
     * Inserts a new role inside of the Role table
     */
    post("/role", "application/json") {
        jdbi.useExtension<RoleDao, SQLException>(RoleDao::class.java) { it.insertNewRole(request.queryParams("name")) }
        response.status(201)
        ResponseMessage(201, "Ok").toJson()
    }

    /**
     * Retrieves all the roles
     */
    get("/role", "application/json") {
        jdbi.withExtension<List<Role>, RoleDao, SQLException>(RoleDao::class.java) { it.selectAllRoles() }.toJson()
    }

    /**
     * Retrieves a specific role based on the passed role ID
     */
    get("/role/:id", "application/json") {
        jdbi.withExtension<List<Role>, RoleDao, SQLException>(RoleDao::class.java) { it.selectRoleById(request.params("id").toInt()) }.toJson()
    }
}