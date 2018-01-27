package main.kotlin.microdb.controllers.api

import JdbiConfiguration
import Params
import main.kotlin.microdb.dao.ActivityDao
import main.kotlin.microdb.model.Activity
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
                    request.queryParams(Params.Activity.EXPECTED_EFFECT),
                    request.queryParams(Params.Activity.TYPE_ID).toInt(),
                    request.queryParams(Params.Activity.SIGNATURE),
                    request.queryParams(Params.Activity.STATUS_ID).toInt())
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
        { it.selectActivityById(request.queryParams(Params.Activity.ID).toInt()) }
                .toJson()
    }
}