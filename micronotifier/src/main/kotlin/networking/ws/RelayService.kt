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
class RelayService : WSServer<Payload<WSOperations, String>>() {

    private val core = CoreController.singleton()

    private val coreSubject: Subject<Pair<Session, String>>


    init {
        coreSubject = core.subjects.getSubjectsOf(CoreController::class.java.name)!!
    }

    override fun onClose(session: Session, statusCode: Int, reason: String) {
        super.onClose(session, statusCode, reason)
        if (core.sessions.has(session)) {
            val message = PayloadWrapper(-1, WSOperations.CLOSE,
                    core.sessions.getOn(session)!!.toJson()).toJson()
            coreSubject.onNext(Pair(session, message))
        }
    }

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)
        coreSubject.onNext(Pair(session, message))
    }

    @OnWebSocketError
    fun onError(session : Session, error : Throwable) {
        Logger.error("[WS Error] @ ${session.remote}", error)
    }
}
