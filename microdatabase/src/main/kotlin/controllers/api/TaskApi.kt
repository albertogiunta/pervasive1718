@file:Suppress("UNUSED_PARAMETER")

package controllers.api

import JdbiConfiguration
import Params
import badRequest
import com.beust.klaxon.Klaxon
import controllers.SessionController
import dao.TaskDao
import model.Task
import ok
import okCreated
import spark.Request
import spark.Response
import utils.KlaxonDate
import utils.dateConverter
import utils.toJson
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*

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
                task.name,
                task.sessionId,
                task.operatorCF,
                task.startTime,
                task.activityId,
                task.statusId)
        }
        return response.okCreated()
    }

    /*fun updateTaskStatus(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.updateTaskStatus(
                request.params(Params.Task.ID).toInt(),
                request.params(Params.Task.STATUS_ID).toInt())
        }
        return response.okCreated()
    }*/

    fun updateTaskStatus(request: Request, response: Response): String {
        val task: Task = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<Task>(request.body())
                ?: return response.badRequest()
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.updateTaskStatus(
                    task.id,
                    task.statusId)
        }
        return response.okCreated()
    }

    fun updateTaskStatusByName(request: Request, response: Response): String {
        val task: Task = Klaxon().fieldConverter(KlaxonDate::class, dateConverter).parse<Task>(request.body())
                ?: return response.badRequest()

        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.updateTaskStatusByName(
                    task.name,
                    task.statusId
            )
        }
        return response.okCreated()
    }

    fun updateTaskEndTimeByName(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.updateTaskEndTimeByName(
                    request.params(Params.Task.TASK_NAME),
                    Timestamp(Date().time)
            )
        }
        return response.ok()
    }

    fun removeTaskById(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.removeTask(request.params(Params.Task.ID).toInt())
        }
        return response.ok()
    }

    fun removeTaskByName(request: Request, response: Response): String {
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.removeTaskByName(request.params(Params.Task.TASK_NAME))
        }
        return response.ok()
    }

    /**
     * Retrieves all the tasks
     */
    fun getAllTasks(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Task>, TaskDao, SQLException>(TaskDao::class.java)
        { it.selectAllTasks() }
            .toJson()
    }

    /**
     * Retrieves all the tasks for the current session
     */
    fun getTasksBySessionId(request: Request, response: Response): String {
        return JdbiConfiguration.INSTANCE.jdbi.withExtension<List<Task>, TaskDao, SQLException>(TaskDao::class.java)
        { it.selectAllTasks(request.params(Params.Session.SESSION_ID).toInt()) }.toJson()
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