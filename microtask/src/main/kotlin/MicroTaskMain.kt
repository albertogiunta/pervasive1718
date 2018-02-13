import networking.WSSessionServer
import networking.WSTaskServer
import spark.kotlin.ignite

object MicroTaskMain {
    @JvmStatic
    fun main(args: Array<String>) {
        MicroTask.init(WSParams.WS_SESSION_PORT, WSParams.WS_TASK_PORT)
    }
}

object MicroTask {

    fun init(sessionPort: Int, taskPort: Int) {

        with(ignite()) {
            port(sessionPort)
            service.webSocket(WSParams.WS_PATH_SESSION, WSSessionServer::class.java)
            service.init()
        }

        Thread.sleep(1000)

        with(ignite()) {
            port(taskPort)
            service.webSocket(WSParams.WS_PATH_TASK, WSTaskServer::class.java)
            service.init()
        }

        Thread.sleep(1000)
    }

}