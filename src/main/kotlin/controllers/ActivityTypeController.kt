package controllers

import dao.ActivityTypeDao
import model.ActivityType
import spark.kotlin.get
import spark.kotlin.post
import utils.JdbiConfiguration
import utils.Params
import utils.okCreated
import utils.toJson
import java.sql.SQLException

object ActivityTypeController : Controller {
    override fun initRoutes() {

        /**
         * Retrieves all the activities types
         */
        get("/activitytype", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.withExtension<List<ActivityType>, ActivityTypeDao, SQLException>(ActivityTypeDao::class.java)
            { it.selectAllActivityTypes() }
                    .toJson()
        }

        /**
         * Retrieves a specified activity by id
         */
        get("/activitytype/:id", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.withExtension<List<ActivityType>, ActivityTypeDao, SQLException>(ActivityTypeDao::class.java)
            { it.selectActivityTypeById(request.params(Params.Role.ID).toInt()) }
                    .toJson()
        }

        /**
         * Add a new activity type
         */
        post("/activitytype", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.useExtension<ActivityTypeDao, SQLException>(ActivityTypeDao::class.java)
            { it.insertNewActivityType(request.queryParams(Params.Role.NAME)) }
            response.okCreated()
        }
    }
}