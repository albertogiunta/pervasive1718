package controllers.api

import dao.RoleDao
import model.Role
import spark.Request
import spark.Response
import utils.JdbiConfiguration
import utils.Params
import utils.okCreated
import utils.toJson
import java.sql.SQLException

object RoleApi {

    /**
     * Inserts a new role inside of the Role table
     */
    fun addRole(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<RoleDao, SQLException>(RoleDao::class.java)
        { it.insertNewRole(request.queryParams(Params.Role.NAME)) }
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