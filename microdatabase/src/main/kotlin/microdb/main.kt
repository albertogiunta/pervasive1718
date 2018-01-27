import main.kotlin.microdb.controllers.RouteController

fun main(args: Array<String>) {

    JdbiConfiguration.init()

    RouteController.initRoutes()
}