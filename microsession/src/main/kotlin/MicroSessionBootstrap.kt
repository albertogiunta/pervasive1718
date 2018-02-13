import Const.sessionPort
import spark.kotlin.ignite

object MicroSessionBootstrap {

    fun init(localPort: Int) {
        with(ignite()) {
            port(localPort)
            service.path("", RouteController.routes(localPort))
            service.init()
        }
    }
}