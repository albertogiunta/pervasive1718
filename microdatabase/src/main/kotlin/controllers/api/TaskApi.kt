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
import utils.KlaxonDate
import utils.dateConverter
import utils.toJson
import java.sql.SQLException

object TaskApi {

    /**
     * Inserts a new role inside of the Task table
     */
    fun addTask(request: Request, response: Response): String {
        val task: Task = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<Task>(request.body())
                ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.insertNewTask(
                task.id,
                task.sessionId,
                task.operatorId,
                task.startTime,
                task.endTime,
                task.activityId,
                task.statusId)
        }
        return response.okCreated()
    }

    fun updateTaskStatus(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.updateTaskStatus(
                request.params(Params.Task.ID).toInt(),
                request.params(Params.Task.STATUS_ID).toInt())
        }
        return response.okCreated()
    }

    fun removeTaskStatus(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.removeTask(request.params(Params.Task.ID).toInt())
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