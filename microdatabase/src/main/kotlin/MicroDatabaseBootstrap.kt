import config.Services
import controllers.RouteController

object MicroDatabaseBootstrap {

    fun init(startIndependent: Boolean = false) {
        RouteController.init()
        JdbiConfiguration.init()
        if (!startIndependent) waitInitAndNotifyToMicroSession(Services.DATA_BASE.executableName, Services.instanceId())
    }
}