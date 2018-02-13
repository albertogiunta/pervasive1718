package controllers

import Params
import RestParams
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
         **/
        path("/api") {

            path("/${Params.Session.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { SessionApi.addSession(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { SessionApi.getAllSessions(request, response) }
                delete("/close/:sessionId", RestParams.applicationJsonRequestType) { SessionApi.removeSessionBySessionId(request, response) }
            }

            path("/${Params.Activity.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { ActivityApi.addActivity(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { ActivityApi.getAllActivities(request, response) }
                get("/:id", RestParams.applicationJsonRequestType) { ActivityApi.getActivityById(request, response) }
            }

            path("/${Params.ActivityType.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { ActivityTypeApi.addActivityType(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { ActivityTypeApi.getAllActivityTypes(request, response) }
                get("/:id", RestParams.applicationJsonRequestType) { ActivityTypeApi.getActivityTypeById(request, response) }
            }

            path("/${Params.Boundary.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { BoundaryApi.addBoundary(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { BoundaryApi.getAllBoundaries(request, response) }
                get("/:id", RestParams.applicationJsonRequestType) { BoundaryApi.getBoundaryById(request, response) }
            }

            path("/${Params.HealthParameter.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { HealthParameterApi.addHealthParameter(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { HealthParameterApi.getAllHealthParameters(request, response) }
                get("/:id", RestParams.applicationJsonRequestType) { HealthParameterApi.getHealthParameterById(request, response) }
            }

            path("/${Params.Role.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { RoleApi.addRole(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { RoleApi.getAllRoles(request, response) }
                get("/:id", RestParams.applicationJsonRequestType) { RoleApi.getRoleById(request, response) }
            }

            path("/${Params.Log.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { LogApi.addLogEntry(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { LogApi.getAllLogEntries(request, response) }

                path("/${Params.HealthParameter.TABLE_NAME}") {
                    get("/:${Params.Log.HEALTH_PARAMETER_ID}", RestParams.applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterId(request, response) }
                    get("/:${Params.Log.HEALTH_PARAMETER_ID}/minthreshold/:${Params.Log.HEALTH_PARAMETER_VALUE}", RestParams.applicationJsonRequestType) { LogApi.getAllLogEntriesByHealthParameterIdAboveValue(request, response) }
                }
            }

            path("/${Params.Operator.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { OperatorApi.addOperator(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { OperatorApi.getAllOperators(request, response) }
                get("/:id", RestParams.applicationJsonRequestType) { OperatorApi.getOperatorById(request, response) }
            }

            path("/${Params.Task.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { TaskApi.addTask(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { TaskApi.getAllTasks(request, response) }
                get("/:id", RestParams.applicationJsonRequestType) { TaskApi.getTaskById(request, response) }
            }

            path("/${Params.TaskStatus.TABLE_NAME}") {
                post("/add", RestParams.applicationJsonRequestType) { TaskStatusApi.addTaskStatus(request, response) }
                get("/all", RestParams.applicationJsonRequestType) { TaskStatusApi.getAllTaskStatuss(request, response) }
                get("/:id", RestParams.applicationJsonRequestType) { TaskStatusApi.getTaskStatusById(request, response) }
            }
        }
    }
}
