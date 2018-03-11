package controllers

//import RestParams.applicationJsonRequestType
import Params
import config.Services
import controllers.api.*
import spark.Spark.path
import spark.Spark.port
import spark.kotlin.delete
import spark.kotlin.get
import spark.kotlin.post
import spark.kotlin.put

object RouteController {

    fun init() {

        val applicationJson = Services.Utils.RESTParams.applicationJson

        port(Services.DATA_BASE.port)

        /**
         * NOTE: Calling APIs is CASE SENSITIVE. Use of camelCase on path definition is then discouraged.
         */
        path("/api") {

            path("/${Params.Activity.API_NAME}") {
                get("", applicationJson) { ActivityApi.getAllActivities(request, response) }
                get("/:id", applicationJson) { ActivityApi.getActivityById(request, response) }
                get("/type/:activitytypeid", applicationJson) { ActivityApi.getActivitiesByActivityTypeId(request, response) }
                post("", applicationJson) { ActivityApi.addActivity(request, response) }
            }

            path("/${Params.ActivityType.API_NAME}") {
                get("", applicationJson) { ActivityTypeApi.getAllActivityTypes(request, response) }
                get("/:id", applicationJson) { ActivityTypeApi.getActivityTypeById(request, response) }
                post("", applicationJson) { ActivityTypeApi.addActivityType(request, response) }
            }

            path("/${Params.Boundary.API_NAME}") {
                get("", applicationJson) { BoundaryApi.getAllBoundaries(request, response) }
                get("/:id", applicationJson) { BoundaryApi.getBoundaryById(request, response) }
                post("", applicationJson) { BoundaryApi.addBoundary(request, response) }
            }

            path("/${Params.HealthParameter.API_NAME}") {
                get("", applicationJson) { HealthParameterApi.getAllHealthParameters(request, response) }
                get("/:id", applicationJson) { HealthParameterApi.getHealthParameterById(request, response) }
                post("", applicationJson) { HealthParameterApi.addHealthParameter(request, response) }
            }

            path("/${Params.Role.API_NAME}") {
                get("", applicationJson) { RoleApi.getAllRoles(request, response) }
                get("/:id", applicationJson) { RoleApi.getRoleById(request, response) }
                post("", applicationJson) { RoleApi.addRole(request, response) }
            }

            path("/${Params.Log.API_NAME}") {
                path("/session/:sessionid") {
                    get("", applicationJson) { LogApi.getAllLogEntriesBySessionId(request, response) }
                    path("/${Params.HealthParameter.API_NAME}") {
                        get("/:${Params.Log.HEALTH_PARAMETER_ID}", applicationJson) { LogApi.getAllLogEntriesByHealthParameterId(request, response) }
                        get("/:${Params.Log.HEALTH_PARAMETER_ID}/minthreshold/:${Params.Log.HEALTH_PARAMETER_VALUE}", applicationJson) { LogApi.getAllLogEntriesByHealthParameterIdAboveValue(request, response) }
                    }
                }
                post("", applicationJson) { LogApi.addLogEntry(request, response) }
            }

            path("/${Params.Operator.API_NAME}") {
                get("", applicationJson) { OperatorApi.getAllOperators(request, response) }
                get("/:id", applicationJson) { OperatorApi.getOperatorById(request, response) }
                post("", applicationJson) { OperatorApi.addOperator(request, response) }
            }

            path("/${Params.Session.API_NAME}") {
                get("", applicationJson) { SessionApi.getAllSessions(request, response) }
                get("/opensessions/:${Params.Session.LEADER_CF}", applicationJson) { SessionApi.getAllOpenSessionsByLeaderCF(request, response) }
                post("", applicationJson) { SessionApi.addSession(request, response) }
                put("/:${Params.Session.SESSION_ID}", applicationJson) { SessionApi.closeSessionBySessionId(request, response) }
                get("/:${Params.Session.SESSION_ID}/report", applicationJson) {SessionApi.generateReport(request, response)}
            }

            path("/${Params.Task.API_NAME}") {
                get("", applicationJson) { TaskApi.getAllTasks(request, response) }
                get("/:id", applicationJson) { TaskApi.getTaskById(request, response) }
                get("/session/:id", applicationJson) { TaskApi.getTasksBySessionId(request, response) }
                post("", applicationJson) { TaskApi.addTask(request, response) }
                put("/:id", applicationJson) { TaskApi.updateTaskStatus(request, response) }
                put("/name/:${Params.Task.TASK_NAME}", applicationJson) { TaskApi.updateTaskStatusByName(request, response) }
                put("/stop/:${Params.Task.TASK_NAME}", applicationJson) { TaskApi.updateTaskEndTimeByName(request, response) }
                delete("/:id", applicationJson) { TaskApi.removeTaskById(request, response) }
                delete("/name/:${Params.Task.TASK_NAME}", applicationJson) { TaskApi.removeTaskByName(request, response) }
            }

            path("/${Params.TaskStatus.API_NAME}") {
                get("", applicationJson) { TaskStatusApi.getAllTaskStatus(request, response) }
                get("/:id", applicationJson) { TaskStatusApi.getTaskStatusById(request, response) }
                post("", applicationJson) { TaskStatusApi.addTaskStatus(request, response) }
            }
        }
    }
}