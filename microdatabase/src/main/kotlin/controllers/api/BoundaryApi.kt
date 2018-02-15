@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.BoundaryDao
import model.Boundary
import okCreated
import spark.Request
import spark.Response
import utils.toJson
import java.sql.SQLException

object BoundaryApi {

    /**
     * Inserts a new status inside of the Boundary table
     */
    fun addBoundary(request: Request, response: Response): String {
        val boundary: Boundary = Klaxon().parse<Boundary>(request.body()) ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<BoundaryDao, SQLException>(BoundaryDao::class.java)
        {
            it.insertNewStatus(
                boundary.healthParameterId,
                boundary.activityId,
                boundary.upperBound,
                boundary.lowerBound,
                boundary.lightWarningOffset,
                boundary.status,
                boundary.itsGood,
                boundary.minAge,
                boundary.maxAge)
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the statuses
     */
    fun getAllBoundaries(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Boundary>, BoundaryDao, SQLException>(BoundaryDao::class.java)
        { it.selectAllStatuses() }
            .toJson()
    }

    /**
     * Retrieves a specific status based on the passed role ID
     */
    fun getBoundaryById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Boundary>, BoundaryDao, SQLException>(BoundaryDao::class.java)
        { it.selectStatusById(request.params(Params.Role.ID).toInt()) }
            .toJson()
    }

}