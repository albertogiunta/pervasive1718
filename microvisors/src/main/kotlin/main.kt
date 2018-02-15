import config.ConfigLoader
import config.Services
import controllers.RouteController

fun main(args: Array<String>) {
    ConfigLoader().load()
    RouteController.initRoutes(Services.VISORS.port)
}