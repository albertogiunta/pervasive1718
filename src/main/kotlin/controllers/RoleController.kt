package controllers

import dao.RoleDao
import model.Role
import spark.kotlin.get
import spark.kotlin.post
import utils.JdbiConfiguration
import utils.Params
import utils.okCreated
import utils.toJson
import java.sql.SQLException

object RoleController : Controller {

    override fun initRoutes() {

        /**
         * Inserts a new role inside of the Role table
         */
        post("/role", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.useExtension<RoleDao, SQLException>(RoleDao::class.java)
            { it.insertNewRole(request.queryParams(Params.Role.NAME)) }
            response.okCreated()
        }

        /**
         * Retrieves all the roles
         */
        get("/role", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Role>, RoleDao, SQLException>(RoleDao::class.java)
            { it.selectAllRoles() }
                .toJson()
        }

        /**
         * Retrieves a specific role based on the passed role ID
         */
        get("/role/:id", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Role>, RoleDao, SQLException>(RoleDao::class.java)
            { it.selectRoleById(request.params(Params.Role.ID).toInt()) }
                .toJson()
        }
    }
}