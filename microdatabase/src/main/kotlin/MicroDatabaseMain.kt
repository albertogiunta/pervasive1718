import controllers.RouteController

object MicroDatabaseMain {
    @JvmStatic
    fun main(args: Array<String>) {
        MicroDatabase.init(8100)
    }
}

object MicroDatabase {
    fun init(localPort: Int) {
        RouteController.init(localPort)
        JdbiConfiguration.init()
    }
}