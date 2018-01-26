package microservices.microdb

import microservices.microdb.controllers.RouteController
import microservices.microdb.utils.JdbiConfiguration

fun main(args: Array<String>) {

    JdbiConfiguration.init()

    RouteController.initRoutes()
}