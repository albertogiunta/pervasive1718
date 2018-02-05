package controllers.api

import JdbiConfiguration
import Params
import dao.TaskStatusDao
import model.TaskStatus
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object TaskStatusApi {

    /**
     * Add a new activity status type
     */
    fun addTaskStatus(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskStatusDao, SQLException>(TaskStatusDao::class.java)
        { it.insertNewTaskStatus(request.queryParams(Params.TaskStatus.NAME)) }
        return response.okCreated()
    }

    /**
     * Retrieves all the activity status
     */
    fun getAllTaskStatuss(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<TaskStatus>, TaskStatusDao, SQLException>(TaskStatusDao::class.java)
        { it.selectAllTaskStatuss() }
            .toJson()
    }

    /**
     * Retrieves a specified activity status by id
     */
    fun getTaskStatusById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<TaskStatus>, TaskStatusDao, SQLException>(TaskStatusDao::class.java)
        { it.selectTaskStatusById(request.params(Params.TaskStatus.ID).toInt()) }
            .toJson()
    }

}