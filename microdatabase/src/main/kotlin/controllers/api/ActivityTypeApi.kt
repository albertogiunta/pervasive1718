package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.ActivityTypeDao
import model.ActivityType
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object ActivityTypeApi {

    /**
     * Add a new activity type
     */
    fun addActivityType(request: Request, response: Response): String {
        val activityType: ActivityType = Klaxon().parse<ActivityType>(request.body()) ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<ActivityTypeDao, SQLException>(ActivityTypeDao::class.java)
        { it.insertNewActivityType(request.queryParams(activityType.name)) }
        return response.okCreated()
    }

    /**
     * Retrieves all the activities types
     */
    fun getAllActivityTypes(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<ActivityType>, ActivityTypeDao, SQLException>(ActivityTypeDao::class.java)
        { it.selectAllActivityTypes() }
                .toJson()
    }

    /**
     * Retrieves a specified activity by id
     */
    fun getActivityTypeById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<ActivityType>, ActivityTypeDao, SQLException>(ActivityTypeDao::class.java)
        { it.selectActivityTypeById(request.params(Params.ActivityType.ID).toInt()) }
                .toJson()
    }
}