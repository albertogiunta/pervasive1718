import Const.sessionPort
import spark.kotlin.ignite

fun main(args: Array<String>) {
    MicroSession.init(sessionPort)
}

object MicroSession {

    fun init(localPort: Int) {
        with(ignite()) {
            port(localPort)
            service.path("", RouteController.routes(localPort))
            service.init()
        }
    }

}