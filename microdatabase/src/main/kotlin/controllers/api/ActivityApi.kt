@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.ActivityDao
import model.Activity
import okCreated
import spark.Request
import spark.Response
import utils.toJson
import java.sql.SQLException

object ActivityApi {

    /**
     * Retrieves all the activities
     */
    fun addActivity(request: Request, response: Response): String {
        val activity: Activity = Klaxon().parse<Activity>(
                request.body()) ?: return response.badRequest("Expected Activity json serialized object," +
                "found: ${request.body()}")
        JdbiConfiguration.INSTANCE.jdbi.useExtension<ActivityDao, SQLException>(ActivityDao::class.java) {
            it.insertNewActivity(
                activity.name,
                activity.activityTypeId,
                activity.acronym,
                activity.healthParameterIds)
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the activities
     */
    fun getAllActivities(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Activity>, ActivityDao, SQLException>(ActivityDao::class.java)
        { it.selectAllActivities() }
            .toJson()
    }

    /**
     * Retrieves all the activities
     */
    fun getActivityById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Activity>, ActivityDao, SQLException>(ActivityDao::class.java)
        { it.selectActivityById(request.params(Params.Activity.ID).toInt()) }
            .toJson()
    }

    /**
     * Retrieves all the activities by activity type id
     */
    fun getActivitiesByActivityTypeId(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Activity>, ActivityDao, SQLException>(ActivityDao::class.java)
        { it.selectActivitiesByActivityTypeId(request.params(Params.Activity.ACTIVITY_TYPE_ID).toInt()) }
                .toJson()
    }
}