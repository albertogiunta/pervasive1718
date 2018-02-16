import config.Services
import networking.WSTaskServer
import spark.kotlin.ignite
import utils.calculatePort


object MicroTaskBootstrap {

    fun init(taskPort: Int) {

        WSServerInitializer.init(WSTaskServer::class.java, wsPath = WSParams.WS_PATH_TASK, wsPort = taskPort)

        Thread.sleep(1000)
    }
}
