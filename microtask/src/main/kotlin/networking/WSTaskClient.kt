package networking

import logic.ServerControllerImpl.Companion.HOST
import logic.ServerControllerImpl.Companion.TASK_ROOT_PATH
import logic.ServerControllerImpl.Companion.WS_PORT
import logic.WSLogger
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

@Suppress("unused", "UNUSED_PARAMETER")
class WSTaskClient(serverURI: URI) : WebSocketClient(serverURI) {

    private val log = WSLogger(WSLogger.WSUser.CLIENT)

    override fun onOpen(handshakeData: ServerHandshake) {
        log.printStatusMessage("session opened")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log.printStatusMessage("session closed | exit code $code | info: $reason")
    }

    override fun onMessage(message: String) {
        log.printIncomingMessage(message)
    }

    fun sendMessage(message: String) {
        log.printOutgoingMessage(message)
        send(message)
    }

    override fun onError(ex: Exception) {
        log.printStatusMessage("error occurred: $ex")
    }
}

fun main(args: Array<String>) {
    val client = WSTaskClient(URI("$HOST$WS_PORT$TASK_ROOT_PATH"))
    client.connect()
}