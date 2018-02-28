import config.Services
import controllers.RouteController

object MicroDatabaseBootstrap {

    fun init(notStartIndependently: Boolean = false) {
        RouteController.init()
        JdbiConfiguration.init()
        if (notStartIndependently) waitInitAndNotifyToMicroSession(Services.DATA_BASE.executableName, Services.instanceId())
    }
}