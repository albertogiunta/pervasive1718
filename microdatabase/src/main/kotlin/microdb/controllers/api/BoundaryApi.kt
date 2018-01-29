package microdb.controllers.api

import JdbiConfiguration
import Params
import microdb.dao.BoundaryDao
import microdb.model.Boundary
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object BoundaryApi {

    /**
     * Inserts a new status inside of the Boundary table
     */
    fun addStatus(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<BoundaryDao, SQLException>(BoundaryDao::class.java)
        {
            it.insertNewStatus(
                request.queryParams(Params.Boundary.HEALTH_PARAMETER_ID).toInt(),
                request.queryParams(Params.Boundary.ACTIVITY_ID).toInt(),
                request.queryParams(Params.Boundary.UPPERBOUND).toDouble(),
                request.queryParams(Params.Boundary.LOWERBOUND).toDouble())
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the statuses
     */
    fun getAllStatuses(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Boundary>, BoundaryDao, SQLException>(BoundaryDao::class.java)
        { it.selectAllStatuses() }
                .toJson()
    }

    /**
     * Retrieves a specific status based on the passed role ID
     */
    fun getStatusById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Boundary>, BoundaryDao, SQLException>(BoundaryDao::class.java)
        { it.selectStatusById(request.params(Params.Role.ID).toInt()) }
                .toJson()
    }

}