import networking.WSTaskServer


object MicroTaskBootstrap {

    fun init(taskPort: Int) {

        WSServerInitializer.init(WSTaskServer::class.java, wsPath = WSParams.WS_PATH_TASK, wsPort = taskPort)

        Thread.sleep(1000)
    }
}
