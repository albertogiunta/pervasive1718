package networking.ws

import WSServer
import controller.CoreController
import io.reactivex.subjects.Subject
import model.Payload
import model.PayloadWrapper
import model.WSOperations
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import utils.Logger
import utils.toJson

/**
 * A WS class which
 *
 */
@WebSocket
class RelayService : WSServer<Payload<WSOperations, String>>("Notifier") {

    private val core = CoreController.singleton()

    private val wsSubject: Subject<Pair<Session, String>>


    init {
        wsSubject = core.subjects.getSubjectsOf(this::class.java.name)!!
    }

    override fun onClose(session: Session, statusCode: Int, reason: String) {
        super.onClose(session, statusCode, reason)
        if (core.sessions.has(session)) {
            val message = PayloadWrapper(-1, WSOperations.CLOSE,
                    core.sessions.getOn(session)!!.toJson()).toJson()
            wsSubject.onNext(Pair(session, message))
        }
    }

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)
        wsSubject.onNext(Pair(session, message))
    }

    @OnWebSocketError
    fun onError(session : Session, error : Throwable) {
        Logger.error("[WS Error] @ ${session.remote}", error)
    }
}
