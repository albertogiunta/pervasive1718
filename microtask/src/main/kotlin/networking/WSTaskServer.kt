package networking

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark.init
import spark.Spark.webSocket
import spark.kotlin.port
import utils.WSParams.TASK_ROOT_PATH
import utils.WSParams.WS_PORT
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

@Suppress("unused", "UNUSED_PARAMETER")
@WebSocket
class WSTaskServer {

    private val sessions = ConcurrentLinkedQueue<Session>()

    @OnWebSocketConnect
    fun connected(session: Session) {
        sessions.add(session)
    }

    @OnWebSocketClose
    fun closed(session: Session, statusCode: Int, reason: String) {
        sessions.remove(session)
    }

    @OnWebSocketMessage
    @Throws(IOException::class)
    fun message(session: Session, message: String) {
        println("Got: " + message)
        session.remote.sendString(message)
    }

}

fun main(args: Array<String>) {
    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    val userUsernameMap = ConcurrentHashMap<String, String>()
    val nextUserNumber = 1 //Used for creating the next username

    port(WS_PORT)
    webSocket(TASK_ROOT_PATH, WSTaskServer::class.java)
    init()
}