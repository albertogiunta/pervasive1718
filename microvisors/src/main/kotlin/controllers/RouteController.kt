package controllers

import config.Services
import config.Services.Utils.RESTParams
import controllers.api.VisorApi
import spark.Spark.path
import spark.kotlin.*


object RouteController {

    fun initRoutes() {

        port(Services.VISORS.port)

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
            get("/all", RESTParams.applicationJson) { VisorApi.getAllTasks(request, response) }
            post("/add", RESTParams.applicationJson) { VisorApi.addTask(request, response) }
            delete("/remove/:taskId", RESTParams.applicationJson) { VisorApi.removeTask(request, response) }
        }
    }
}
