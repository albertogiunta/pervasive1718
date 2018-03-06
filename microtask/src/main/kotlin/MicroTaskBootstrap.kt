import config.ConfigLoader
import config.Services
import logic.TaskController
import networking.WSTaskServer


object MicroTaskBootstrap {

    fun init(args: Array<String>) {
        ConfigLoader().load(args)
        WSServerInitializer.init(WSTaskServer::class.java, wsPath = WSParams.WS_PATH_TASK, wsPort = Services.TASK_HANDLER.port)

        if (Services.isNotStartedIndependently()) {
            waitInitAndNotifyToMicroSession(Services.TASK_HANDLER.executableName, Services.instanceId())
        }

        TaskController.fetchActivitiesFromDB()
        TaskController.getLastTaskId()
        println("[${Services.TASK_HANDLER.executableName}] FINISHED bootstrap")
    }
}
