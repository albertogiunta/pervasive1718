import controllers.RouteController

object MicroDatabaseBootstrap {

    fun init() {
        RouteController.init()
        JdbiConfiguration.init()
    }
}