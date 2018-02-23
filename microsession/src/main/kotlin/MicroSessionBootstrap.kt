import config.Services
import spark.Service

object MicroSessionBootstrap {
    fun init() {
        RouteController.initRoutes()
        val service = Service.ignite()
        service.port(Services.SESSION.port + 1)
        service.webSocket(WSParams.WS_PATH_SESSION, SessionApi.WSSessionServer::class.java)
        service.init()
    }
}