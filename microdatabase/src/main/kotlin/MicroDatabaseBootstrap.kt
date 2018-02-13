import controllers.RouteController
import spark.kotlin.ignite

object MicroDatabase {

    fun init(localPort: Int) {
        with(ignite()) {
            port(localPort)
            service.path("", RouteController.routes(localPort))
            service.init()
        }
        JdbiConfiguration.init()
    }
}