@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.TaskStatusDao
import model.TaskStatus
import okCreated
import spark.Request
import spark.Response
import utils.toJson
import java.sql.SQLException

object TaskStatusApi {

    /**
     * Add a new activity status type
     */
    fun addTaskStatus(request: Request, response: Response): String {
        val taskStatus: TaskStatus = Klaxon().parse<TaskStatus>(request.body()) ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskStatusDao, SQLException>(TaskStatusDao::class.java)
        { it.insertNewTaskStatus(taskStatus.name) }
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