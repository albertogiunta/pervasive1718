import networking.WSTaskServer
import spark.kotlin.ignite


object MicroTaskBootstrap {

    fun init(taskPort: Int) {

        with(ignite()) {
            port(taskPort)
            service.webSocket(WSParams.WS_PATH_TASK, WSTaskServer::class.java)
            service.init()
        }

        Thread.sleep(1000)
    }
}
