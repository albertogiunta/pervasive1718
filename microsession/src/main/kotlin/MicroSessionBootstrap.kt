import config.Services

object MicroSessionBootstrap {
    fun init() {
        RouteController.initRoutes()
        WSServerInitializer.init(SessionApi.WSSessionServer::class.java, wsPath = WSParams.WS_PATH_SESSION, wsPort = Services.SESSION.port)
    }
}