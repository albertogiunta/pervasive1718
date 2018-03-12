@file:Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")

package controllers.api

import badRequest
import com.beust.klaxon.Klaxon
import logic.TaskManager
import model.SessionInfo
import model.VisibleTask
import ok
import okCreated
import spark.Request
import spark.Response
import utils.PathGetter
import utils.toJson
import java.io.File

object VisorApi {

    private val taskManager : TaskManager = TaskManager()

    /**
     * Add a task
     */
    fun addTask(request: Request, response: Response): String {
        val task: VisibleTask = Klaxon().parse<VisibleTask>(
                request.body()) ?: return response.badRequest("Expected VisibleTask json serialized " +
                "object, found: ${request.body()}")
        taskManager.addTask(task)
        return response.okCreated()
    }

    /**
     * Creates or updates content of infoLoader.js about the patient
     */
    fun addSessionInfo(request: Request, response: Response): String {
        val info: SessionInfo = Klaxon().parse<SessionInfo>(
                request.body()) ?: return response.badRequest("Expected SessionInfo json serialized " +
                "object, found: ${request.body()}")
        File(PathGetter.getRootPath()+"microvisors/src/view/js/infoLoader.js").bufferedWriter().use { out ->
            out.write("var patientCF = \"${info.patientCF}\"" )}
        return response.okCreated()
    }

    /**
     * Erases the content of infoLoader.js about the patient
     */
    fun clearSessionInfo(request: Request, response: Response): String {
        File(PathGetter.getRootPath()+"microvisors/src/view/js/infoLoader.js").bufferedWriter().use { out ->
            out.write("")}
        return response.ok()
    }

    /**
     * Removes a task
     */
    fun removeTask(request: Request, response: Response): String {
        taskManager.removeTask(request.params("taskName").toString())
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