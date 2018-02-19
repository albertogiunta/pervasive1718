import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

@Suppress("unused", "UNUSED_PARAMETER")
open class WSClient(serverURI: URI) : WebSocketClient(serverURI) {

    private val log = WSLogger(WSLogger.WSUser.CLIENT)

    override fun onOpen(handshakeData: ServerHandshake) {
        log.printStatusMessage("session opened")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log.printStatusMessage("session onClose | exit code $code | info: $reason")
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

object WSClientInitializer {

    fun <T : WSClient> init(client: T): T {
        client.connectBlocking()
        return client
    }
}

fun main(args: Array<String>) {
    WSClientInitializer.init(WSClient(URIFactory.getDefaultURI()))
}