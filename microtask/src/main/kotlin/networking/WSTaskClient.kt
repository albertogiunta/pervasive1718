package networking

import JSONClass
import logic.Member
import logic.Task
import logic.ontologies.Operation
import logic.ontologies.Status
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import toJson
import utils.WSParams.HOST
import utils.WSParams.TASK_ROOT_PATH
import utils.WSParams.WS_PORT
import java.net.URI
import java.sql.Timestamp
import java.util.*

@Suppress("unused", "UNUSED_PARAMETER")
class WSTaskClient(serverURI: URI) : WebSocketClient(serverURI) {

    override fun onOpen(handshakeData: ServerHandshake) {
        println("new connection opened")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        println("closed with exit code $code additional info: $reason")
    }

    override fun onMessage(message: String) {
        println("received message: " + message)
    }

    override fun onError(ex: Exception) {
        System.err.println("an error occurred:" + ex)
    }
}

fun main(args: Array<String>) {
    val client = WSTaskClient(URI("$HOST$WS_PORT$TASK_ROOT_PATH"))
    client.connect()
}