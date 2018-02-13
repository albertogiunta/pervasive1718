package controllers

import Params
import RestParams.applicationJsonRequestType
import controllers.api.*
import spark.Spark.path
import spark.Spark.port
import spark.kotlin.delete
import spark.kotlin.get
import spark.kotlin.post

object RouteController {

    fun init(localPort: Int) {

        port(localPort)

        /**
         * NOTE: Calling APIs is CASE SENSITIVE. Use of camelCase on path definition is then discouraged.
         */
        path("/api") {

            path("/${Params.Activity.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { ActivityApi.addActivity(request, response) }
                get("/all", applicationJsonRequestType) { ActivityApi.getAllActivities(request, response) }
                get("/:id", applicationJsonRequestType) { ActivityApi.getActivityById(request, response) }
            }

            path("/${Params.ActivityType.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { ActivityTypeApi.addActivityType(request, response) }
                get("/all", applicationJsonRequestType) { ActivityTypeApi.getAllActivityTypes(request, response) }
                get("/:id", applicationJsonRequestType) { ActivityTypeApi.getActivityTypeById(request, response) }
            }

            path("/${Params.Boundary.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { BoundaryApi.addBoundary(request, response) }
                get("/all", applicationJsonRequestType) { BoundaryApi.getAllBoundaries(request, response) }
                get("/:id", applicationJsonRequestType) { BoundaryApi.getBoundaryById(request, response) }
            }

            path("/${Params.HealthParameter.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { HealthParameterApi.addHealthParameter(request, response) }
                get("/all", applicationJsonRequestType) { HealthParameterApi.getAllHealthParameters(request, response) }
                get("/:id", applicationJsonRequestType) { HealthParameterApi.getHealthParameterById(request, response) }
            }

            path("/${Params.Role.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { RoleApi.addRole(request, response) }
                get("/all", applicationJsonRequestType) { RoleApi.getAllRoles(request, response) }
                get("/:id", applicationJsonRequestType) { RoleApi.getRoleById(request, response) }
            }

            path("/${Params.Log.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { LogApi.addLogEntry(request, response) }
                get("/all", applicationJsonRequestType) { LogApi.getAllLogEntries(request, response) }

                path("/${Params.HealthParameter.TABLE_NAME}") {
                    get("/:${Params.Log.HEALTH_PARAMETER_ID}", applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterId(request, response) }
                    get("/:${Params.Log.HEALTH_PARAMETER_ID}/minthreshold/:${Params.Log.HEALTH_PARAMETER_VALUE}", applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterIdAboveValue(request, response) }
                }
            }

            path("/${Params.Operator.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { OperatorApi.addOperator(request, response) }
                get("/all", applicationJsonRequestType) { OperatorApi.getAllOperators(request, response) }
                get("/:id", applicationJsonRequestType) { OperatorApi.getOperatorById(request, response) }
            }

            path("/${Params.Session.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { SessionApi.addSession(request, response) }
                get("/all", applicationJsonRequestType) { SessionApi.getAllSessions(request, response) }
                delete("/close/:id", applicationJsonRequestType) { SessionApi.removeSessionBySessionId(request, response) }
            }

            path("/${Params.Task.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { TaskApi.addTask(request, response) }
                get("/all", applicationJsonRequestType) { TaskApi.getAllTasks(request, response) }
                get("/:id", applicationJsonRequestType) { TaskApi.getTaskById(request, response) }
            }

            path("/${Params.TaskStatus.TABLE_NAME}") {
                post("/add", applicationJsonRequestType) { TaskStatusApi.addTaskStatus(request, response) }
                get("/all", applicationJsonRequestType) { TaskStatusApi.getAllTaskStatuss(request, response) }
                get("/:id", applicationJsonRequestType) { TaskStatusApi.getTaskStatusById(request, response) }
            }
        }
    }
}
