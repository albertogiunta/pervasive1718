package microservices.microdb.controllers.api

import microservices.microdb.dao.ActivityTypeDao
import microservices.microdb.model.ActivityType
import microservices.microdb.utils.JdbiConfiguration
import microservices.microdb.utils.Params
import microservices.microdb.utils.okCreated
import microservices.microdb.utils.toJson
import spark.Request
import spark.Response
import java.sql.SQLException

object ActivityTypeApi {

    /**
     * Add a new activity type
     */
    fun addActivityType(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<ActivityTypeDao, SQLException>(ActivityTypeDao::class.java)
        { it.insertNewActivityType(request.queryParams(Params.ActivityType.NAME)) }
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