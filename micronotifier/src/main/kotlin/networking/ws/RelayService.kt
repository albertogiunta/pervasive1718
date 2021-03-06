package networking.ws

import WSLogger
import WSServer
import config.Services
import controller.CoreController
import model.Payload
import model.WSOperations
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import patterns.Observable
import patterns.Observer
import utils.Logger
import utils.asJson

/**
 * A class implementing a WS that receives messages from
 * clients and notify such messages to the CoreController
 *
 * @author XanderC
 *
 */
@WebSocket
class RelayService : WSServer<Payload<WSOperations, String>>(name = Services.NOTIFIER.wsPath) , Observable{

    private val observers = mutableListOf<Observer>()

    init {
        this.addObserver(CoreController.singleton())
    }

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {}

    override fun notify(obj: Any) {
        observers.parallelStream().forEach { it.update(obj) }
    }

    override fun onConnect(session: Session) {
        Logger.info("[ ${wsUser.name} | $name@${this.javaClass.simpleName} *** ] session open on remote ${session.remote.inetSocketAddress}")
    }

    override fun onClose(session: Session, statusCode: Int, reason: String) {
        Logger.info("[ ${wsUser.name} | $name@${this.javaClass.simpleName} *** ] session onClose on remote | exit code $statusCode")
        this.notify(session)
    }

    override fun onMessage(session: Session, message: String) {
        Logger.info("[ ${wsUser.name} | $name@${this.javaClass.simpleName} --> ] received message: $message")
        this.notify(session to message)
    }

    @OnWebSocketError
    fun onError(session : Session, error : Throwable) {
        Logger.error("[${wsUser.name} | $name@${this.javaClass.simpleName} --- Error] @ ${session.remote}", error)
    }

    /**
     * Since the reference of the WS class is handled by SparkJava
     * and as such not callable from code,
     * this companion object wraps some stubs methods
     * to handle replies from the WS to the clients
     *
     */
    companion object {

        /**
         * This is the stub method to call when relaying a onMessage to/through the web-socket to the client
         *
         */
        fun <P> sendMessage(wsUser: WSLogger.WSUser, wsName: String, session: Session, payload: P, logEnabled: Boolean = true) {
            try {
                if (logEnabled) {
                    Logger.info("[ ${wsUser.name} | $wsName <-- ] ${payload.toString()}")
                }
                sendMessage(session, payload.asJson())
            } catch (e: WebSocketException) {
                println(e.message)
            }
        }

        fun sendMessage(session: Session, message: String) {
            session.remote.sendString(message)
        }
    }
}
