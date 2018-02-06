@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import dao.TaskDao
import model.Task
import okCreated
import spark.Request
import spark.Response
import toJson
import java.sql.SQLException

object TaskApi {

    /**
     * Inserts a new role inside of the Task table
     */
    fun addTask(request: Request, response: Response): String {
        val task: Task = Klaxon().parse<Task>(request.body()) ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.insertNewTask(
                task.operatorId,
                task.startTime,
                task.endTime,
                task.activityId,
                task.statusId)
        }
        return response.okCreated()
    }

    /**
     * Retrieves all the roles
     */
    fun getAllTasks(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Task>, TaskDao, SQLException>(TaskDao::class.java)
        { it.selectAllTasks() }
            .toJson()
    }

    /**
     * Retrieves a specific role based on the passed role ID
     */
    fun getTaskById(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Task>, TaskDao, SQLException>(TaskDao::class.java)
        { it.selectTaskById(request.params(Params.Task.ID).toInt()) }
            .toJson()
    }

}