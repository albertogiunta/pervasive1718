package controllers

import controllers.api.*
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

            path("/${Params.Activity.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { ActivityApi.addActivity(request, response) }
                get("/all", Controller.applicationJsonRequestType) { ActivityApi.getAllActivities(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { ActivityApi.getActivityById(request, response) }
            }

            path("/${Params.ActivityType.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { ActivityTypeApi.addActivityType(request, response) }
                get("/all", Controller.applicationJsonRequestType) { ActivityTypeApi.getAllActivityTypes(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { ActivityTypeApi.getActivityTypeById(request, response) }
            }

            path("/${Params.Boundary.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { BoundaryApi.addStatus(request, response) }
                get("/all", Controller.applicationJsonRequestType) { BoundaryApi.getAllStatuses(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { BoundaryApi.getStatusById(request, response) }
            }

            path("/${Params.HealthParameter.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { HealthParameterApi.addHealthParameter(request, response) }
                get("/all", Controller.applicationJsonRequestType) { HealthParameterApi.getAllHealthParameters(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { HealthParameterApi.getHealthParameterById(request, response) }
            }

            path("/${Params.Role.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { RoleApi.addRole(request, response) }
                get("/all", Controller.applicationJsonRequestType) { RoleApi.getAllRoles(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { RoleApi.getRoleById(request, response) }
            }

            path("/${Params.Log.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { LogApi.addLogEntry(request, response) }
                get("/all", Controller.applicationJsonRequestType) { LogApi.getAllLogEntries(request, response) }

                path("/${Params.HealthParameter.TABLE_NAME}") {
                    get("/:id", Controller.applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterId(request, response) }
                    get("/:id/minthreshold/:value", Controller.applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterIdAboveValue(request, response) }
                }
            }

            path("/${Params.Operator.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { OperatorApi.addOperator(request, response) }
                get("/all", Controller.applicationJsonRequestType) { OperatorApi.getAllOperators(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { OperatorApi.getOperatorById(request, response) }
            }

            path("/${Params.Task.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { TaskApi.addTask(request, response) }
                get("/all", Controller.applicationJsonRequestType) { TaskApi.getAllTasks(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { TaskApi.getTaskById(request, response) }
            }

            path("/${Params.TaskStatus.TABLE_NAME}") {
                post("/add", Controller.applicationJsonRequestType) { TaskStatusApi.addTaskStatus(request, response) }
                get("/all", Controller.applicationJsonRequestType) { TaskStatusApi.getAllTaskStatuss(request, response) }
                get("/:id", Controller.applicationJsonRequestType) { TaskStatusApi.getTaskStatusById(request, response) }
            }
        }
    }
}
