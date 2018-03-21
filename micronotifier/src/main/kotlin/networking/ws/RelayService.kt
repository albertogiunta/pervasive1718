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
 * A WS class which relays and publish Messages
 *
 */
@WebSocket
class RelayService : WSServer<Payload<WSOperations, String>>(name = Services.NOTIFIER.wsPath) , Observable{

    private val observers = mutableListOf<Observer>()

//    private val wsSubject = PublishSubject.create<Pair<Session, String>>()

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
        Logger.info("[ ${wsUser.name} | ${this.name} *** ] session open on remote ${session.remote.inetSocketAddress}")
    }

    override fun onClose(session: Session, statusCode: Int, reason: String) {
        Logger.info("[ ${wsUser.name} | ${this.name} *** ] session onClose on remote | exit code $statusCode")
        this.notify(session)
    }

    override fun onMessage(session: Session, message: String) {
        Logger.info("[ ${wsUser.name} | $name --> ] $message")
        this.notify(session to message)
    }

    @OnWebSocketError
    fun onError(session : Session, error : Throwable) {
        Logger.error("[${wsUser.name} | $name --- Error] @ ${session.remote}", error)
    }

    companion object {

        /**
         * This is the stub method to call when relaying a onMessage to/through the web-socket to the client
         *
         */
        fun <P> sendMessage(wsUser: WSLogger.WSUser, wsName: String, session: Session, payload: P) {
            try {
                sendMessage(wsUser, wsName, session, payload.asJson())
            } catch (e: WebSocketException) {
                println(e.message)
            }
        }

        fun sendMessage(wsUser: WSLogger.WSUser, wsName: String, session: Session, message: String) {
            Logger.info("[ ${wsUser.name} | $wsName <-- ] $message")
            session.remote.sendString(message)
        }
    }
}
