package controllers

import dao.ActivityDao
import model.Activity
import spark.kotlin.get
import utils.JdbiConfiguration
import utils.toJson
import java.sql.SQLException

object ActivityController : Controller {
    override fun initRoutes() {

        /**
         * Retrieves all the activities
         */
        get("/activity", "application/json") {
            JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Activity>, ActivityDao, SQLException>(ActivityDao::class.java)
            { it.selectAllActivities() }
                .toJson()
        }
    }
}