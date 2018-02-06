package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.RoleDao
import model.Role
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object RoleApi {

    /**
     * Inserts a new role inside of the Role table
     */
    fun addRole(request: Request, response: Response): String {
        val role: Role = Klaxon().parse<Role>(request.body()) ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<RoleDao, SQLException>(RoleDao::class.java)
        { it.insertNewRole(role.name) }
        return response.okCreated()
    }

    /**
     * Retrieves all the roles
     */
    fun getAllRoles(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Role>, RoleDao, SQLException>(RoleDao::class.java)
        { it.selectAllRoles() }
                .toJson()
    }

    /**
     * Retrieves a specific role based on the passed role ID
     */
    fun getRoleById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Role>, RoleDao, SQLException>(RoleDao::class.java)
        { it.selectRoleById(request.params(Params.Role.ID).toInt()) }
                .toJson()
    }
}