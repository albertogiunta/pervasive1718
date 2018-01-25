import controllers.ActivityController
import controllers.RoleController
import utils.JdbiConfiguration

fun main(args: Array<String>) {

    JdbiConfiguration.init()

    RoleController.initRoutes()
    ActivityController.initRoutes()

}