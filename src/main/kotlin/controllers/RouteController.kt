package controllers

import controllers.Controller.Companion.applicationJsonRequestType
import spark.Spark.path
import spark.kotlin.get
import spark.kotlin.post

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
                post("/add", applicationJsonRequestType) { LogController.addLogEntry(request, response) }
                get("/all", applicationJsonRequestType) { LogController.getAllLogEntries(request, response) }

                path("/healthParameter") {
                    get("/:id", applicationJsonRequestType) { LogController.getAllLogEntriesByHealthParameterId(request, response) }
                    get("/:id/minThreshold/:value", applicationJsonRequestType) { LogController.getAllLogEntriesByHealthParameterIdAboveValue(request, response) }
                }
            }

        }
    }
}

/// /log/healthParameter/:id