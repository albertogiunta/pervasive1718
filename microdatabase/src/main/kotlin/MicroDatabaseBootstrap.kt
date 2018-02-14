import controllers.RouteController

object MicroDatabaseBootstrap {

    fun init(localPort: Int) {
        RouteController.init(localPort)
        JdbiConfiguration.init()
    }
}