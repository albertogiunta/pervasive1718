import controllers.ActivityApi
import controllers.HealthParameterApi
import controllers.RoleApi
import utils.JdbiConfiguration

fun main(args: Array<String>) {

    JdbiConfiguration.init()

    RoleApi.initRoutes()
    ActivityApi.initRoutes()
    HealthParameterApi.initRoutes()
}