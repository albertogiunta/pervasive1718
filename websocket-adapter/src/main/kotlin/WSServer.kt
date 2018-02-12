import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark
import spark.kotlin.port
import java.io.IOException

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@WebSocket
open class WSServer<in P> {

    private val log = WSLogger(WSLogger.WSUser.SERVER)

    @OnWebSocketConnect
    open fun connected(session: Session) {
        log.printStatusMessage("session opened on remote ${session.remote}")
    }

    @OnWebSocketClose
    open fun closed(session: Session, statusCode: Int, reason: String) {
        log.printStatusMessage("session closed on remote ${session.remote} | exit code $statusCode | info: $reason")
    }

    @OnWebSocketMessage
    @Throws(IOException::class)
    open fun message(session: Session, message: String) {
        log.printIncomingMessage(message)
    }

    open fun sendMessage(session: Session, payload: P) {
        sendMessage(session, payload.toJson())
    }

    private fun sendMessage(session: Session, message: String) {
        log.printOutgoingMessage(message)
        session.remote.sendString(message)
    }
}

class WSLogger(private val user: WSUser) {

    enum class WSUser(name: String) {
        SERVER("SERVER"),
        CLIENT("CLIENT")
    }

    fun printStatusMessage(message: String) = println("[ ${user.name} | *** ] $message")

    fun printIncomingMessage(message: String) = println("[ ${user.name} | <-- ] $message")

    fun printOutgoingMessage(message: String) = println("[ ${user.name} | --> ] $message")
}

object WSServerInitializer {
    fun init(serverClazz: Class<out WSServer<*>>, wsPort: Int = WSParams.WS_TASK_PORT, wsPath: String) {
        port(wsPort)
        Spark.webSocket(wsPath, serverClazz)
        Spark.init()
    }
}

fun main(args: Array<String>) {
    WSServerInitializer.init(serverClazz = WSServer::class.java, wsPath = WSParams.WS_PATH_TASK)
}