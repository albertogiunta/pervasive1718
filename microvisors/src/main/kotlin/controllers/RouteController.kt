package controllers

import controllers.api.VisorApi
import spark.Spark.path
import spark.kotlin.delete
import spark.kotlin.get
import spark.kotlin.post

interface Controller {

    companion object {
        const val applicationJsonRequestType = "application/json"
    }

    fun initRoutes()

}

object RouteController : Controller {

    override fun initRoutes() {

        /*
         * NOTE: Calling APIs is CASE SENSITIVE. Use of camelCase on path definition is then discouraged.
         */
        path("/api") {
                get("/all", Controller.applicationJsonRequestType) { VisorApi.getAllTasks(request, response)}
                post("/add", Controller.applicationJsonRequestType) { VisorApi.addTask(request, response) }
                delete("/remove/:taskId", Controller.applicationJsonRequestType) { VisorApi.removeTask(request, response) }
        }
    }
}
