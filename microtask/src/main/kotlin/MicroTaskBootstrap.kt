import config.Services
import networking.WSTaskServer


object MicroTaskBootstrap {

    fun init() {
        WSServerInitializer.init(WSTaskServer::class.java, wsPath = WSParams.WS_PATH_TASK, wsPort = Services.TASK_HANDLER.port)

        Thread.sleep(1000)
    }
}
