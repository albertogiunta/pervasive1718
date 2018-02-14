import networking.WSTaskServer
import spark.kotlin.ignite

object MicroTaskMain {
    @JvmStatic
    fun main(args: Array<String>) {
        MicroTask.init(WSParams.WS_TASK_PORT)
    }
}

object MicroTask {

    fun init(taskPort: Int) {

        with(ignite()) {
            port(taskPort)
            service.webSocket(WSParams.WS_PATH_TASK, WSTaskServer::class.java)
            service.init()
        }

        Thread.sleep(1000)
    }

}