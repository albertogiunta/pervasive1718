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

            path("/${Params.Activity.TABLE_NAME}") {
                post("/add", applicationJson) { ActivityApi.addActivity(request, response) }
                get("/all", applicationJson) { ActivityApi.getAllActivities(request, response) }
                get("/:id", applicationJson) { ActivityApi.getActivityById(request, response) }
                get("/all/:activitytypeid", applicationJson) { ActivityApi.getActivitiesByActivityTypeId(request, response) }
            }

            path("/${Params.ActivityType.TABLE_NAME}") {
                post("/add", applicationJson) { ActivityTypeApi.addActivityType(request, response) }
                get("/all", applicationJson) { ActivityTypeApi.getAllActivityTypes(request, response) }
                get("/:id", applicationJson) { ActivityTypeApi.getActivityTypeById(request, response) }
            }

            path("/${Params.Boundary.TABLE_NAME}") {
                post("/add", applicationJson) { BoundaryApi.addBoundary(request, response) }
                get("/all", applicationJson) { BoundaryApi.getAllBoundaries(request, response) }
                get("/:id", applicationJson) { BoundaryApi.getBoundaryById(request, response) }
            }

            path("/${Params.HealthParameter.TABLE_NAME}") {
                post("/add", applicationJson) { HealthParameterApi.addHealthParameter(request, response) }
                get("/all", applicationJson) { HealthParameterApi.getAllHealthParameters(request, response) }
                get("/:id", applicationJson) { HealthParameterApi.getHealthParameterById(request, response) }
            }

            path("/${Params.Role.TABLE_NAME}") {
                post("/add", applicationJson) { RoleApi.addRole(request, response) }
                get("/all", applicationJson) { RoleApi.getAllRoles(request, response) }
                get("/:id", applicationJson) { RoleApi.getRoleById(request, response) }
            }

            path("/${Params.Log.TABLE_NAME}") {
                post("/add", applicationJson) { LogApi.addLogEntry(request, response) }
                get("/all", applicationJson) { LogApi.getAllLogEntries(request, response) }

                path("/${Params.HealthParameter.TABLE_NAME}") {
                    get("/:${Params.Log.HEALTH_PARAMETER_ID}", applicationJson) { LogApi.getAllLogEntriesByHealthParameterId(request, response) }
                    get("/:${Params.Log.HEALTH_PARAMETER_ID}/minthreshold/:${Params.Log.HEALTH_PARAMETER_VALUE}", applicationJson) { LogApi.getAllLogEntriesByHealthParameterIdAboveValue(request, response) }
                }
            }

            path("/${Params.Operator.TABLE_NAME}") {
                post("/add", applicationJson) { OperatorApi.addOperator(request, response) }
                get("/all", applicationJson) { OperatorApi.getAllOperators(request, response) }
                get("/:id", applicationJson) { OperatorApi.getOperatorById(request, response) }
            }

            path("/${Params.Session.TABLE_NAME}") {
                post("/add/patientcf/:${Params.Session.PATIENT_CF}/leadercf/:${Params.Session.LEADER_CF}/instanceid/:${Params.Session.INSTANCE_ID}", applicationJson) { SessionApi.addSession(request, response) }
                post("/add/sessionid/:${Params.Session.SESSION_ID}/patientcf/:${Params.Session.PATIENT_CF}/leadercf/:${Params.Session.LEADER_CF}/instanceid/:${Params.Session.INSTANCE_ID}", applicationJson) { SessionApi.addSessionWithId(request, response) }
                get("/all", applicationJson) { SessionApi.getAllSessions(request, response) }
                get("/all/open/:${Params.Session.LEADER_CF}", applicationJson) { SessionApi.getAllOpenSessionsByLeaderCF(request, response) }
                delete("/close/:${Params.Session.SESSION_ID}", applicationJson) { SessionApi.closeSessionBySessionId(request, response) }
            }

            path("/${Params.Task.TABLE_NAME}") {
                post("/add", applicationJson) { TaskApi.addTask(request, response) }
                put("/:id/status/:statusId", applicationJson) { TaskApi.updateTaskStatus(request, response) }

                put("/update/:${Params.Task.TASK_NAME}/status/:${Params.Task.STATUS_ID}", applicationJson) { TaskApi.updateTaskStatusByName(request, response) }

                put("/stop/:${Params.Task.TASK_NAME}", applicationJson) { TaskApi.updateTaskEndtimeByName(request, response) }

                put("/stopTask", applicationJson) { TaskApi.updateTaskEndtime(request, response) }
                delete("/:id", applicationJson) { TaskApi.removeTaskStatus(request, response) }

                delete("/remove/:${Params.Task.TASK_NAME}", applicationJson) { TaskApi.removeTaskStatusByName(request, response) }

                get("/all", applicationJson) { TaskApi.getCurrentSessionTasks(request, response) }
                get("/history", applicationJson) { TaskApi.getAllTasks(request, response) }
                get("/all/sessionid/:${Params.Session.SESSION_ID}", applicationJson) { TaskApi.getAllTasksBySession(request, response) }
                get("/:id", applicationJson) { TaskApi.getTaskById(request, response) }
            }

            path("/${Params.TaskStatus.TABLE_NAME}") {
                post("/add", applicationJson) { TaskStatusApi.addTaskStatus(request, response) }
                get("/all", applicationJson) { TaskStatusApi.getAllTaskStatuss(request, response) }
                get("/:id", applicationJson) { TaskStatusApi.getTaskStatusById(request, response) }
            }
        }
    }
}