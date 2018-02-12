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

    private val coreSubject: Subject<Pair<Session, String>> =
            core.subjects.getSubjectsOf(CoreController::class.java.name)!!

    private val gson = GsonBuilder().create()

    init {
        Logger.info(core.topics.activeTopics().toString())

        val publishSubjects = core.topics.activeTopics().map {
            it to core.subjects.createNewSubjectFor<String>(it.toString())
        }.toMap()
    }

    override fun onMessage(session: Session, message: String) {
        super.onMessage(session, message)
        coreSubject.onNext(Pair(session, message))
    }
}

fun main(args: Array<String>) {

    NotifierTopicsController.init(LifeParameters.values().toSet())
    WSServerInitializer.init(RelayService::class.java, WSParams.WS_NOTIFIER_PORT, WSParams.WS_PATH_NOTIFIER)
}
