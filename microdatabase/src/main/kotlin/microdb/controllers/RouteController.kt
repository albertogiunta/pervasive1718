package microdb.controllers

import microdb.controllers.api.*
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

        /*
         * NOTE: Calling APIs is CASE SENSITIVE. Use of camelCase on path definition is then discouraged.
         */
        path("/api") {

            path("/activity") {
                post("/add", Controller.applicationJsonRequestType) { ActivityApi.addActivity(request, response) }
                get("/all", Controller.applicationJsonRequestType) { ActivityApi.getAllActivities(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { ActivityApi.getActivityById(request, response) }
            }

            path("/activitytype") {
                post("/add", Controller.applicationJsonRequestType) { ActivityTypeApi.addActivityType(request, response) }
                get("/all", Controller.applicationJsonRequestType) { ActivityTypeApi.getAllActivityTypes(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { ActivityTypeApi.getActivityTypeById(request, response) }
            }

            path("/healthparameter") {
                post("/add", Controller.applicationJsonRequestType) { HealthParameterApi.addHealthParameter(request, response) }
                get("/all", Controller.applicationJsonRequestType) { HealthParameterApi.getAllHealthParameters(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { HealthParameterApi.getHealthParameterById(request, response) }
            }

            path("/role") {
                post("/add", Controller.applicationJsonRequestType) { RoleApi.addRole(request, response) }
                get("/all", Controller.applicationJsonRequestType) { RoleApi.getAllRoles(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { RoleApi.getRoleById(request, response) }
            }

            path("/log") {
                post("/add", Controller.applicationJsonRequestType) { LogApi.addLogEntry(request, response) }
                get("/all", Controller.applicationJsonRequestType) { LogApi.getAllLogEntries(request, response) }

                path("/healthparameter") {
                    get("/:id", Controller.applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterId(request, response) }
                    get("/:id/minthreshold/:value", Controller.applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterIdAboveValue(request, response) }
                }
            }

            path("/operator") {
                post("/add", Controller.applicationJsonRequestType) { OperatorApi.addOperator(request, response) }
                get("/all", Controller.applicationJsonRequestType) { OperatorApi.getAllOperators(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { OperatorApi.getOperatorById(request, response) }
            }

            path("/status") {
                post("/add", Controller.applicationJsonRequestType) { StatusApi.addStatus(request, response) }
                get("/all", Controller.applicationJsonRequestType) { StatusApi.getAllStatuses(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { StatusApi.getStatusById(request, response) }
            }

            path("/task") {
                post("/add", Controller.applicationJsonRequestType) { TaskApi.addTask(request, response) }
                get("/all", Controller.applicationJsonRequestType) { TaskApi.getAllTasks(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { TaskApi.getTaskById(request, response) }
            }
        }
    }
}
