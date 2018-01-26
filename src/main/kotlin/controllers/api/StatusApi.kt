package controllers.api

import dao.StatusDao
import model.Status
import spark.Request
import spark.Response
import utils.JdbiConfiguration
import utils.Params
import utils.okCreated
import utils.toJson
import java.sql.SQLException

object StatusApi {

    /**
     * Inserts a new status inside of the Status table
     */
    fun addStatus(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<StatusDao, SQLException>(StatusDao::class.java)
        {
            it.insertNewStatus(
                request.queryParams(Params.Status.HEALTH_PARAMETER_ID).toInt(),
                request.queryParams(Params.Status.ACTIVITY_ID).toInt(),
                request.queryParams(Params.Status.UPPERBOUND).toDouble(),
                request.queryParams(Params.Status.LOWERBOUND).toDouble())
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the statuses
     */
    fun getAllStatuses(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Status>, StatusDao, SQLException>(StatusDao::class.java)
        { it.selectAllStatuses() }
            .toJson()
    }

    /**
     * Retrieves a specific status based on the passed role ID
     */
    fun getStatusById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Status>, StatusDao, SQLException>(StatusDao::class.java)
        { it.selectStatusById(request.params(Params.Role.ID).toInt()) }
            .toJson()
    }

}