import controllers.RouteController
import utils.JdbiConfiguration

fun main(args: Array<String>) {

    JdbiConfiguration.init()

    RouteController.initRoutes()
}