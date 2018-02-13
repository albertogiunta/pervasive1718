@file:Suppress("UNUSED_PARAMETER")

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
    open fun onConnect(session: Session) {
        log.printStatusMessage("session opened on remote ${session.remote}")
    }

    @OnWebSocketClose
    open fun onClose(session: Session, statusCode: Int, reason: String) {
        log.printStatusMessage("session onClose on remote ${session.remote} | exit code $statusCode | info: $reason")
    }

    /**
     * This Method is called when the WS receives a Message from a client to the web-socket.
     *
     */
    @OnWebSocketMessage
    @Throws(IOException::class)
    open fun onMessage(session: Session, message: String) {
        log.printIncomingMessage(message)
    }

    /**
     * This is the stub method to call when relaying a onMessage to/through the web-socket to the client
     *
     */
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