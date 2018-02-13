@file:JvmName("Session")

import DefaultPorts.sessionPort

object MicroSessionMain {
    @JvmStatic
    fun main(args: Array<String>) {
        MicroSession.init(sessionPort)
    }
}

object MicroSession {

    fun init(localPort: Int) {
        RouteController.initRoutes(localPort)
    }

}