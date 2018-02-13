import controllers.RouteController
import spark.kotlin.ignite

object MicroDatabaseBootstrap {

    fun init(localPort: Int) {
        RouteController.init(localPort)
        JdbiConfiguration.init()
    }
}