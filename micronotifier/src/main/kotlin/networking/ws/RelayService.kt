package networking.ws

import LifeParameters
import WSParams
import WSServer
import WSServerInitializer
import com.google.gson.GsonBuilder
import controller.CoreController
import controller.NotifierTopicsController
import io.reactivex.subjects.Subject
import model.Payload
import model.PayloadWrapper
import model.SessionOperation
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import toJson
import utils.Logger

/**
 * A WS class which
 *
 */
@WebSocket
class RelayService : WSServer<Payload<SessionOperation, String>>() {

    private val core = CoreController.singleton()

    private val coreSubject: Subject<Pair<Session, String>> =
            core.subjects.getSubjectsOf(CoreController::class.java.name)!!

    private val gson = GsonBuilder().create()

    init {
        Logger.info(core.topics.activeTopics().toString())

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.createNewSubjectFor<String>(it.toString())
        }.toMap()
    }

    override fun onClose(session: Session, statusCode: Int, reason: String) {
        super.onClose(session, statusCode, reason)
        if (core.sessions.has(session)) {
            val message = PayloadWrapper(-1L, SessionOperation.CLOSE,
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

fun main(args: Array<String>) {

    NotifierTopicsController.init(LifeParameters.values().toSet())
    WSServerInitializer.init(RelayService::class.java, WSParams.WS_NOTIFIER_PORT, WSParams.WS_PATH_NOTIFIER)
}
