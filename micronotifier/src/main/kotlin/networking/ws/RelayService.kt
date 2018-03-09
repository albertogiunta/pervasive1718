package networking.ws

import WSLogger
import WSServer
import config.Services
import controller.CoreController
import io.reactivex.subjects.Subject
import model.Payload
import model.PayloadWrapper
import model.WSOperations
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import utils.Logger
import utils.asJson
import utils.toJson

/**
 * A WS class which relays and publish Messages
 *
 */
@WebSocket
class RelayService : WSServer<Payload<WSOperations, String>>(Services.NOTIFIER.wsPath) {

    private val core = CoreController.singleton()

    private val wsSubject: Subject<Pair<Session, String>>

    init {
        wsSubject = core.subjects.getSubjectsOf(RelayService::class.java.name)!!
    }

    override fun onConnect(session: Session) {
        Logger.info("[ ${wsUser.name} | ${this.name} *** ] session open on remote ${session.remote.inetSocketAddress}")
    }

    override fun onClose(session: Session, statusCode: Int, reason: String) {
        Logger.info("[ ${wsUser.name} | ${this.name} *** ] session onClose on remote | exit code $statusCode")
        if (core.sessions.has(session)) {
            val message = PayloadWrapper(-1, WSOperations.CLOSE,
                    core.sessions.getOn(session)!!.toJson()).toJson()
            wsSubject.onNext(Pair(session, message))
        }
    }

    override fun onMessage(session: Session, message: String) {
        Logger.info("[ ${wsUser.name} | $name --> ] $message")
        wsSubject.onNext(session to message)
    }

    @OnWebSocketError
    fun onError(session : Session, error : Throwable) {
        Logger.error("[WS Error !] @ ${session.remote}", error)
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
