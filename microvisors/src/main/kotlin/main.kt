import DefaultPorts.visorsPort
import controllers.RouteController

fun main(args: Array<String>) {

    RouteController.initRoutes(visorsPort)
}