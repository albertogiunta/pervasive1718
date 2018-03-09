@file:Suppress("UNUSED_PARAMETER")

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark
import spark.kotlin.port
import utils.asJson
import java.io.IOException

@Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
@WebSocket
open class WSServer<in P>(val name: String = "WS", val wsUser: WSLogger.WSUser = WSLogger.WSUser.SERVER) {

    private val log = WSLogger(wsUser, name)

    @OnWebSocketConnect
    open fun onConnect(session: Session) {
        log.printStatusMessage("session opened on remote ${session.remote}")
    }

    @OnWebSocketClose
    open fun onClose(session: Session, statusCode: Int, reason: String) {
        log.printStatusMessage("session onClose on remote | exit code")
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
        try { sendMessage(session, payload.asJson()) } catch (e : WebSocketException) { println(e.message) }
    }

    private fun sendMessage(session: Session, message: String) {
        log.printOutgoingMessage(message)
        session.remote.sendString(message)
    }
}

class WSLogger(private val user: WSUser, private val wsName: String) {

    enum class WSUser(name: String) {
        SERVER("SERVER"),
        CLIENT("CLIENT")
    }

    fun printStatusMessage(message: String) = println("[ ${user.name} | $wsName *** ] $message")

    fun printIncomingMessage(message: String) = println("[ ${user.name} | $wsName <-- ] $message")

    fun printOutgoingMessage(message: String) = println("[ ${user.name} | $wsName --> ] $message")
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