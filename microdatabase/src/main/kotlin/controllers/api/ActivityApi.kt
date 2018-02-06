package controllers.api

import JdbiConfiguration
import Params
import dao.ActivityDao
import model.Activity
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object ActivityApi {

    /**
     * Retrieves all the activities
     */
    fun addActivity(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<ActivityDao, SQLException>(ActivityDao::class.java) {
            it.insertNewActivity(
                request.queryParams(Params.Activity.NAME),
                request.queryParams(Params.Activity.ACTIVITY_TYPE_ID).toInt(),
                request.queryParams(Params.Activity.ACRONYM),
                request.queryParams(Params.Activity.BOUNDARY_ID).toInt())
        }
            .toJson()
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
}