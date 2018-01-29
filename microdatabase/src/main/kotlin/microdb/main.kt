import microdb.controllers.RouteController

fun main(args: Array<String>) {

    JdbiConfiguration.init()

    RouteController.initRoutes()
}