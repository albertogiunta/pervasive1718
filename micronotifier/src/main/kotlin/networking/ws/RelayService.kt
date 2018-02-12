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
import model.SessionOperation
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import utils.Logger

/**
 * A WS class which
 *
 */
@WebSocket
class RelayService : WSServer<Payload<SessionOperation, String>>() {

    private val core = CoreController.singleton()

    private val channel: Subject<Pair<Session, String>> = core.subjects.getSubjectsOf(core.toString())!!

    private val gson = GsonBuilder().create()

    init {
        Logger.info(core.topics.activeTopics().toString())

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.createNewSubjectFor<String>(it.toString())
        }.toMap()
    }

    override fun closed(session: Session, statusCode: Int, reason: String) {
        super.closed(session, statusCode, reason)
        core.sessions.removeListenerOn(session)
    }

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)
        channel.onNext(Pair(session, message))
    }
}

fun main(args: Array<String>) {

    NotifierTopicsController.init(LifeParameters.values().toSet())
    WSServerInitializer.init(RelayService::class.java, WSParams.WS_SESSION_PORT, WSParams.WS_PATH_NOTIFIER)
}