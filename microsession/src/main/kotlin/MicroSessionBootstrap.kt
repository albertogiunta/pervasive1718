object MicroSessionBootstrap {
    fun init(localPort: Int) {
        RouteController.initRoutes(localPort)
    }
}