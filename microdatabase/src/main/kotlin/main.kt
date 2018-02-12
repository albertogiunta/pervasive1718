
import controllers.RouteController
import spark.kotlin.ignite

fun main(args: Array<String>) {

    MicroDatabase.init(8100)

}

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