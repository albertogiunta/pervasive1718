package main.kotlin.microdb.controllers.api

import JdbiConfiguration
import Params
import main.kotlin.microdb.dao.TaskDao
import main.kotlin.microdb.model.Task
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
        JdbiConfiguration.INSTANCE.jdbi.useExtension<TaskDao, SQLException>(TaskDao::class.java)
        {
            it.insertNewTask(
                    request.queryParams(Params.Task.OPERATOR_ID).toInt(),
                    request.queryParams(Params.Task.START_TIME),
                    request.queryParams(Params.Task.END_TIME),
                    request.queryParams(Params.Task.ACTIVITY_ID).toInt(),
                    request.queryParams(Params.Task.PROGRESS))
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