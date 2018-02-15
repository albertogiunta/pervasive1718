@file:Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")

package controllers.api

import badRequest
import com.beust.klaxon.Klaxon
import logic.TaskManager
import model.VisibleTask
import ok
import okCreated
import spark.Request
import spark.Response
import utils.toJson

object VisorApi {

    val taskManager : TaskManager = TaskManager()

    /**
     * Add a task
     */
    fun addTask(request: Request, response: Response): String {
        val task: VisibleTask = Klaxon().parse<VisibleTask>(request.body()) ?: return response.badRequest()
        taskManager.addTask(task)
        return response.okCreated()
    }

    /**
     * Removes a task
     */
    fun removeTask(request: Request, response: Response): String {
        taskManager.removeTask(request.params("taskId").toInt())
        return response.ok()
    }

    /**
     * Retrieve all tasks
     */
    fun getAllTasks(request: Request, response: Response): String {
        return taskManager.getAllTasks()
            .toJson()
    }
}