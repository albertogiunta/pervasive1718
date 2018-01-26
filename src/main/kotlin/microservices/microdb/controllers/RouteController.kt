package microservices.microdb.controllers

import microservices.microdb.controllers.Controller.Companion.applicationJsonRequestType
import microservices.microdb.controllers.api.*
import spark.Spark.path
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

        path("/api") {

            path("/activity") {
                post("/add", applicationJsonRequestType) { ActivityApi.addActivity(request, response) }
                get("/all", applicationJsonRequestType) { ActivityApi.getAllActivities(request, response) }
                get("/:id", applicationJsonRequestType) { ActivityApi.getActivityById(request, response) }
            }

            path("/activityType") {
                post("/add", applicationJsonRequestType) { ActivityTypeApi.addActivityType(request, response) }
                get("/all", applicationJsonRequestType) { ActivityTypeApi.getAllActivityTypes(request, response) }
                get("/:id", applicationJsonRequestType) { ActivityTypeApi.getActivityTypeById(request, response) }
            }

            path("/healthParameter") {
                post("/add", applicationJsonRequestType) { HealthParameterApi.addHealthParameter(request, response) }
                get("/all", applicationJsonRequestType) { HealthParameterApi.getAllHealthParameters(request, response) }
                get("/:id", applicationJsonRequestType) { HealthParameterApi.getHealthParameterById(request, response) }
            }

            path("/role") {
                post("/add", applicationJsonRequestType) { RoleApi.addRole(request, response) }
                get("/all", applicationJsonRequestType) { RoleApi.getAllRoles(request, response) }
                get("/:id", applicationJsonRequestType) { RoleApi.getRoleById(request, response) }
            }

            path("/log") {
                post("/add", applicationJsonRequestType) { LogApi.addLogEntry(request, response) }
                get("/all", applicationJsonRequestType) { LogApi.getAllLogEntries(request, response) }

                path("/healthParameter") {
                    get("/:id", applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterId(request, response) }
                    get("/:id/minThreshold/:value", applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterIdAboveValue(request, response) }
                }
            }

            path("/operator") {
                post("/add", applicationJsonRequestType) { OperatorApi.addOperator(request, response) }
                get("/all", applicationJsonRequestType) { OperatorApi.getAllOperators(request, response) }
                get("/:id", applicationJsonRequestType) { OperatorApi.getOperatorById(request, response) }
            }

            path("/status") {
                post("/add", applicationJsonRequestType) { StatusApi.addStatus(request, response) }
                get("/all", applicationJsonRequestType) { StatusApi.getAllStatuses(request, response) }
                get("/:id", applicationJsonRequestType) { StatusApi.getStatusById(request, response) }
            }

            path("/task") {
                post("/add", applicationJsonRequestType) { TaskApi.addTask(request, response) }
                get("/all", applicationJsonRequestType) { TaskApi.getAllTasks(request, response) }
                get("/:id", applicationJsonRequestType) { TaskApi.getTaskById(request, response) }
            }

        }

    }
}
