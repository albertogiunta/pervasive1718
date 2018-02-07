import org.eclipse.jetty.websocket.api.annotations.WebSocket

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@WebSocket
class WSTaskServer : WSServer<String>() {

//    init {
//        ServerControllerImpl.init(this)
//    }
//
//    private val serverController: ServerController = ServerControllerImpl.INSTANCE


}

fun main(args: Array<String>) {
    WSServerInitializer.init(serverClazz = WSTaskServer::class.java, wsPath = WSParams.WS_PATH_TASK)
}