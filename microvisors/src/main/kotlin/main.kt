import config.Services
import controllers.RouteController

fun main(args: Array<String>) {

    RouteController.initRoutes(Services.VISORS.port)
}