package controllers

import controllers.api.VisorApi
import spark.Request
import spark.Response
import spark.Spark.path
import spark.kotlin.*

interface Controller {

    companion object {
        const val applicationJsonRequestType = "application/json"
    }

    fun initRoutes()

}

object RouteController : Controller {

    override fun initRoutes() {

        options("/*") {

            val accessControlRequestHeaders = request
                    .headers("Access-Control-Request-Headers")
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders)
            }

            val accessControlRequestMethod = request
                    .headers("Access-Control-Request-Method")
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod)
            }

            "OK"
        }

        before { response.header("Access-Control-Allow-Origin", "*") }

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
